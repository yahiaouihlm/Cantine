package fr.sqli.Cantine.controller.admin.adminDashboard;

import fr.sqli.Cantine.controller.admin.AbstractContainerConfig;
import fr.sqli.Cantine.dao.IAdminDao;
import fr.sqli.Cantine.dao.IConfirmationTokenDao;
import fr.sqli.Cantine.dao.IFunctionDao;
import fr.sqli.Cantine.entity.FunctionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class SendToken extends AbstractContainerConfig implements IAdminTest {

    final String EMAIL_TO_TEST = "halim.yahiaoui@social.aston-ecole.com";
    @Autowired
    private IFunctionDao iFunctionDao ;
    @Autowired
    private IAdminDao adminDao;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private Environment environment;

    @Autowired
    private IConfirmationTokenDao iConfirmationTokenDao;
    void cleanDb() {
        this.adminDao.deleteAll();
        this.adminDao.deleteAll();
        this.iFunctionDao.deleteAll();
    }

    void initDb () {
        FunctionEntity function  =  new FunctionEntity();
        function.setName("manager");
        this.iFunctionDao.save(function);
        var admin  = IAdminTest.createAdminWith(EMAIL_TO_TEST, function);
        this.adminDao.save(admin);
    }






}
