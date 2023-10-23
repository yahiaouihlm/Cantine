package fr.sqli.cantine.service.admin.account;


import fr.sqli.cantine.dao.IAdminDao;
import fr.sqli.cantine.dao.IConfirmationTokenDao;
import fr.sqli.cantine.dao.IFunctionDao;
import fr.sqli.cantine.dto.in.person.AdminDtoIn;
import fr.sqli.cantine.dto.out.person.AdminDtout;
import fr.sqli.cantine.dto.out.superAdmin.FunctionDtout;
import fr.sqli.cantine.entity.AdminEntity;
import fr.sqli.cantine.entity.ImageEntity;
import fr.sqli.cantine.service.admin.adminDashboard.exceptions.*;
import fr.sqli.cantine.service.admin.exceptions.AdminFunctionNotFoundException;
import fr.sqli.cantine.service.admin.exceptions.AdminNotFound;
import fr.sqli.cantine.service.admin.exceptions.ExistingAdminException;
import fr.sqli.cantine.service.admin.exceptions.InvalidPersonInformationException;
import fr.sqli.cantine.service.images.ImageService;
import fr.sqli.cantine.service.images.exception.ImagePathException;
import fr.sqli.cantine.service.images.exception.InvalidFormatImageException;
import fr.sqli.cantine.service.images.exception.InvalidImageException;
import fr.sqli.cantine.service.mailer.EmailSenderService;
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


 /*   @Override
    public String checkTokenValidity(String token) throws AdminNotFound, InvalidTokenException, ExpiredToken {
        if (token == null || token.trim().isEmpty())
            throw new InvalidTokenException("INVALID TOKEN");

        var confirmationTokenEntity = this.confirmationTokenDao.findByToken(token).orElseThrow(
                () -> new InvalidTokenException("INVALID TOKEN")); //  token  not  found

        var adminEntity = confirmationTokenEntity.getAdmin();
        if (adminEntity == null)
            throw new InvalidTokenException("INVALID TOKEN"); //  admin  not  found

        var expiredTime = System.currentTimeMillis() - confirmationTokenEntity.getCreatedDate().getTime();
        long fiveMinutesInMillis = 5 * 60 * 1000; // 5 minutes en millisecondes

        //  expired  token  ///
        if (expiredTime > fiveMinutesInMillis) {
            this.confirmationTokenDao.delete(confirmationTokenEntity);
            throw new ExpiredToken("EXPIRED TOKEN");
        }
        var admin = this.adminDao.findById(adminEntity.getId());
        if (admin.isEmpty())
            throw new AdminNotFound("ADMIN NOT FOUND");

        admin.get().setStatus(1);
        this.adminDao.save(admin.get());

        return "ok";
    }
*/

    @Override
    public void disableAdminAccount(Integer idAdmin) throws InvalidPersonInformationException, AdminNotFound {

        IAdminService.checkIDValidity(idAdmin); //  check  id  validity

        var adminEntity = this.adminDao.findById(idAdmin).orElseThrow(
                () -> new AdminNotFound("ADMIN NOT FOUND")
        );

        adminEntity.setStatus(0);
        adminEntity.setDisableDate(LocalDate.now());
        this.adminDao.save(adminEntity);
    }

    @Override
    public AdminDtout getAdminById(Integer idAdmin) throws InvalidPersonInformationException, AdminNotFound {
        IAdminService.checkIDValidity(idAdmin); //  check  id  validity

        var adminEntity = this.adminDao.findById(idAdmin).orElseThrow(
                () -> new AdminNotFound("ADMIN NOT FOUND")
        );

        return new AdminDtout(adminEntity , this.ADMIN_IMAGE_URL );
    }

    @Override
    public void updateAdminInfo(AdminDtoIn adminDtoIn) throws InvalidPersonInformationException, InvalidFormatImageException, InvalidImageException,ImagePathException, IOException, AdminNotFound, AdminFunctionNotFoundException {
        if (adminDtoIn == null)
            throw new InvalidPersonInformationException("INVALID INFORMATION REQUEST");

        if (adminDtoIn.getEmail() != null || adminDtoIn.getPassword() != null)
            throw new InvalidPersonInformationException("INVALID INFORMATION REQUEST THE  EMAIL AND  PASSWORD  MUST BE  EXCLUDED");
        var idAdmin = adminDtoIn.getId();
        IAdminService.checkIDValidity(idAdmin); //  check  id  validity

        adminDtoIn.checkInformationValidityExceptEmailAndPassword(); //  check  information  validity
        adminDtoIn.checkAddressValidity(); //  check  address  validity
        var functionAdmin = adminDtoIn.getFunction();

        var functionAdminEntity = this.functionDao.findByName(functionAdmin.trim());

        if (functionAdminEntity.isEmpty()) {
            AdminService.LOG.error("function  is  not  valid");
            throw new AdminFunctionNotFoundException("YOUR FUNCTIONALITY IS NOT FOUND");
        }


        var adminEntity = this.adminDao.findById(idAdmin).orElseThrow(
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
    public AdminEntity signUp(AdminDtoIn adminDtoIn) throws InvalidPersonInformationException, ExistingAdminException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, AdminFunctionNotFoundException {
        if (adminDtoIn == null)
            throw new InvalidPersonInformationException("INVALID INFORMATION REQUEST");

        AdminEntity adminEntity = adminDtoIn.toAdminEntityWithOutFunction();

        //check  function  validity
        var functionAdmin = adminDtoIn.getFunction();
        System.out.println(functionAdmin);
        var functionAdminEntity = this.functionDao.findByName(functionAdmin.trim());
        if (functionAdminEntity.isEmpty()) {
            AdminService.LOG.error("function  is  not  found");
            throw new AdminFunctionNotFoundException("YOUR FUNCTIONALITY IS NOT FOUND");
        }

        adminEntity.setFunction(functionAdminEntity.get());
        //check  email  validity
        if (!adminEntity.getEmail().matches(this.EMAIL_ADMIN_REGEX)) {
            AdminService.LOG.error("email  is  not  valid");
            throw new InvalidPersonInformationException("YOUR EMAIL IS NOT VALID");
        }

        //check  if  admin  is  already  existing by  email
        this.existingAdmin(adminEntity.getEmail());

        adminEntity.setStatus(0);
        adminEntity.setDisableDate(LocalDate.now());  // any  account  should  be  disabled  by  default in creation
        adminEntity.setValidation(0);
        // passorwd  encoding

        adminEntity.setPassword(this.bCryptPasswordEncoder.encode(adminEntity.getPassword()));


        if (adminDtoIn.getImage() != null && !adminDtoIn.getImage().isEmpty()) {
            MultipartFile image = adminDtoIn.getImage();
            var imageName = this.imageService.uploadImage(image, ADMIN_IMAGE_PATH);
            ImageEntity imageEntity = new ImageEntity();
            imageEntity.setImagename(imageName);
            adminEntity.setImage(imageEntity);
        } else {
            ImageEntity imageEntity = new ImageEntity();
            imageEntity.setImagename(this.DEFAULT_ADMIN_IMAGE_NAME);
            adminEntity.setImage(imageEntity);
        }
        adminEntity.setRegistrationDate(LocalDate.now());
        return this.adminDao.save(adminEntity);
    }

    /*
    public void sendToken(String email) throws AdminNotFound, InvalidPersonInformationException, MessagingException, AccountAlreadyActivatedException {

        if (email == null || email.trim().isEmpty()) {
            AdminService.LOG.error("email  is  not  valid");
            throw new InvalidPersonInformationException("YOUR EMAIL IS NOT VALID");
        }

        var adminEntity = this.adminDao.findByEmail(email.trim()).orElseThrow(
                () -> new AdminNotFound("ADMIN NOT FOUND")
        );
        if (adminEntity.getStatus() == 1) {
            throw new AccountAlreadyActivatedException("YOUR ACCOUNT IS ALREADY ENABLED");
        }
        // if  there is    token  in database  delete  it mapped with    this  admin
        var confirmationTokenEntity = this.confirmationTokenDao.findByAdmin(adminEntity);
        confirmationTokenEntity.ifPresent(tokenEntity -> this.confirmationTokenDao.delete(tokenEntity));

        ConfirmationTokenEntity confirmationToken = new ConfirmationTokenEntity(adminEntity);
        this.confirmationTokenDao.save(confirmationToken);

        var url = this.SERVER_ADDRESS + this.CONFIRMATION_TOKEN_URL + confirmationToken.getToken();


        String text = """
                     <!DOCTYPE html>
                     <html lang="en">
                     <head>
                         <meta charset="UTF-8">
                     </head>
                     <body>
                         <h1>Confirmation d'inscription</h1>
                           <p> Bonjour 
                           """
                           + adminEntity.getFirstname() +"  " + adminEntity.getLastname() +
                         """ 
                         </p>
                         <P>
                             Merci de cliquer sur le lien ci-dessous pour confirmer votre adresse Email et activer votre compte.
                            
                         </P>
                           <p> Nous  vous  Remercions  Votre  Compréhention </p>
                             <p> Cordialement </p>
                                   
                     </body>
                     </html>
                """ + url;


        this.emailSenderService.send(email, "Complete Registration!", text);
    }
*/
    @Override
    public List<FunctionDtout> getAllAdminFunctions() {
        return this.functionDao.findAll().stream().map(FunctionDtout::new).collect(Collectors.toList());
    }


    public void tokenValidation(String token) throws InvalidPersonInformationException {
        IAdminService.validationArgument(token);
    }


    @Override
    public void existingAdmin(String adminEmail) throws ExistingAdminException {
        if (this.adminDao.findByEmail(adminEmail).isPresent()) {
            throw new ExistingAdminException("THIS ADMIN IS ALREADY EXISTS");
        }
    }
}
