package fr.sqli.cantine.controller.users.auth;

import fr.sqli.cantine.controller.AbstractLoginRequest;
import fr.sqli.cantine.controller.users.admin.adminDashboard.account.IAdminTest;
import fr.sqli.cantine.controller.users.student.IStudentTest;
import fr.sqli.cantine.dao.*;
import fr.sqli.cantine.dto.in.users.Login;
import fr.sqli.cantine.entity.AdminEntity;
import fr.sqli.cantine.entity.StudentEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class LoginTests extends AbstractLoginRequest implements IStudentTest, IAdminTest {


    @Autowired
    private IStudentDao studentDao;

    @Autowired
    private IStudentClassDao istudentClassDao;

    @Autowired
    private IImageDao imageDao;
    @Autowired
    private IAdminDao iAdminDao;
    @Autowired
    private IFunctionDao iFunctionDao;
    @Autowired
    private MockMvc mockMvc;

    private StudentEntity studentEntity;
    private AdminEntity adminEntity;

    private ObjectMapper ObjectMapper = new ObjectMapper();

    private void cleanDataBase() {
        this.studentDao.deleteAll();
        this.iAdminDao.deleteAll();
        this.imageDao.deleteAll();
        this.iFunctionDao.deleteAll();
        this.istudentClassDao.deleteAll();
    }

    @BeforeEach
    void initDB() {
        cleanDataBase();
        // save  student ;
        var studentClass = this.istudentClassDao.save(IStudentTest.createStudentClassEntity());
        this.studentEntity = IStudentTest.createStudentEntity(IStudentTest.STUDENT_EMAIL_EXAMPLE, studentClass, IStudentTest.createImageEntity()); //  need password  before  save (hashing)
        this.studentDao.save(this.studentEntity);

        // save  admin;
        var functionEntity = this.iFunctionDao.save(IAdminTest.createFunctionEntity());
        this.adminEntity = IAdminTest.createAdminWith(IAdminTest.ADMIN_EMAIL_EXAMPLE, functionEntity, IAdminTest.createImageEntity());
        this.iAdminDao.save(this.adminEntity);
    }

    /******************************************************* admin login tests *******************************************************/
    @Test
    void loginAdminWithValidateAccount() throws Exception {
        var login = new Login();
        login.setEmail(IAdminTest.ADMIN_EMAIL_EXAMPLE);
        login.setPassword(IAdminTest.ADMIN_PASSWORD_EXAMPLE);

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, ADMIN_SIGN_IN_URL)
                .content(ObjectMapper.writeValueAsString(login))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("{   \"Firstname\":\"" + this.adminEntity.getFirstname() + "\" , \"email\":\"" + this.adminEntity.getEmail() + "\",  \"id\":\"" + this.adminEntity.getUuid() + "\"    ,\"status\":\"OK\"}"));
    }

    @Test
    void loginAdminWithInvalidAccount() throws Exception {
        var login = new Login();
        login.setEmail(IAdminTest.ADMIN_EMAIL_EXAMPLE);
        login.setPassword(this.adminEntity.getPassword());

        //  update student  status  to  disabled
        this.adminEntity.setValidation(0);
        this.iAdminDao.save(this.adminEntity);


        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, ADMIN_SIGN_IN_URL)
                .content(ObjectMapper.writeValueAsString(login))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content().json("{\"message\":\"INVALID ACCOUNT\",\"status\":\"UNAUTHORIZED\"}"));
    }

    @Test
    void loginAdminWithDisabledAccount() throws Exception {
        var login = new Login();
        login.setEmail(IAdminTest.ADMIN_EMAIL_EXAMPLE);
        login.setPassword(this.adminEntity.getPassword());

        //  update student  status  to  disabled
        this.adminEntity.setStatus(0);
        this.iAdminDao.save(this.adminEntity);


        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, ADMIN_SIGN_IN_URL)
                .content(ObjectMapper.writeValueAsString(login))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().json("{\"message\":\"DISABLED ACCOUNT\",\"status\":\"FORBIDDEN\"}"));
    }

    @Test
    void loginAdminWithWrongPassword() throws Exception {
        var login = new Login();
        login.setEmail(IAdminTest.ADMIN_EMAIL_EXAMPLE);
        login.setPassword("wrongPassword");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, ADMIN_SIGN_IN_URL)
                .content(ObjectMapper.writeValueAsString(login))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));


        result.andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content().json("{\"message\":\"WRONG CREDENTIALS\",\"status\":\"UNAUTHORIZED\"}"));


    }

    @Test
    void loginAdminWithEmptyPassword() throws Exception {
        var login = new Login();
        login.setEmail(IAdminTest.ADMIN_EMAIL_EXAMPLE);
        login.setPassword("");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, ADMIN_SIGN_IN_URL)
                .content(ObjectMapper.writeValueAsString(login))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));


        result.andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content().json("{\"message\":\"WRONG CREDENTIALS\",\"status\":\"UNAUTHORIZED\"}"));


    }

    @Test
    void loginAdminWithEmptyEmail() throws Exception {
        var login = new Login();
        login.setPassword("password");


        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, ADMIN_SIGN_IN_URL)
                .content(ObjectMapper.writeValueAsString(login))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));


        result.andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content().json("{\"message\":\"WRONG CREDENTIALS\",\"status\":\"UNAUTHORIZED\"}"));


    }

    /******************************************************* Student login tests *******************************************************/


    @Test
    void loginWithValidCredentials() throws Exception {
        var login = new Login();
        login.setEmail(IStudentTest.STUDENT_EMAIL_EXAMPLE);
        login.setPassword(IStudentTest.STUDENT_PASSWORD_EXAMPLE);

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, STUDENT_SIGN_IN_URL)
                .content(ObjectMapper.writeValueAsString(login))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("{   \"Firstname\":\"" + this.studentEntity.getFirstname() + "\" , \"email\":\"" + this.studentEntity.getEmail() + "\",  \"id\":\"" + this.studentEntity.getUuid() + "\"    ,\"status\":\"OK\"}"));
    }

    @Test
    void loginWithDisabledAccount() throws Exception {
        var login = new Login();
        login.setEmail(IStudentTest.STUDENT_EMAIL_EXAMPLE);
        login.setPassword("password");

        //  update student  status  to  disabled
        this.studentEntity.setStatus(0);
        this.studentDao.save(this.studentEntity);


        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, STUDENT_SIGN_IN_URL)
                .content(ObjectMapper.writeValueAsString(login))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().json("{\"message\":\"DISABLED ACCOUNT\",\"status\":\"FORBIDDEN\"}"));
    }

    @Test
    void loginWithWrongPassword() throws Exception {
        var login = new Login();
        login.setEmail(IStudentTest.STUDENT_EMAIL_EXAMPLE);
        login.setPassword("wrongPassword");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, STUDENT_SIGN_IN_URL)
                .content(ObjectMapper.writeValueAsString(login))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));


        result.andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content().json("{\"message\":\"WRONG CREDENTIALS\",\"status\":\"UNAUTHORIZED\"}"));


    }

    @Test
    void loginWithEmptyPassword() throws Exception {
        var login = new Login();
        login.setEmail(IStudentTest.STUDENT_EMAIL_EXAMPLE);
        login.setPassword("");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, STUDENT_SIGN_IN_URL)
                .content(ObjectMapper.writeValueAsString(login))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));


        result.andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content().json("{\"message\":\"WRONG CREDENTIALS\",\"status\":\"UNAUTHORIZED\"}"));


    }

    @Test
    void loginWithEmptyEmail() throws Exception {
        var login = new Login();
        login.setPassword("password");


        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, STUDENT_SIGN_IN_URL)
                .content(ObjectMapper.writeValueAsString(login))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));


        result.andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content().json("{\"message\":\"WRONG CREDENTIALS\",\"status\":\"UNAUTHORIZED\"}"));


    }


}
