package fr.sqli.Cantine.service.admin.adminDashboard;


import fr.sqli.Cantine.dao.AdminDao;
import fr.sqli.Cantine.dao.IFunctionDao;
import fr.sqli.Cantine.dto.in.person.AdminDtoIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Admin  implements  IAdminDashboard {

    private IFunctionDao functionDao;
    private AdminDao adminDao;

        @Autowired
        public Admin(AdminDao adminDao , IFunctionDao functionDao){
            this.adminDao = adminDao;
            this.functionDao = functionDao;
        }


    @Override
    public void signUp(AdminDtoIn adminDtoIn, String function) {

    }
}
