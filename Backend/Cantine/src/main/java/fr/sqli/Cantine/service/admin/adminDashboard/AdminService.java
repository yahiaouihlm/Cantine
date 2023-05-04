package fr.sqli.Cantine.service.admin.adminDashboard;


import fr.sqli.Cantine.dao.AdminDao;
import fr.sqli.Cantine.dao.IFunctionDao;
import fr.sqli.Cantine.dto.in.person.AdminDtoIn;
import fr.sqli.Cantine.entity.AdminEntity;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.ExistingAdminException;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.InvalidPersonInformationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminService implements  IAdminDashboard {

    final  String ADMIN_IMAGE_NAME ;
    final  String EMAIL_ADMIN_DOMAIN ;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private IFunctionDao functionDao;
    private AdminDao adminDao;
    private Environment environment;
        @Autowired
        public AdminService(AdminDao adminDao , IFunctionDao functionDao
                ,Environment environment
                , BCryptPasswordEncoder bCryptPasswordEncoder){
            this.adminDao = adminDao;
            this.functionDao = functionDao;
            this.bCryptPasswordEncoder = bCryptPasswordEncoder;
            this.ADMIN_IMAGE_NAME = environment.getProperty("sqli.cantine.default.user.imagename");
            this.EMAIL_ADMIN_DOMAIN = environment.getProperty("sqli.cantine.admin.email.domain");

        }


    @Override
    public void signUp(AdminDtoIn adminDtoIn, String function) throws InvalidPersonInformationException, ExistingAdminException {
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

        // check If Image Exsit
        /*TODO  :  image  processing   */

    }

    @Override
    public void exstingAdmin(String  adminEmail ) throws ExistingAdminException {
          if  (this.adminDao.findByEmail(adminEmail).isPresent()){
              throw  new ExistingAdminException("THIS ADMIN IS ALREADY EXISTING");
          }
    }
}
