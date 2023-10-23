package fr.sqli.cantine.controller;


import fr.sqli.cantine.dao.IAdminDao;
import fr.sqli.cantine.dao.IFunctionDao;
import fr.sqli.cantine.dao.IStudentClassDao;
import fr.sqli.cantine.dao.IStudentDao;
import fr.sqli.cantine.entity.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;

@AutoConfigureMockMvc
@SpringBootTest
public class AbstractLoginRequest extends AbstractContainerConfig {

    private String studentBearerToken;
    private String adminBearerToken;

    @Autowired
    private IAdminDao adminDao;

    @Autowired
    private IStudentDao studentDao;

    @Autowired
    private IFunctionDao functionDao;

    @Autowired
    private IStudentClassDao studentClassDao;


    @Autowired
    private MockMvc mockMvc;
    private StudentEntity studentCreated;
    private AdminEntity adminCreated;

    //  delete  all  student  or admin  from  database
    void cleanDataBase() {
        this.studentDao.deleteAll();
        this.adminDao.deleteAll();
        this.studentClassDao.deleteAll();
        this.functionDao.deleteAll();
    }



    @BeforeEach
    public void studentLoginRequest() throws Exception {
        cleanDataBase();
        this.studentCreated = this.createStudentForLoginRequest();
          this.studentBearerToken =  this.studentBearerToken();
    }

    @BeforeEach
    public void  adminLoginRequest() throws Exception {
        System.out.println("adminLoginRequest");
        cleanDataBase();
        this.adminCreated = this.createAdminForLoginRequest();
        this.adminBearerToken =  this.adminBearerToken();
    }


    public String studentBearerToken() throws Exception {
        var req = this.mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"username\": \"" + this.studentCreated.getEmail() + "\", \"password\": \"" + this.studentCreated.getPassword() + "\" }"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn(); // Utilisez .andReturn() pour obtenir la réponse HTTP

        String authorizationHeader = req.getResponse().getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // Le jeton d'autorisation commence généralement par "Bearer "
            String token = authorizationHeader.substring("Bearer ".length());
            return token;
        } else {
            throw new Exception("Bearer token not found in the response header");
        }
    }


    public String adminBearerToken() throws Exception {
        var req = this.mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"email\": \"" + this.adminCreated.getEmail() + "\", \"password\": \"" + this.studentCreated.getPassword() + "\" }"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn(); // Utilisez .andReturn() pour obtenir la réponse HTTP

        String authorizationHeader = req.getResponse().getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // Le jeton d'autorisation commence généralement par "Bearer "
            String token = authorizationHeader.substring("Bearer ".length());
            return token;
        } else {
            throw new Exception("Bearer token not found in the response header");
        }
    }


    public final  StudentEntity createStudentForLoginRequest() {
        var  studentClass = createStudentClass();
        StudentEntity studentEntity = new StudentEntity();
        studentEntity.setFirstname("student");
        studentEntity.setLastname("student");
        studentEntity.setEmail("halim.yahiaoui@social.aston-ecole.com");
        studentEntity.setPassword("test33");
        studentEntity.setWallet(new BigDecimal(500));
        studentEntity.setBirthdate(LocalDate.now());
        studentEntity.setPhone("0666666666");
        studentEntity.setTown("paris");
        studentEntity.setStatus(0);
        studentEntity.setRegistrationDate(LocalDate.now());
        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setImagename("image");
        studentEntity.setImage(imageEntity);
        studentEntity.setStudentClass(studentClass);
        return this.studentDao.save(studentEntity);
    }


    public final   AdminEntity createAdminForLoginRequest() {
        var adminFunction = createFunction();
        AdminEntity adminEntity = new AdminEntity();
        adminEntity.setFirstname("admin");
        adminEntity.setLastname("admin");
        adminEntity.setEmail("yahiaouihlm@gmail.com");
        adminEntity.setPassword("test33");
        adminEntity.setBirthdate(LocalDate.now());
        adminEntity.setPhone("0666666666");
        adminEntity.setTown("paris");
        adminEntity.setRegistrationDate(LocalDate.now());
        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setImagename("image");
        adminEntity.setImage(imageEntity);
        adminEntity.setFunction(adminFunction);
        return this.adminDao.save(adminEntity);
    }


    public StudentClassEntity createStudentClass() {
        StudentClassEntity studentClassEntity = new StudentClassEntity();
        studentClassEntity.setName("SQLI_JAVA");
        return this.studentClassDao.save(studentClassEntity);
    }

    public FunctionEntity createFunction() {
        FunctionEntity functionEntity = new FunctionEntity();
        functionEntity.setName("MANAGER");
        return  this.functionDao.save(functionEntity);
    }

    public String getStudentBearerToken() {
        return studentBearerToken;
    }

    public String getAdminBearerToken() {
        return adminBearerToken;
    }


}


