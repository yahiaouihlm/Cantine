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
public class DisableAccountTest  extends AbstractContainerConfig implements IAdminTest {

    private  final  String  paramReq = "?" + "idAdmin" + "=" ;
    @Autowired
    private IAdminDao adminDao;

    @Autowired
    private  AdminController adminController;

    @Autowired
    private IFunctionDao iFunctionDao;

    @Autowired
    private Environment environment;

    @Autowired
    private MockMvc mockMvc;
    private AdminEntity adminEntity;
    void  cleanDB() {
      this.adminDao.deleteAll();
    }

    void initDB () {
        FunctionEntity function = new FunctionEntity();
        function.setName("Manager");
        function =   this.iFunctionDao.save(function);
        this.adminEntity = IAdminTest.createAdminWith("yahiaoui@social.aston-ecole.com", function);
        this.adminEntity = this.adminDao.save(this.adminEntity);
    }

    @BeforeEach
    void init () {
        this.cleanDB();
        this.initDB();
    }

    /*****************************  TESTS FOR  ID ADMIN  ********************************/
    @Test
    void disableAdminAccountWithAdminNotFound() throws Exception {
        var idAdmin = this.adminDao.findAll().stream().map(AdminEntity::getId)
                .max(Integer::compareTo).get() + 1;

        var result = this.mockMvc.perform(MockMvcRequestBuilders.put(ADMIN_DISABLE_ACCOUNT
                        + paramReq + String.valueOf(idAdmin) )
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("AdminNotFound"))));
    }


    @Test
    void disableAdminAccountWithDoubleIdAdmin () throws Exception {

        var result = this.mockMvc.perform(MockMvcRequestBuilders.put(ADMIN_DISABLE_ACCOUNT
                        + paramReq + "1.5" )
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidParam"))));
    }

    @Test
    void disableAdminAccountWithNegativeIdAdmin () throws Exception {

        var result = this.mockMvc.perform(MockMvcRequestBuilders.put(ADMIN_DISABLE_ACCOUNT
                        + paramReq + "-5" )
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidId"))));
    }

    @Test
    void disableAdminAccountWithInvalidIdAdmin () throws Exception {

        var result = this.mockMvc.perform(MockMvcRequestBuilders.put(ADMIN_DISABLE_ACCOUNT
                        + paramReq + "jjedh5" )
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidParam"))));
    }
    @Test
    void  disableAdminAccountWithNullIdAdmin () throws Exception {

        var result = this.mockMvc.perform(MockMvcRequestBuilders.put(ADMIN_DISABLE_ACCOUNT
                        + paramReq + null )
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidParam"))));
    }

    @Test
    void disableAdminAccountWithEmptyIdAdmin() throws Exception {

        var result = this.mockMvc.perform(MockMvcRequestBuilders.put(ADMIN_DISABLE_ACCOUNT
                        + paramReq +"" )
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("MissingPram"))));
    }

    @Test
    void disableAdminAccountWithOutIdAdmin () throws Exception {

        var result = this.mockMvc.perform(MockMvcRequestBuilders.put(ADMIN_DISABLE_ACCOUNT + paramReq)
                .contentType(MediaType.APPLICATION_JSON));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content()
                        .json(super.exceptionMessage(exceptionsMap.get("MissingPram"))));
    }


}
