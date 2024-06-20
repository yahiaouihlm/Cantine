package fr.sqli.cantine.controller.users.admin.adminDashboard.account;

import fr.sqli.cantine.controller.AbstractContainerConfig;
import fr.sqli.cantine.dao.IAdminDao;
import fr.sqli.cantine.dao.IConfirmationTokenDao;
import fr.sqli.cantine.dao.IFunctionDao;
import fr.sqli.cantine.entity.AdminEntity;
import fr.sqli.cantine.entity.FunctionEntity;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class GetAdminTest extends AbstractContainerConfig implements  IAdminTest {

    final  String paramReq = "?" + "idAdmin" + "=";
    @Autowired
    private IFunctionDao functionDao;

    @Autowired
    private IConfirmationTokenDao iConfirmationTokenDao;
  @Autowired
  private IAdminDao adminDao;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private Environment environment;

    private AdminEntity adminEntity1;
    private AdminEntity adminEntity2;


    void cleanUpDb(){
        this.iConfirmationTokenDao.deleteAll();// remove  all confirmationtokenEntity  to  keep  the  database  Integrity
        this.adminDao.deleteAll();
        this.functionDao.deleteAll();
    }
    @BeforeEach
    void initDb (){
       cleanUpDb();

        FunctionEntity functionEntity = new  FunctionEntity();
        functionEntity.setName("Manager");
        functionEntity = functionDao.save(functionEntity);

        this.adminEntity1 = IAdminTest.createAdminWith("halim@social.aston-ecole.com",functionEntity ,  IAdminTest.createImageEntity())  ;
        this.adminEntity2 = IAdminTest.createAdminWith("yahiaoui@social.aston-ecole.com",functionEntity ,  IAdminTest.createImageEntity() )  ;
        this.adminEntity1 = adminDao.save(this.adminEntity1);
        this.adminEntity2 = adminDao.save(this.adminEntity2);


    }
    @Test
   void  getAdminByIdTest () throws Exception {
        var  idAdmin = this.adminEntity1.getId();

       var result = this.mockMvc.perform(MockMvcRequestBuilders.get(GET_ADMIN_BY_ID
                       + paramReq + idAdmin)
               .contentType(MediaType.APPLICATION_JSON)
               .accept(MediaType.APPLICATION_JSON));

         result.andExpect(MockMvcResultMatchers.status().isOk());
       result.andExpect(MockMvcResultMatchers.jsonPath("id").value(CoreMatchers.is(idAdmin)));
        result.andExpect(MockMvcResultMatchers.jsonPath("firstname").value(CoreMatchers.is(this.adminEntity1.getFirstname())));
        result.andExpect(MockMvcResultMatchers.jsonPath("lastname").value(CoreMatchers.is(this.adminEntity1.getLastname())));
        result.andExpect(MockMvcResultMatchers.jsonPath("email").value(CoreMatchers.is(this.adminEntity1.getEmail())));
   }

  /*****************************  TESTS FOR  ID ADMIN  ********************************/
  @Test
  void getAdminByIDWithAdminNotFound() throws Exception {
    var idAdmin = this.adminDao.findAll().stream().map(AdminEntity::getId)
            .max(Integer::compareTo).get() + 1;

    var result = this.mockMvc.perform(MockMvcRequestBuilders.get(GET_ADMIN_BY_ID
                    + paramReq + idAdmin)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON));

    result.andExpect(MockMvcResultMatchers.status().isNotFound())
            .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("AdminNotFound"))));
  }


  @Test
  void getAdminByIDWithDoubleIdAdmin () throws Exception {

    var result = this.mockMvc.perform(MockMvcRequestBuilders.get(GET_ADMIN_BY_ID
                    + paramReq + "1.5" )
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON));

    result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
            .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidParam"))));
  }

  @Test
  void getAdminByIDWithNegativeIdAdmin () throws Exception {

    var result = this.mockMvc.perform(MockMvcRequestBuilders.get(GET_ADMIN_BY_ID
                    + paramReq + "-5" )
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON));

    result.andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidId"))));
  }

  @Test
  void getAdminByIDWithInvalidIdAdmin () throws Exception {

    var result = this.mockMvc.perform(MockMvcRequestBuilders.get(GET_ADMIN_BY_ID
                    + paramReq + "jjedh5" )
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON));

    result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
            .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidParam"))));
  }
  @Test
  void  getAdminByIDWithNullIdAdmin () throws Exception {

    var result = this.mockMvc.perform(MockMvcRequestBuilders.get(GET_ADMIN_BY_ID
                    + paramReq + null )
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON));

    result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
            .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidParam"))));
  }

  @Test
  void getAdminByIDWithEmptyIdAdmin() throws Exception {
    var result = this.mockMvc.perform(MockMvcRequestBuilders.get(GET_ADMIN_BY_ID
                    + paramReq +"" )
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON));


    result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
            .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("MissingPram"))));
  }
    @Test
    void getAdminByIDWithOutIdAdmin () throws Exception {
        var result = this.mockMvc.perform(MockMvcRequestBuilders.get(GET_ADMIN_BY_ID
                + paramReq )
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content()
                        .json(super.exceptionMessage(exceptionsMap.get("MissingPram"))));
    }
}
