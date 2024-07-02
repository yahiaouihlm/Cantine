package fr.sqli.cantine.controller.users.student;

import fr.sqli.cantine.controller.AbstractContainerConfig;
import fr.sqli.cantine.controller.AbstractLoginRequest;
import fr.sqli.cantine.dao.IAdminDao;
import fr.sqli.cantine.dao.IFunctionDao;
import fr.sqli.cantine.dao.IStudentClassDao;
import fr.sqli.cantine.dao.IStudentDao;
import fr.sqli.cantine.entity.StudentClassEntity;
import fr.sqli.cantine.entity.StudentEntity;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


@SpringBootTest
@AutoConfigureMockMvc
public class GetStudentTest extends AbstractContainerConfig implements IStudentTest {
    final String paramReq = "?" + "studentUuid" + "=";

    private IAdminDao adminDao;
    private IFunctionDao functionDao;
    private IStudentClassDao studentClassDao;
    private MockMvc mockMvc;
    private IStudentDao studentDao;
    private StudentEntity studentEntity;
    private String authorizationToken;

    @Autowired
    public GetStudentTest(IAdminDao iAdminDao , IFunctionDao iFunctionDao , IStudentDao iStudentDao, IStudentClassDao iStudentClassDao, MockMvc mockMvc) throws Exception {
        this.studentDao = iStudentDao;
        this.studentClassDao = iStudentClassDao;
        this.mockMvc = mockMvc;
        this.adminDao = iAdminDao;
        this.functionDao = iFunctionDao;
        cleanUpDb();
        initDB();
    }


    void cleanUpDb() {
        this.studentDao.deleteAll();
        this.functionDao.deleteAll();
        this.studentClassDao.deleteAll();
        this.adminDao.deleteAll();
    }


    void initDB() throws Exception {
        this.studentEntity = AbstractLoginRequest.saveAStudent( this.studentDao, this.studentClassDao);
        this.authorizationToken= AbstractLoginRequest.getStudentBearerToken(this.mockMvc);
 }

    @Test
    void getStudentByIdTest() throws Exception {

        var result = this.mockMvc.perform(MockMvcRequestBuilders.get(GET_STUDENT_BY_ID
                        + paramReq + this.studentEntity.getUuid())
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION,this.authorizationToken)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(MockMvcResultMatchers.jsonPath("uuid").value(CoreMatchers.is(this.studentEntity.getUuid())));
        result.andExpect(MockMvcResultMatchers.jsonPath("firstname").value(CoreMatchers.is(this.studentEntity.getFirstname())));
        result.andExpect(MockMvcResultMatchers.jsonPath("lastname").value(CoreMatchers.is(this.studentEntity.getLastname())));
        result.andExpect(MockMvcResultMatchers.jsonPath("email").value(CoreMatchers.is(this.studentEntity.getEmail())));
    }


    @Test
    void getStudentByIDWithAdminAuthToken() throws Exception {

        AbstractLoginRequest.saveAdmin(this.adminDao, this.functionDao);
        String  adminAuthToken = AbstractLoginRequest.getAdminBearerToken(this.mockMvc);

        var result = this.mockMvc.perform(MockMvcRequestBuilders.get(GET_STUDENT_BY_ID
                        + paramReq + this.studentEntity.getUuid())
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION,adminAuthToken)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isOk());
    }



    /*****************************  TESTS FOR  ID Student  ********************************/
    @Test
    void getStudentByIDWithStudentNotFound() throws Exception {

        var result = this.mockMvc.perform(MockMvcRequestBuilders.get(GET_STUDENT_BY_ID
                        + paramReq + java.util.UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION,this.authorizationToken)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("StudentNotFound"))));
    }


    @Test
    void getStudentByIDWithDoubleIdStudent() throws Exception {

        var result = this.mockMvc.perform(MockMvcRequestBuilders.get(GET_STUDENT_BY_ID
                        + paramReq + "z57z57dzzz774zz")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION,this.authorizationToken)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidUuid"))));
    }

    @Test
    void getStudentByIDWithNegativeIdStudent() throws Exception {

        var result = this.mockMvc.perform(MockMvcRequestBuilders.get(GET_STUDENT_BY_ID
                        + paramReq + "-5")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION,this.authorizationToken)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidUuid"))));
    }

    @Test
    void getStudentByIDWithInvalidIdStudent() throws Exception {

        var result = this.mockMvc.perform(MockMvcRequestBuilders.get(GET_STUDENT_BY_ID
                        + paramReq + "jjedh5")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION,this.authorizationToken)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidUuid"))));
    }

    @Test
    void getStudentByIDWithNullIdStudent() throws Exception {

        var result = this.mockMvc.perform(MockMvcRequestBuilders.get(GET_STUDENT_BY_ID
                        + paramReq + null)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION,this.authorizationToken)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidUuid"))));
    }

    @Test
    void getStudentByIDWithEmptyIdStudent() throws Exception {
        var result = this.mockMvc.perform(MockMvcRequestBuilders.get(GET_STUDENT_BY_ID
                        + paramReq + "")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION,this.authorizationToken)
                .accept(MediaType.APPLICATION_JSON));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidUuid"))));
    }


    @Test
    void getStudentByIDWithOutIdStudent() throws Exception {

        var result = this.mockMvc.perform(MockMvcRequestBuilders.get(GET_STUDENT_BY_ID
                        + paramReq)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION,this.authorizationToken)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .json(super.exceptionMessage(exceptionsMap.get("InvalidUuid"))));
    }


    @Test
    void getStudentByIDWithOutAuthToken() throws Exception {
        var result = this.mockMvc.perform(MockMvcRequestBuilders.get(GET_STUDENT_BY_ID
                        + paramReq + java.util.UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isUnauthorized());

    }



}
