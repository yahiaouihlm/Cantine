package fr.sqli.Cantine.controller.admin.adminDashboard;

import fr.sqli.Cantine.controller.admin.AbstractContainerConfig;
import fr.sqli.Cantine.dao.IAdminDao;
import fr.sqli.Cantine.dao.IFunctionDao;
import fr.sqli.Cantine.entity.AdminEntity;
import fr.sqli.Cantine.entity.FunctionEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class GetAdmin  extends AbstractContainerConfig implements  IAdminTest {

    final  String paramReq = "?" + "idAdmin" + "=";
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

    @Test
    void getAdminByIWithOutIdAdmin () throws Exception {

        var result = this.mockMvc.perform(MockMvcRequestBuilders.get(ADMIN_DASH_BOARD_GET_ADMIN_BY_ID_ENDPOINT
                + paramReq )
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidId"))));
    }
}
