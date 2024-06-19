package fr.sqli.cantine.controller.users.auth;

import fr.sqli.cantine.controller.AbstractLoginRequest;
import fr.sqli.cantine.controller.users.student.IStudentTest;
import fr.sqli.cantine.dao.IImageDao;
import fr.sqli.cantine.dao.IStudentClassDao;
import fr.sqli.cantine.dao.IStudentDao;
import fr.sqli.cantine.dto.in.users.Login;
import fr.sqli.cantine.entity.StudentEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
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
public class LoginTests extends AbstractLoginRequest  implements IStudentTest {


    @Autowired
    private IStudentDao studentDao;

    @Autowired
    private IStudentClassDao istudentClassDao;

    @Autowired
    private IImageDao imageDao;

    @Autowired
    private MockMvc mockMvc;

    private StudentEntity studentEntity;


    private ObjectMapper ObjectMapper =  new ObjectMapper() ;

    private  void cleanDataBase() {
        this.studentDao.deleteAll();
        this.imageDao.deleteAll();
        this.istudentClassDao.deleteAll();
    }

    @BeforeEach
    void initDB() {
        cleanDataBase();
        var studentClass = this.istudentClassDao.save(IStudentTest.createStudentClassEntity());
        this.studentEntity = IStudentTest.createStudentEntity( IStudentTest.STUDENT_EMAIL_EXAMPLE,studentClass , IStudentTest.createImageEntity()); //  need password  before  save (hashing)
        this.studentDao.save(this.studentEntity);
    }




    /******************************************************* Student login tests *******************************************************/
    @Test
    void loginWithValidCredentials() throws Exception {
        var login = new Login();
        login.setEmail(IStudentTest.STUDENT_EMAIL_EXAMPLE);
        login.setPassword("password");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, STUDENT_SIGN_IN_URL)
                .content(ObjectMapper.writeValueAsString(login))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("{   \"Firstname\":\""+ this.studentEntity.getFirstname() +"\" , \"email\":\""+ this.studentEntity.getEmail() +"\",\"status\":\"OK\"}"));
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
