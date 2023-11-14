package fr.sqli.cantine.service.users.admin.impl;


import fr.sqli.cantine.dao.IAdminDao;
import fr.sqli.cantine.dao.IConfirmationTokenDao;
import fr.sqli.cantine.dao.IFunctionDao;
import fr.sqli.cantine.dto.in.users.AdminDtoIn;
import fr.sqli.cantine.dto.out.person.AdminDtout;
import fr.sqli.cantine.dto.out.superAdmin.FunctionDtout;
import fr.sqli.cantine.entity.AdminEntity;
import fr.sqli.cantine.entity.ConfirmationTokenEntity;
import fr.sqli.cantine.entity.ImageEntity;
import fr.sqli.cantine.service.mailer.SendUserConfirmationEmail;

import fr.sqli.cantine.service.users.admin.IAdminService;
import fr.sqli.cantine.service.users.exceptions.ExpiredToken;
import fr.sqli.cantine.service.users.exceptions.*;
import fr.sqli.cantine.service.images.ImageService;
import fr.sqli.cantine.service.images.exception.ImagePathException;
import fr.sqli.cantine.service.images.exception.InvalidFormatImageException;
import fr.sqli.cantine.service.images.exception.InvalidImageException;
import fr.sqli.cantine.service.users.student.exceptions.AccountAlreadyActivatedException;
import jakarta.mail.MessagingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService implements IAdminService {
    private static final Logger LOG = LogManager.getLogger();
    final String SERVER_ADDRESS;
    final String DEFAULT_ADMIN_IMAGE_NAME;
    final String CONFIRMATION_TOKEN_URL;
    final String ADMIN_IMAGE_URL;

    final String ADMIN_IMAGE_PATH;  //  path  to  admin image  directory
    final String EMAIL_ADMIN_REGEX;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ImageService imageService;
    private final IFunctionDao functionDao;
    private final IAdminDao adminDao;
    private final IConfirmationTokenDao confirmationTokenDao;
    private final SendUserConfirmationEmail sendUserConfirmationEmail;

    @Autowired
    public AdminService(IAdminDao adminDao, IFunctionDao functionDao, ImageService imageService
            , Environment environment
            , BCryptPasswordEncoder bCryptPasswordEncoder
            , IConfirmationTokenDao confirmationTokenDao
            , SendUserConfirmationEmail sendUserConfirmationEmail
    ) {
        this.sendUserConfirmationEmail = sendUserConfirmationEmail;
        this.confirmationTokenDao = confirmationTokenDao;
        this.imageService = imageService;
        this.adminDao = adminDao;
        this.functionDao = functionDao;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.DEFAULT_ADMIN_IMAGE_NAME = environment.getProperty("sqli.cantine.default.persons.admin.imagename"); //  default  image  name  for  admin
        this.ADMIN_IMAGE_PATH = environment.getProperty("sqli.cantine.image.admin.path"); //  path  to  admin image  directory
        this.ADMIN_IMAGE_URL = environment.getProperty("sqli.cantine.images.url.admin"); //  url  to  admin image  directory
        this.EMAIL_ADMIN_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        var protocol = environment.getProperty("sqli.cantine.server.protocol");
        var host = environment.getProperty("sqli.cantine.server.ip.address");
        var port = environment.getProperty("sali.cantine.server.port");
        this.CONFIRMATION_TOKEN_URL = environment.getProperty("sqli.cantine.server.confirmation.token.url");
        this.SERVER_ADDRESS = protocol + host + ":" + port;
    }


    @Override
    public void checkLinkValidity(String token) throws InvalidTokenException, TokenNotFoundException, ExpiredToken, UserNotFoundException {
        if (token == null || token.trim().isEmpty()) {
            AdminService.LOG.error("INVALID TOKEN  IN CHECK  LINK  VALIDITY");
            throw new InvalidTokenException("INVALID TOKEN");
        }

        var confirmationTokenEntity = this.confirmationTokenDao.findByToken(token).orElseThrow(
                () -> {
                    AdminService.LOG.error("TOKEN  NOT  FOUND  IN CHECK  LINK  VALIDITY : token = {}", token);
                    return new TokenNotFoundException("INVALID TOKEN");
                }); //  token  not  found

        var adminEntity = confirmationTokenEntity.getAdmin();
        if (adminEntity == null) {
            AdminService.LOG.error("ADMIN  NOT  FOUND  IN CHECK  LINK  VALIDITY WITH  token = {}", token);
            throw new InvalidTokenException("INVALID TOKEN"); //  token  not  found
        }


        var expiredTime = System.currentTimeMillis() - confirmationTokenEntity.getCreatedDate().getTime();
        long fiveMinutesInMillis = 5 * 60 * 1000; // 5 minutes en millisecondes
        //  expired  token  ///
        if (expiredTime > fiveMinutesInMillis) {
            this.confirmationTokenDao.delete(confirmationTokenEntity);
            AdminService.LOG.error("EXPIRED TOKEN  IN CHECK  LINK  VALIDITY WITH  token = {}", token);
            throw new ExpiredToken("EXPIRED TOKEN");
        }

        var admin = this.adminDao.findById(adminEntity.getId()).orElseThrow(
                () -> {
                    AdminService.LOG.error("ADMIN  NOT  FOUND  IN CHECK  LINK  VALIDITY WITH  token = {}", token);
                    return new UserNotFoundException("ADMIN NOT FOUND");
                }
        );
        //  change  admin  status  to  activated
        admin.setStatus(1);
        this.adminDao.save(admin);

    }

    @Override
    public void sendConfirmationLink(String email) throws UserNotFoundException, RemovedAccountException, AccountAlreadyActivatedException, MessagingException {
        if (email == null || email.isEmpty() || email.isBlank()) {
            AdminService.LOG.error("INVALID EMAIL TO SEND  CONFIRMATION LINK");
            throw new UserNotFoundException("INVALID EMAIL");
        }

        var admin = this.adminDao.findByEmail(email).orElseThrow(() -> {
            AdminService.LOG.error("ADMIN  WITH  EMAIL  {} IS  NOT  FOUND TO SEND  CONFIRMATION LINK", email);
            return new UserNotFoundException("ADMIN NOT FOUND");
        });

        // account already  removed
        if (admin.getDisableDate() != null) {
            AdminService.LOG.error("ACCOUNT  ALREADY  REMOVED WITH  EMAIL  {} ", email);
            throw new RemovedAccountException("ACCOUNT  ALREADY  REMOVED");
        }
        // account already  activated
        if (admin.getStatus() == 1) {
            AdminService.LOG.error("ACCOUNT  ALREADY  ACTIVATED WITH  EMAIL  {} ", email);
            throw new AccountAlreadyActivatedException("ACCOUNT  ALREADY  ACTIVATED");
        }

        var confirmationTokenEntity = this.confirmationTokenDao.findByAdmin(admin);
        confirmationTokenEntity.ifPresent(this.confirmationTokenDao::delete);

        ConfirmationTokenEntity confirmationToken = new ConfirmationTokenEntity(admin);
        this.confirmationTokenDao.save(confirmationToken);


        var url = this.SERVER_ADDRESS + this.CONFIRMATION_TOKEN_URL + confirmationToken.getToken();

        this.sendUserConfirmationEmail.sendConfirmationLink(admin, url);
    }

    @Override
    public void signUp(AdminDtoIn adminDtoIn) throws InvalidUserInformationException, ExistingUserException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, AdminFunctionNotFoundException, UserNotFoundException, MessagingException, AccountAlreadyActivatedException, RemovedAccountException {

        if (adminDtoIn == null)
            throw new InvalidUserInformationException("INVALID INFORMATION REQUEST");

        adminDtoIn.checkAdminInformationsValidity();

        //check  function  validity
        var functionAdmin = adminDtoIn.getFunction();


        var functionAdminEntity = this.functionDao.findByName(functionAdmin).orElseThrow(() -> {
            AdminService.LOG.error(" ADMIN  FUNCTION  {} IS  NOT  FOUND TO SIGN  UP", functionAdmin);
            return new AdminFunctionNotFoundException("YOUR FUNCTIONALITY IS NOT FOUND");
        });


        //check  email  validity
        if (!adminDtoIn.getEmail().matches(this.EMAIL_ADMIN_REGEX)) {
            AdminService.LOG.error("INVALID EMAIL FORMAT TO SIGN  UP  : {}", adminDtoIn.getEmail());
            throw new InvalidUserInformationException("INVALID EMAIL");
        }

        //check  if  admin  is  already  existing by  email
        this.existingAdmin(adminDtoIn.getEmail());

        ImageEntity imageEntity = new ImageEntity();

        if (adminDtoIn.getImage() != null && !adminDtoIn.getImage().isEmpty()) {
            MultipartFile image = adminDtoIn.getImage();
            var imageName = this.imageService.uploadImage(image, ADMIN_IMAGE_PATH);
            imageEntity.setImagename(imageName);

        } else {
            imageEntity.setImagename(this.DEFAULT_ADMIN_IMAGE_NAME);
        }

        AdminEntity adminEntity = new AdminEntity();
        adminEntity.setFirstname(adminDtoIn.getFirstname());
        adminEntity.setLastname(adminDtoIn.getLastname());
        adminEntity.setBirthdate(adminDtoIn.getBirthdate());
        adminEntity.setEmail(adminDtoIn.getEmail());
        adminEntity.setPassword(this.bCryptPasswordEncoder.encode(adminDtoIn.getPassword()));
        adminEntity.setAddress(adminDtoIn.getAddress().trim());
        adminEntity.setPhone(adminDtoIn.getPhone().trim());
        adminEntity.setTown(adminDtoIn.getTown().trim());
        adminEntity.setFunction(functionAdminEntity);
        adminEntity.setStatus(0);
        adminEntity.setImage(imageEntity);
        adminEntity.setRegistrationDate(LocalDate.now());
        adminEntity.setValidation(0);

        // save admin
        this.adminDao.save(adminEntity);

        this.sendConfirmationLink(adminDtoIn.getEmail()); //  send  confirmation Link for  email
    }


    @Override
    public void disableAdminAccount(String adminUuid) throws UserNotFoundException, InvalidUserInformationException {

        IAdminService.checkUuIdValidity(adminUuid);

        var admin = this.adminDao.findByUuid(adminUuid).orElseThrow(
                () -> {
                    AdminService.LOG.error("ADMIN  NOT  FOUND  IN DISABLE  ADMIN  ACCOUNT  WITH  UUID = {}", adminUuid);
                    return new UserNotFoundException("ADMIN NOT FOUND");
                }
        );

        admin.setStatus(0);
        admin.setDisableDate(LocalDate.now());
        this.adminDao.save(admin);
    }

    @Override
    public AdminDtout getAdminByUuID(String adminUuid) throws InvalidUserInformationException, UserNotFoundException {
        IAdminService.checkUuIdValidity(adminUuid);

        var admin = this.adminDao.findByUuid(adminUuid).orElseThrow(
                () -> {
                    AdminService.LOG.error("ADMIN  NOT  FOUND  IN GET  ADMIN  BY  UUID  WITH  UUID = {}", adminUuid);
                    return new UserNotFoundException("ADMIN NOT FOUND");
                }
        );

        return new AdminDtout(admin, this.ADMIN_IMAGE_URL);
    }

    @Override
    public void updateAdminInfo(AdminDtoIn adminDtoIn) throws InvalidUserInformationException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, AdminFunctionNotFoundException, UserNotFoundException {
        if (adminDtoIn == null) {
            AdminService.LOG.error("INVALID INFORMATION REQUEST adminDtoIn IS  NULL IN  updateAdminInfo");
            throw new InvalidUserInformationException("INVALID INFORMATION REQUEST");
        }

        if (adminDtoIn.getEmail() != null || adminDtoIn.getPassword() != null) {
            AdminService.LOG.error("INVALID INFORMATION REQUEST THE  EMAIL AND  PASSWORD  MUST BE  EXCLUDED IN  updateAdminInfo");
            throw new InvalidUserInformationException("INVALID INFORMATION REQUEST THE  EMAIL AND  PASSWORD  MUST BE  EXCLUDED");
        }

        adminDtoIn.checkInformationValidityExceptEmailAndPassword(); //  check  information  validity
        adminDtoIn.checkAddressValidity(); //  check  address  validity
        var functionAdmin = adminDtoIn.getFunction();

        var functionAdminEntity = this.functionDao.findByName(functionAdmin.trim());

        if (functionAdminEntity.isEmpty()) {
            AdminService.LOG.error("function  is  not  valid");
            throw new AdminFunctionNotFoundException("YOUR FUNCTIONALITY IS NOT FOUND");
        }


        var adminEntity = this.adminDao.findByUuid(adminDtoIn.getUuid()).orElseThrow(
                () -> new UserNotFoundException("ADMIN NOT FOUND")
        );
        adminEntity.setAddress(adminDtoIn.getAddress().trim());
        adminEntity.setFirstname(adminDtoIn.getFirstname().trim());
        adminEntity.setBirthdate(adminDtoIn.getBirthdate());
        adminEntity.setLastname(adminDtoIn.getLastname().trim());
        adminEntity.setPhone(adminDtoIn.getPhone().trim());
        adminEntity.setFunction(functionAdminEntity.get());
        adminEntity.setTown(adminDtoIn.getTown().trim());
        adminEntity.setAddress(adminDtoIn.getAddress().trim());


        if (adminDtoIn.getImage() != null && !adminDtoIn.getImage().isEmpty()) {
            MultipartFile image = adminDtoIn.getImage();
            String imageName;
            // check  if the image  to  delete  is  the  default image  or  not
            if (adminEntity.getImage().getImagename().equals(DEFAULT_ADMIN_IMAGE_NAME)) {
                imageName = this.imageService.uploadImage(image, ADMIN_IMAGE_PATH);
                AdminService.LOG.info("image  is  uploaded");
            } else {
                var oldImageName = adminEntity.getImage().getImagename();
                imageName = this.imageService.updateImage(oldImageName, image, ADMIN_IMAGE_PATH);
            }
            ImageEntity imageEntity = new ImageEntity();
            imageEntity.setImagename(imageName);
            adminEntity.setImage(imageEntity);

        }

        this.adminDao.save(adminEntity);

    }


    @Override
    public List<FunctionDtout> getAllAdminFunctions() {
        return this.functionDao.findAll().stream().map(FunctionDtout::new).collect(Collectors.toList());
    }


    @Override
    public void existingAdmin(String adminEmail) throws ExistingUserException {
        if (this.adminDao.findByEmail(adminEmail).isPresent()) {
            throw new ExistingUserException("THIS ADMIN IS ALREADY EXISTS");
        }
    }
}