package fr.sqli.Cantine.controller.admin.adminDashboard;

import fr.sqli.Cantine.controller.admin.AbstractContainerConfig;
import fr.sqli.Cantine.dao.IAdminDao;
import fr.sqli.Cantine.dao.IFunctionDao;
import fr.sqli.Cantine.entity.AdminEntity;
import fr.sqli.Cantine.entity.FunctionEntity;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class GetAdmin  extends AbstractContainerConfig implements  IAdminTest {
    @Autowired
    private IFunctionDao functionDao;

  @Autowired
  private IAdminDao adminDao;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private Environment environment;

    private AdminEntity adminEntity1;
    private AdminEntity adminEntity2;
    @BeforeEach
    void initDb (){
        FunctionEntity functionEntity = new  FunctionEntity();
        functionEntity.setName("Manager");
        functionEntity = functionDao.save(functionEntity);

        this.adminEntity1 = IAdminTest.createAdminWith("halim@social.aston-ecole.com",functionEntity )  ;
        this.adminEntity1 = IAdminTest.createAdminWith("yahiaoui@social.aston-ecole.com",functionEntity )  ;
        this.adminEntity1 = adminDao.save(this.adminEntity1);
        this.adminEntity2 = adminDao.save(this.adminEntity2);


    }


}
