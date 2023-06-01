package fr.sqli.Cantine.controller.admin.adminDashboard.account;

import fr.sqli.Cantine.controller.admin.AbstractContainerConfig;
import fr.sqli.Cantine.dao.IAdminDao;
import fr.sqli.Cantine.dao.IConfirmationTokenDao;
import fr.sqli.Cantine.dao.IFunctionDao;
import fr.sqli.Cantine.entity.FunctionEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class SendTokenTest extends AbstractContainerConfig implements IAdminTest {

    final String EMAIL_TO_TEST = "halim.yahiaoui@social.aston-ecole.com";
    final  String paramReq = "?" + "email" + "=";
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
        this.iConfirmationTokenDao.deleteAll();
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

    @BeforeEach
    void  init (){
        this.cleanDb();
        this.initDb();
    }




    @Test
    void  sendTokenWithValidEmail () throws Exception {
        var result = this.mockMvc.perform(MockMvcRequestBuilders
                .post(ADMIN_SEND_TOKEN_URL + paramReq + EMAIL_TO_TEST));
        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers
                        .content()
                        .string(TOKEN_SENDED_SUCCESSFULLY)
                          );
         var  token = this.iConfirmationTokenDao.findAll().get(0);
         var admin =  token.getAdmin();
         var  adminFromDb = this.adminDao.findById(admin.getId());
         Assertions.assertTrue(adminFromDb.isPresent());
        Assertions.assertTrue(adminFromDb.get().getStatus()== 0); // 0 =  the  account is  not  validated
        Assertions.assertTrue(adminFromDb.get().getValidation() == 0);  // 0 =  the  account is  not  validated by the  sueper  admin
    }

    @Test
    void  sendTokenWithWrongEmail () throws Exception {
        var result = this.mockMvc.perform(MockMvcRequestBuilders
                .post(ADMIN_SEND_TOKEN_URL + paramReq + "wrongEmai"));


        result.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers
                        .content()
                        .json(super.exceptionMessage(exceptionsMap.get("AdminNotFound"))));

    }


    @Test
    void  sendTokenWithNullEmail () throws Exception {
        var result = this.mockMvc.perform(MockMvcRequestBuilders
                .post(ADMIN_SEND_TOKEN_URL + paramReq + null ));


        result.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers
                        .content()
                        .json(super.exceptionMessage(exceptionsMap.get("AdminNotFound"))));

    }

    @Test
    void  sendTokenWithOutEmail () throws Exception {
        var result = this.mockMvc.perform(MockMvcRequestBuilders
                                  .post(ADMIN_SEND_TOKEN_URL + paramReq));


        result.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers
                        .content()
                        .json(super.exceptionMessage(exceptionsMap.get("AdminNotFound"))));

    }

}
