package fr.sqli.Cantine.service.admin.adminDashboard;


import fr.sqli.Cantine.dao.AdminDao;
import fr.sqli.Cantine.dao.IFunctionDao;
import fr.sqli.Cantine.dto.in.person.AdminDtoIn;
import fr.sqli.Cantine.entity.AdminEntity;
import fr.sqli.Cantine.entity.ImageEntity;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.ExistingAdminException;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.InvalidPersonInformationException;
import fr.sqli.Cantine.service.images.ImageService;
import fr.sqli.Cantine.service.images.exception.ImagePathException;
import fr.sqli.Cantine.service.images.exception.InvalidFormatImageException;
import fr.sqli.Cantine.service.images.exception.InvalidImageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class AdminService implements IAdminDashboardService {

    final  String DEFAULT_ADMIN_IMAGE_NAME;
    final  String EMAIL_ADMIN_DOMAIN ;
    final  String ADMIN_IMAGE_PATH ;  //  path  to  admin image  directory
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
            this.DEFAULT_ADMIN_IMAGE_NAME = environment.getProperty("sqli.cantine.default.persons.admin.imagename");
            this.EMAIL_ADMIN_DOMAIN = environment.getProperty("sqli.cantine.admin.email.domain");
            this.ADMIN_IMAGE_PATH = environment.getProperty("sqli.cantine.image.admin.path");
        }


    @Override
    public AdminEntity signUp(AdminDtoIn adminDtoIn) throws InvalidPersonInformationException, ExistingAdminException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {
        AdminEntity adminEntity = adminDtoIn.toAdminEntityWithOutFunction();

        //check  function  validity
        var  functionAdmin =  adminDtoIn.getFunction();

        if  (this.functionDao.findByName(functionAdmin).isEmpty()){
            throw  new InvalidPersonInformationException(" YOUR FUNCTIONALITY IS NOT VALID");
        }

        //check  email  validity
        if (!adminEntity.getEmail().endsWith(EMAIL_ADMIN_DOMAIN) ){
            throw  new InvalidPersonInformationException(" YOUR EMAIL IS NOT VALID");
        }

        //check  if  admin  is  already  existing by  email
        this.exstingAdmin(adminEntity.getEmail());

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
        // check If Image Exsit
        /*TODO  :  image  processing   */
         return this.adminDao.save(adminEntity);
    }

    @Override
    public void exstingAdmin(String  adminEmail ) throws ExistingAdminException {
          if  (this.adminDao.findByEmail(adminEmail).isPresent()){
              throw  new ExistingAdminException("THIS ADMIN IS ALREADY EXISTING");
          }
    }
}
