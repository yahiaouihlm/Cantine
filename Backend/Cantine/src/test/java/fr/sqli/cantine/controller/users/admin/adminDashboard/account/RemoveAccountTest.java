package fr.sqli.cantine.controller.users.admin.adminDashboard.account;

import fr.sqli.cantine.controller.AbstractContainerConfig;
import fr.sqli.cantine.controller.AbstractLoginRequest;
import fr.sqli.cantine.dao.*;
import fr.sqli.cantine.entity.UserEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;


@SpringBootTest
@AutoConfigureMockMvc

public class RemoveAccountTest extends AbstractContainerConfig implements IAdminTest {

    private final String paramReq = "?" + "adminUuid" + "=";

    private IUserDao adminDao;
    private IFunctionDao iFunctionDao;
    private IConfirmationTokenDao iConfirmationTokenDao;
    private MockMvc mockMvc;
    private UserEntity adminEntity;
    private String authorizationToken;
    @Autowired
    private IUserDao iStudentDao;
    @Autowired
    private IStudentClassDao iStudentClassDao;


    @Autowired
    public RemoveAccountTest(MockMvc mockMvc, IUserDao adminDao, IFunctionDao functionDao, IConfirmationTokenDao iConfirmationTokenDao) throws Exception {
        this.mockMvc = mockMvc;
        this.adminDao = adminDao;
        this.iFunctionDao = functionDao;
        this.iConfirmationTokenDao = iConfirmationTokenDao;

        cleanDB();
        initDB();
    }

    void cleanDB() {
        this.iConfirmationTokenDao.deleteAll();// remove  all confirmationtokenEntity  to  keep  the  database  Integrity
        this.adminDao.deleteAll();
        this.iFunctionDao.deleteAll();
    }

    void initDB() throws Exception {

        this.adminEntity = AbstractLoginRequest.saveAdmin(this.adminDao, this.iFunctionDao);
        this.authorizationToken = AbstractLoginRequest.getAdminBearerToken(this.mockMvc);
    }


    @Test
    void removeAdminAccountWithValidateAdminUuid() throws Exception {
        var adminUuid = this.adminEntity.getId();

        var result = this.mockMvc.perform(MockMvcRequestBuilders.post(ADMIN_DISABLE_ACCOUNT
                        + paramReq + adminUuid)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(super.responseMessage(responseMap.get("AdminDisabledSuccessfully"))));

        var admin = this.adminDao.findById(adminUuid);
        Assertions.assertTrue(admin.isPresent());

        Assertions.assertEquals(0, admin.get().getStatus());
        Assertions.assertNotNull(admin.get().getDisableDate());
    }


    @Test
    void removeAdminAccountWithStudentToken() throws Exception {
        this.iStudentDao.deleteAll();
        this.iStudentClassDao.deleteAll();

        AbstractLoginRequest.saveAStudent(this.iStudentDao, this.iStudentClassDao);
        var studentAuthorizationToken = AbstractLoginRequest.getStudentBearerToken(this.mockMvc);

        var result = this.mockMvc.perform(MockMvcRequestBuilders.post(ADMIN_DISABLE_ACCOUNT
                        + paramReq + this.adminEntity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, studentAuthorizationToken)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isForbidden());
    }


    /*****************************  TESTS FOR  ID ADMIN  ********************************/

    @Test
    void removeAdminAccountWithAdminNotFound() throws Exception {
        var adminUuid = java.util.UUID.randomUUID().toString();

        var result = this.mockMvc.perform(MockMvcRequestBuilders.post(ADMIN_DISABLE_ACCOUNT
                        + paramReq + adminUuid)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("AdminNotFound"))));
    }

    @Test
    void removeAdminAccountWithInvalidIdAdmin() throws Exception {

        var result = this.mockMvc.perform(MockMvcRequestBuilders.post(ADMIN_DISABLE_ACCOUNT
                        + paramReq + "jjedh5")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidUuId"))));
    }

    @Test
    void removeAdminAccountWithEmptyIdAdmin() throws Exception {

        var result = this.mockMvc.perform(MockMvcRequestBuilders.post(ADMIN_DISABLE_ACCOUNT
                        + paramReq + "")
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidUuId"))));
    }

    @Test
    void removeAdminAccountWithInvalidToken() throws Exception {

        var result = this.mockMvc.perform(MockMvcRequestBuilders.put(ADMIN_DISABLE_ACCOUNT + paramReq)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "invalidToken")
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }


    @Test
    void removeAdminAccountWithToken() throws Exception {

        var result = this.mockMvc.perform(MockMvcRequestBuilders.put(ADMIN_DISABLE_ACCOUNT + paramReq)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }


}
