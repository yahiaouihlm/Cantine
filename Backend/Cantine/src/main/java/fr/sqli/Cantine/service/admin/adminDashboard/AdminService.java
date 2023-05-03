package fr.sqli.Cantine.service.admin.adminDashboard;


import fr.sqli.Cantine.dao.AdminDao;
import fr.sqli.Cantine.dao.IFunctionDao;
import fr.sqli.Cantine.dto.in.person.AdminDtoIn;
import fr.sqli.Cantine.entity.AdminEntity;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.InvalidPersonInformationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminService implements  IAdminDashboard {

    final  String ADMIN_IMAGE_NAME ;
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
        }


    @Override
    public void signUp(AdminDtoIn adminDtoIn, String function) throws InvalidPersonInformationException {
        AdminEntity adminEntity = adminDtoIn.toAdminEntityWithOutFunction();

        //check  function  validity
        var  functionAdmin =  adminDtoIn.getFunction();
        if  (this.functionDao.findByName(functionAdmin).isEmpty()){
            throw  new InvalidPersonInformationException(" YOUR FUNCTIONALITY IS NOT VALID");
        }


    }
}
