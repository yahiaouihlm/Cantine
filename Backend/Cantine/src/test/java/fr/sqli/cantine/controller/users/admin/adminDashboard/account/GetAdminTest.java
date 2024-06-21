package fr.sqli.cantine.controller.users.admin.adminDashboard.account;

import fr.sqli.cantine.controller.AbstractContainerConfig;
import fr.sqli.cantine.controller.AbstractLoginRequest;
import fr.sqli.cantine.dao.*;
import fr.sqli.cantine.entity.AdminEntity;
import fr.sqli.cantine.entity.FunctionEntity;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class GetAdminTest extends AbstractContainerConfig implements IAdminTest {

    final String paramReq = "?" + "adminUuid" + "=";

    @Autowired
    private IConfirmationTokenDao iConfirmationTokenDao;
    @Autowired
    private Environment environment;
    private MockMvc mockMvc;
    private IAdminDao adminDao;
    private IFunctionDao functionDao;

    private  String authorizationToken;

    @Autowired
    private IStudentDao iStudentDao;

    @Autowired
    private IStudentClassDao iStudentClassDao;

    private AdminEntity adminEntity1;
    private AdminEntity adminEntity2;

    @Autowired
    public GetAdminTest(MockMvc mockMvc, IAdminDao adminDao, IFunctionDao functionDao, IConfirmationTokenDao iConfirmationTokenDao) throws Exception {
        this.mockMvc = mockMvc;
        this.adminDao = adminDao;
        this.functionDao = functionDao;
        this.iConfirmationTokenDao = iConfirmationTokenDao;
        cleanUpDb();
        initDb();
    }


    void cleanUpDb() {
        this.iConfirmationTokenDao.deleteAll();
        this.adminDao.deleteAll();
        this.functionDao.deleteAll();
    }


    void initDb() throws Exception {

        AbstractLoginRequest.saveAdmin(this.adminDao, this.functionDao);
        this.authorizationToken = AbstractLoginRequest.getAdminBearerToken(this.mockMvc);


        FunctionEntity functionEntity = new FunctionEntity();
        functionEntity.setName("TEACHER");
        functionEntity = functionDao.save(functionEntity);

        this.adminEntity1 = IAdminTest.createAdminWith("halim@social.aston-ecole.com", functionEntity, IAdminTest.createImageEntity());
        this.adminEntity2 = IAdminTest.createAdminWith("yahiaoui@social.aston-ecole.com", functionEntity, IAdminTest.createImageEntity());
        this.adminEntity1 = adminDao.save(this.adminEntity1);
        this.adminEntity2 = adminDao.save(this.adminEntity2);


    }
    @Test
    void getAllAdminFunctions  () throws Exception {

        var result = this.mockMvc.perform(MockMvcRequestBuilders.get(ADMIN_DASH_BOARD_GET_ALL_ADMIN_FUNCTIONS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .accept(MediaType.APPLICATION_JSON));


        result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(CoreMatchers.is(this.functionDao.findAll().size())));
        result.andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(CoreMatchers.is("MANAGER")));
        result.andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value(CoreMatchers.is(this.adminEntity1.getFunction().getName())));

    }


    @Test
    void getAllAdminFunctionsWithStudentToken  () throws Exception {
        this.iStudentDao.deleteAll();
        this.iStudentClassDao.deleteAll();

        AbstractLoginRequest.saveAStudent(this.iStudentDao, this.iStudentClassDao);
        var studentAuthorizationToken = AbstractLoginRequest.getStudentBearerToken(this.mockMvc);


        var result = this.mockMvc.perform(MockMvcRequestBuilders.get(ADMIN_DASH_BOARD_GET_ALL_ADMIN_FUNCTIONS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, studentAuthorizationToken)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isForbidden());
    }





    @Test
    void getAdminByIdTest() throws Exception {
        var adminUuid = this.adminEntity1.getUuid();

        var result = this.mockMvc.perform(MockMvcRequestBuilders.get(GET_ADMIN_BY_ID
                        + paramReq + adminUuid)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(jsonPath("uuid").value(CoreMatchers.is(adminUuid)));
        result.andExpect(jsonPath("firstname").value(CoreMatchers.is(this.adminEntity1.getFirstname())));
        result.andExpect(jsonPath("lastname").value(CoreMatchers.is(this.adminEntity1.getLastname())));
        result.andExpect(jsonPath("email").value(CoreMatchers.is(this.adminEntity1.getEmail())));
    }


    @Test
    void getAdminByIdWithStudentAuthorizationToken() throws Exception {
          this.iStudentDao.deleteAll();
            this.iStudentClassDao.deleteAll();

        AbstractLoginRequest.saveAStudent(this.iStudentDao, this.iStudentClassDao);
        this.authorizationToken = AbstractLoginRequest.getStudentBearerToken(this.mockMvc);


        var adminUuid = this.adminEntity1.getUuid();
        var result = this.mockMvc.perform(MockMvcRequestBuilders.get(GET_ADMIN_BY_ID
                        + paramReq + adminUuid)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isForbidden());
    }


    /*****************************  TESTS FOR  ID ADMIN  ********************************/
    @Test
    void getAdminByIDWithAdminNotFound() throws Exception {

        var randomUuid = java.util.UUID.randomUUID().toString();

        var result = this.mockMvc.perform(MockMvcRequestBuilders.get(GET_ADMIN_BY_ID
                        + paramReq + randomUuid)
                .header(HttpHeaders.AUTHORIZATION, authorizationToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("AdminNotFound"))));
    }


    @Test
    void getAdminByIDWithInvalidIdAdmin() throws Exception {

        var result = this.mockMvc.perform(MockMvcRequestBuilders.get(GET_ADMIN_BY_ID
                        + paramReq + "jjedh5")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidUuId"))));
    }

    @Test
    void getAdminByIDWithNullIdAdmin() throws Exception {

        var result = this.mockMvc.perform(MockMvcRequestBuilders.get(GET_ADMIN_BY_ID
                        + paramReq + null)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidUuId"))));
    }

    @Test
    void getAdminByIDWithEmptyIdAdmin() throws Exception {
        var result = this.mockMvc.perform(MockMvcRequestBuilders.get(GET_ADMIN_BY_ID
                        + paramReq + "")
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidUuId"))));
    }

    @Test
    void getAdminByIDWithOutIdAdmin() throws Exception {

        var result = this.mockMvc.perform(MockMvcRequestBuilders.get(GET_ADMIN_BY_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content()
                        .json(super.exceptionMessage(exceptionsMap.get("MissingPram"))));
    }

    @Test
    void getAdminWithInvalidAuthorizationToken() throws Exception {
        var result = this.mockMvc.perform(MockMvcRequestBuilders.get(GET_ADMIN_BY_ID
                        + paramReq + this.adminEntity1.getUuid())
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "InvalidToken")
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void getAdminWithOutAuthorizationToken() throws Exception {
        var result = this.mockMvc.perform(MockMvcRequestBuilders.get(GET_ADMIN_BY_ID
                        + paramReq + this.adminEntity1.getUuid())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
}