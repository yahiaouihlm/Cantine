package fr.sqli.Cantine.service.admin.registration;


import fr.sqli.Cantine.dao.AdminDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SignUp {

     private AdminDao adminDao;

        @Autowired
        public SignUp(AdminDao adminDao){
            this.adminDao = adminDao;
        }





}
