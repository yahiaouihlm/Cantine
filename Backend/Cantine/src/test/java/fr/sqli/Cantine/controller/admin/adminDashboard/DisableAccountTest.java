package fr.sqli.Cantine.controller.admin.adminDashboard;

import fr.sqli.Cantine.controller.admin.AbstractContainerConfig;
import fr.sqli.Cantine.dao.IAdminDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

@SpringBootTest
@AutoConfigureMockMvc
public class DisableAccountTest  extends AbstractContainerConfig implements IAdminTest {
    @Autowired
    private IAdminDao adminDao;

    @Autowired
    private  AdminController adminController;

    @Autowired
    private Environment environment;

    void initDB () {

    }


}
