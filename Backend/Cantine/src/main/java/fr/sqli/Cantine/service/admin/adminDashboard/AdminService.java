package fr.sqli.Cantine.service.admin.adminDashboard;


import fr.sqli.Cantine.dao.AdminDao;
import fr.sqli.Cantine.dao.IFunctionDao;
import fr.sqli.Cantine.dto.in.person.AdminDtoIn;
import fr.sqli.Cantine.entity.AdminEntity;
import fr.sqli.Cantine.entity.ImageEntity;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.AdminNotFound;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.ExistingAdminException;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.InvalidPersonInformationException;
import fr.sqli.Cantine.service.images.ImageService;
import fr.sqli.Cantine.service.images.exception.ImagePathException;
import fr.sqli.Cantine.service.images.exception.InvalidFormatImageException;
import fr.sqli.Cantine.service.images.exception.InvalidImageException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;

@Service
public class AdminService implements IAdminDashboardService {
    private static final Logger LOG = LogManager.getLogger();
    final  String DEFAULT_ADMIN_IMAGE_NAME;
    final  String EMAIL_ADMIN_DOMAIN ;
    final  String ADMIN_IMAGE_PATH ;  //  path  to  admin image  directory
    final String  EMAIL_ADMIN_REGEX ;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private ImageService imageService;
    private IFunctionDao functionDao;
    private AdminDao adminDao;
    private Environment environment;
        @Autowired
        public AdminService(AdminDao adminDao , IFunctionDao functionDao,  ImageService imageService
                ,Environment environment
                , BCryptPasswordEncoder bCryptPasswordEncoder){
            this.imageService = imageService;
            this.adminDao = adminDao;
            this.functionDao = functionDao;
            this.bCryptPasswordEncoder = bCryptPasswordEncoder;
            this.DEFAULT_ADMIN_IMAGE_NAME = environment.getProperty("sqli.cantine.default.persons.admin.imagename"); //  default  image  name  for  admin
            this.EMAIL_ADMIN_DOMAIN = environment.getProperty("sqli.cantine.admin.email.domain"); //  email  domain  for  admin
            this.ADMIN_IMAGE_PATH = environment.getProperty("sqli.cantine.image.admin.path"); //  path  to  admin image  directory
            this.EMAIL_ADMIN_REGEX  = "^[a-zA-Z0-9._-]+@"+EMAIL_ADMIN_DOMAIN+"$" ;
        }

    @Override
    public void updateAdmin(AdminDtoIn adminDtoIn,  Integer idAdmin) throws InvalidPersonInformationException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, AdminNotFound {
         IAdminDashboardService.checkIDValidity(idAdmin); //  check  id  validity

        adminDtoIn.checkInformationValidityExceptEmailAndPassword(); //  check  information  validity

        var  functionAdmin =  adminDtoIn.getFunction();

        var functionAdminEntity = this.functionDao.findByName(functionAdmin.trim());

        if  (functionAdminEntity.isEmpty()){
            AdminService.LOG.error("function  is  not  valid");
            throw  new InvalidPersonInformationException("YOUR FUNCTIONALITY IS NOT VALID");
        }


        var  adminEntity = this.adminDao.findById(idAdmin).orElseThrow(
                    ()-> new AdminNotFound("ADMIN NOT FOUND")
        );
        adminEntity.setAddress(adminDtoIn.getAddress());
        adminEntity.setFirstname(adminDtoIn.getFirstname());
        adminEntity.setBirthdate(adminDtoIn.getBirthdate());
        adminEntity.setLastname(adminDtoIn.getLastname());
        adminEntity.setPhone(adminDtoIn.getPhone());
        adminEntity.setFunction(functionAdminEntity.get());
        adminEntity.setTown(adminDtoIn.getTown());
        adminEntity.setAddress(adminDtoIn.getAddress());



        if  (adminDtoIn.getImage() != null && !adminDtoIn.getImage().isEmpty()) {
            MultipartFile image = adminDtoIn.getImage();
            var  imageName =  this.imageService.uploadImage(image, ADMIN_IMAGE_PATH );
            ImageEntity imageEntity = new ImageEntity();
            imageEntity.setImagename(imageName);
            adminEntity.setImage(imageEntity);
        }

        this.adminDao.save(adminEntity);

        }

    @Override
    public AdminEntity signUp(AdminDtoIn adminDtoIn) throws InvalidPersonInformationException, ExistingAdminException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {
        AdminEntity adminEntity = adminDtoIn.toAdminEntityWithOutFunction();

        //check  function  validity
        var  functionAdmin =  adminDtoIn.getFunction();
        var functionAdminEntity = this.functionDao.findByName(functionAdmin.trim());
        if  (functionAdminEntity.isEmpty()){
            AdminService.LOG.error("function  is  not  valid");
            throw  new InvalidPersonInformationException("YOUR FUNCTIONALITY IS NOT VALID");
        }

        adminEntity.setFunction(functionAdminEntity.get());
        //check  email  validity
        if (!adminEntity.getEmail().matches(this.EMAIL_ADMIN_REGEX ) ){
            AdminService.LOG.error("email  is  not  valid");
            throw  new InvalidPersonInformationException("YOUR EMAIL IS NOT VALID");
        }

        //check  if  admin  is  already  existing by  email
        this.existingAdmin(adminEntity.getEmail());

        adminEntity.setStatus(0);

        // passorwd  encoding

        adminEntity.setPassword(this.bCryptPasswordEncoder.encode(adminEntity.getPassword()));


        if  (adminDtoIn.getImage() != null && !adminDtoIn.getImage().isEmpty()) {
            MultipartFile image = adminDtoIn.getImage();
            var  imageName =  this.imageService.uploadImage(image, ADMIN_IMAGE_PATH );
            ImageEntity imageEntity = new ImageEntity();
            imageEntity.setImagename(imageName);
             adminEntity.setImage(imageEntity);
        }
        else{
            ImageEntity imageEntity = new ImageEntity();
            imageEntity.setImagename(this.DEFAULT_ADMIN_IMAGE_NAME);
            adminEntity.setImage(imageEntity);
        }
        adminEntity.setRegistrationDate(LocalDate.now());
         return this.adminDao.save(adminEntity);
    }

    @Override
    public void existingAdmin(String  adminEmail ) throws ExistingAdminException {
          if  (this.adminDao.findByEmail(adminEmail).isPresent()){
              throw  new ExistingAdminException("THIS ADMIN IS ALREADY EXISTS");
          }
    }
}
