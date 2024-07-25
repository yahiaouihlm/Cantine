package fr.sqli.cantine.service.users.admin.impl;


import fr.sqli.cantine.constants.ConstCantine;
import fr.sqli.cantine.dao.IFunctionDao;
import fr.sqli.cantine.dao.IRoleDao;
import fr.sqli.cantine.dao.IUserDao;
import fr.sqli.cantine.dto.in.users.AdminDtoIn;
import fr.sqli.cantine.dto.out.person.AdminDtout;
import fr.sqli.cantine.dto.out.superAdmin.FunctionDtout;
import fr.sqli.cantine.entity.ImageEntity;

import fr.sqli.cantine.entity.RoleEntity;
import fr.sqli.cantine.entity.UserEntity;
import fr.sqli.cantine.service.mailer.UserEmailSender;
import fr.sqli.cantine.service.users.user.impl.UserService;
import fr.sqli.cantine.service.users.admin.IAdminService;
import fr.sqli.cantine.service.users.exceptions.*;
import fr.sqli.cantine.service.images.ImageService;
import fr.sqli.cantine.service.images.exception.ImagePathException;
import fr.sqli.cantine.service.images.exception.InvalidFormatImageException;
import fr.sqli.cantine.service.images.exception.InvalidImageException;
import fr.sqli.cantine.service.users.exceptions.AccountActivatedException;
import jakarta.mail.MessagingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.management.relation.RoleNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static fr.sqli.cantine.constants.ConstCantine.ADMIN_ROLE_LABEL;

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
    private final UserService userService;
    private final IUserDao userDao;
    private  final IRoleDao roleDao;
    private UserEmailSender userEmailSender;

    @Autowired
    public AdminService(IUserDao iUserDao,IRoleDao roleDao ,IFunctionDao functionDao, ImageService imageService
            , Environment environment
            , BCryptPasswordEncoder bCryptPasswordEncoder
            , UserService userService
            , UserEmailSender userEmailSender
    ) {
        this.roleDao = roleDao;
        this.imageService = imageService;
        this.userDao = iUserDao;
        this.functionDao = functionDao;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userService = userService;
        this.userEmailSender = userEmailSender;
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
    public void signUp(AdminDtoIn adminDtoIn) throws InvalidUserInformationException, ExistingUserException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, AdminFunctionNotFoundException, UserNotFoundException, MessagingException, RemovedAccountException, AccountActivatedException {

        if (adminDtoIn == null)
            throw new InvalidUserInformationException("INVALID INFORMATION REQUEST");

        adminDtoIn.checkAdminInformationValidity();

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
        this.existingEmail(adminDtoIn.getEmail());

        ImageEntity imageEntity = new ImageEntity();

        if (adminDtoIn.getImage() != null && !adminDtoIn.getImage().isEmpty()) {
            MultipartFile image = adminDtoIn.getImage();
            var imageName = this.imageService.uploadImage(image, ADMIN_IMAGE_PATH);
            imageEntity.setName(imageName);

        } else {
            imageEntity.setName(this.DEFAULT_ADMIN_IMAGE_NAME);
        }

        UserEntity adminEntity = new UserEntity();
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
        adminEntity.setRoles(List.of(new RoleEntity(ADMIN_ROLE_LABEL, ConstCantine.STUDENT_ROLE_DESCRIPTION, adminEntity)));

        // save admin
        this.userDao.save(adminEntity);

        String URL = this.SERVER_ADDRESS + "/cantine/superAdmin/newAdmins";
        this.userService.sendConfirmationLink(adminDtoIn.getEmail());//  send  confirmation Link for  email
        this.userEmailSender.sendNotificationToSuperAdminAboutAdminRegistration(adminEntity, URL);
    }
    @Override
    public void removeAdminAccount(String adminUuid) throws UserNotFoundException, InvalidUserInformationException {

        IAdminService.checkUuIdValidity(adminUuid);

        var admin = this.userDao.findAdminById(adminUuid).orElseThrow(
                () -> {
                    AdminService.LOG.error("ADMIN  NOT  FOUND  IN DISABLE  ADMIN  ACCOUNT  WITH  UUID = {}", adminUuid);
                    return new UserNotFoundException("ADMIN NOT FOUND");
                }
        );

        admin.setStatus(0);
        admin.setDisableDate(LocalDate.now());
        this.userDao.save(admin);
    }
    @Override
    public AdminDtout getAdminByUuID(String adminUuid) throws InvalidUserInformationException, UserNotFoundException {
        IAdminService.checkUuIdValidity(adminUuid);

        var admin = this.userDao.findAdminById(adminUuid).orElseThrow(
                () -> {
                    AdminService.LOG.error("ADMIN  NOT  FOUND  IN GET  ADMIN  BY  UUID  WITH  UUID = {}", adminUuid);
                    return new UserNotFoundException("ADMIN NOT FOUND");
                }
        );
        if (admin.getStatus() != 1 || admin.getValidation() != 1) {
            AdminService.LOG.error("ADMIN FOUND BUT NOT  ACTIVE  OR  NOT  VALIDATED GET  ADMIN  BY  UUID  WITH  UUID = {} CAN  NOT  BE  EXECUTED", adminUuid);
            throw new InvalidUserInformationException("INVALID USER");
        }
        return new AdminDtout(admin, this.ADMIN_IMAGE_URL);
    }
    @Override
    public void updateAdminInfo(AdminDtoIn adminDtoIn) throws InvalidUserInformationException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, AdminFunctionNotFoundException, UserNotFoundException {
        if (adminDtoIn == null) {
            AdminService.LOG.error("INVALID INFORMATION REQUEST adminDtoIn IS  NULL IN  updateAdminInfo");
            throw new InvalidUserInformationException("INVALID INFORMATION REQUEST");
        }

        IAdminService.checkUuIdValidity(adminDtoIn.getId());

        if (adminDtoIn.getEmail() != null || adminDtoIn.getPassword() != null) {
            AdminService.LOG.error("INVALID INFORMATION REQUEST THE  EMAIL AND  PASSWORD  MUST BE  EXCLUDED IN  updateAdminInfo");
            throw new InvalidUserInformationException("INVALID INFORMATION REQUEST THE  EMAIL AND  PASSWORD  MUST BE  EXCLUDED");
        }

        adminDtoIn.checkInformationValidityExceptEmailAndPassword(); //  check  information  validity
        adminDtoIn.checkAddressValidity(); //  check  address  validity
        var functionAdmin = adminDtoIn.getFunction();

        var functionAdminEntity = this.functionDao.findByName(functionAdmin.trim());

        if (functionAdminEntity.isEmpty()) {
            AdminService.LOG.error("FUNCTION  OF ADMIN  IS  NOT  FOUND  IN  updateAdminInfo  WITH  FUNCTION = {}", functionAdmin);
            throw new AdminFunctionNotFoundException("YOUR FUNCTIONALITY IS NOT FOUND");
        }


        var adminEntity = this.userDao.findAdminById(adminDtoIn.getId()).orElseThrow(
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
            if (adminEntity.getImage().getName().equals(DEFAULT_ADMIN_IMAGE_NAME)) {
                imageName = this.imageService.uploadImage(image, ADMIN_IMAGE_PATH);
                AdminService.LOG.info("IMAGE  UPLOADED  SUCCESSFULLY  WITH  NAME = {}", imageName);
            } else {
                var oldImageName = adminEntity.getImage().getName();
                imageName = this.imageService.updateImage(oldImageName, image, ADMIN_IMAGE_PATH);
            }
            ImageEntity imageEntity = new ImageEntity();
            imageEntity.setName(imageName);
            adminEntity.setImage(imageEntity);

        }

        this.userDao.save(adminEntity);

    }
    @Override
    public List<FunctionDtout> getAllAdminFunctions() {
        return this.functionDao.findAll().stream().map(FunctionDtout::new).collect(Collectors.toList());
    }
    @Override
    public void existingEmail(String adminEmail) throws ExistingUserException {
        if (this.userDao.findUserByEmail(adminEmail).isPresent()) {
            throw new ExistingUserException("EMAIL IS ALREADY EXISTS");
        }
    }
    @Override
    public UserEntity findByUsername(String username) throws UserNotFoundException {
         return this.userDao.findAdminById(username)
                 .orElseThrow(() -> new UserNotFoundException("ADMIN NOT FOUND"));
    }



}

