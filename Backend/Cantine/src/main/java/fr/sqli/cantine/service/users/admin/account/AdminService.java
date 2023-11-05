package fr.sqli.cantine.service.users.admin.account;


import fr.sqli.cantine.dao.IAdminDao;
import fr.sqli.cantine.dao.IConfirmationTokenDao;
import fr.sqli.cantine.dao.IFunctionDao;
import fr.sqli.cantine.dto.in.users.AdminDtoIn;
import fr.sqli.cantine.dto.out.person.AdminDtout;
import fr.sqli.cantine.dto.out.superAdmin.FunctionDtout;
import fr.sqli.cantine.entity.AdminEntity;
import fr.sqli.cantine.entity.ImageEntity;
import fr.sqli.cantine.service.users.admin.exceptions.AdminFunctionNotFoundException;
import fr.sqli.cantine.service.users.admin.exceptions.AdminNotFound;
import fr.sqli.cantine.service.users.exceptions.ExistingUserException;
import fr.sqli.cantine.service.users.exceptions.InvalidUserInformationException;
import fr.sqli.cantine.service.images.ImageService;
import fr.sqli.cantine.service.images.exception.ImagePathException;
import fr.sqli.cantine.service.images.exception.InvalidFormatImageException;
import fr.sqli.cantine.service.images.exception.InvalidImageException;
import fr.sqli.cantine.service.mailer.EmailSenderService;
import fr.sqli.cantine.service.users.exceptions.UserNotFoundException;
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
    final  String  CONFIRMATION_TOKEN_URL;
    final String ADMIN_IMAGE_URL    ;

    final String ADMIN_IMAGE_PATH;  //  path  to  admin image  directory
    final String EMAIL_ADMIN_REGEX;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private ImageService imageService;
    private IFunctionDao functionDao;
    private IAdminDao adminDao;
    private IConfirmationTokenDao confirmationTokenDao;
    private EmailSenderService emailSenderService;
    private Environment environment;

    @Autowired
    public AdminService(IAdminDao adminDao, IFunctionDao functionDao, ImageService imageService
            , Environment environment
            , BCryptPasswordEncoder bCryptPasswordEncoder
            , IConfirmationTokenDao confirmationTokenDao
            , EmailSenderService emailSenderService
    ) {
        this.emailSenderService = emailSenderService;
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
    public void signUp(AdminDtoIn adminDtoIn) throws InvalidUserInformationException, ExistingUserException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, AdminFunctionNotFoundException {

        if (adminDtoIn == null)
            throw new InvalidUserInformationException("INVALID INFORMATION REQUEST");

        adminDtoIn.checkAdminInformationsValidity();

        //check  function  validity
        var functionAdmin = adminDtoIn.getFunction();


        var functionAdminEntity = this.functionDao.findByName(functionAdmin).orElseThrow(()->{
            AdminService.LOG.error(" ADMIN  FUNCTION  {} IS  NOT  FOUND TO SIGN  UP" , functionAdmin);
            return  new AdminFunctionNotFoundException("YOUR FUNCTIONALITY IS NOT FOUND");
        });



        //check  email  validity
        if (!adminDtoIn.getEmail().matches(this.EMAIL_ADMIN_REGEX)) {
            AdminService.LOG.error("INVALID EMAIL FORMAT TO SIGN  UP  : {}" , adminDtoIn.getEmail());
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
        adminEntity.setAddress(adminDtoIn.getAddress());
        adminEntity.setPhone(adminDtoIn.getPhone());
        adminEntity.setTown(adminDtoIn.getTown());
        adminEntity.setFunction(functionAdminEntity);
        adminEntity.setStatus(0);
        adminEntity.setImage(imageEntity);
        adminEntity.setRegistrationDate(LocalDate.now());
        adminEntity.setValidation(0);
        this.adminDao.save(adminEntity);
    }





    @Override
    public void disableAdminAccount(Integer idAdmin) throws InvalidUserInformationException, AdminNotFound {

        IAdminService.checkIDValidity(idAdmin); //  check  id  validity

        var adminEntity = this.adminDao.findById(idAdmin).orElseThrow(
                () -> new AdminNotFound("ADMIN NOT FOUND")
        );

        adminEntity.setStatus(0);
        adminEntity.setDisableDate(LocalDate.now());
        this.adminDao.save(adminEntity);
    }

    @Override
    public AdminDtout getAdminById(Integer idAdmin) throws InvalidUserInformationException, AdminNotFound {
        IAdminService.checkIDValidity(idAdmin); //  check  id  validity

        var adminEntity = this.adminDao.findById(idAdmin).orElseThrow(
                () -> new AdminNotFound("ADMIN NOT FOUND")
        );

        return new AdminDtout(adminEntity , this.ADMIN_IMAGE_URL );
    }

    @Override
    public void updateAdminInfo(AdminDtoIn adminDtoIn) throws InvalidUserInformationException, InvalidFormatImageException, InvalidImageException,ImagePathException, IOException, AdminNotFound, AdminFunctionNotFoundException {
        if (adminDtoIn == null)
            throw new InvalidUserInformationException("INVALID INFORMATION REQUEST");

        if (adminDtoIn.getEmail() != null || adminDtoIn.getPassword() != null)
            throw new InvalidUserInformationException("INVALID INFORMATION REQUEST THE  EMAIL AND  PASSWORD  MUST BE  EXCLUDED");
       /* var idAdmin = adminDtoIn.getId();
        IAdminService.checkIDValidity(idAdmin);
*/
        adminDtoIn.checkInformationValidityExceptEmailAndPassword(); //  check  information  validity
        adminDtoIn.checkAddressValidity(); //  check  address  validity
        var functionAdmin = adminDtoIn.getFunction();

        var functionAdminEntity = this.functionDao.findByName(functionAdmin.trim());

        if (functionAdminEntity.isEmpty()) {
            AdminService.LOG.error("function  is  not  valid");
            throw new AdminFunctionNotFoundException("YOUR FUNCTIONALITY IS NOT FOUND");
        }


        var adminEntity = this.adminDao.findById(0).orElseThrow(
                () -> new AdminNotFound("ADMIN NOT FOUND")
        );
        adminEntity.setAddress(adminDtoIn.getAddress());
        adminEntity.setFirstname(adminDtoIn.getFirstname());
        adminEntity.setBirthdate(adminDtoIn.getBirthdate());
        adminEntity.setLastname(adminDtoIn.getLastname());
        adminEntity.setPhone(adminDtoIn.getPhone());
        adminEntity.setFunction(functionAdminEntity.get());
        adminEntity.setTown(adminDtoIn.getTown());
        adminEntity.setAddress(adminDtoIn.getAddress());


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


    public void tokenValidation(String token) throws InvalidUserInformationException {
        IAdminService.validationArgument(token);
    }


    @Override
    public void existingAdmin(String adminEmail) throws ExistingUserException {
        if (this.adminDao.findByEmail(adminEmail).isPresent()) {
            throw new ExistingUserException("THIS ADMIN IS ALREADY EXISTS");
        }
    }
}
