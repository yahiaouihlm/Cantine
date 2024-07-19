package fr.sqli.cantine.controller;


import fr.sqli.cantine.controller.users.admin.adminDashboard.account.IAdminTest;
import fr.sqli.cantine.controller.users.student.IStudentTest;
import fr.sqli.cantine.dao.IFunctionDao;
import fr.sqli.cantine.dao.IStudentClassDao;
import fr.sqli.cantine.dao.IUserDao;
import fr.sqli.cantine.dto.in.users.Login;
import fr.sqli.cantine.entity.UserEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.JsonNode;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;


public class AbstractLoginRequest extends AbstractContainerConfig  implements IAdminTest , IStudentTest {


     public  static UserEntity saveAdmin(IUserDao iAdminDao, IFunctionDao iFunctionDao) {
         // save  admin;
         var functionEntity = iFunctionDao.save(IAdminTest.createFunctionEntity());
         var adminEntity = IAdminTest.createAdminWith(IAdminTest.ADMIN_EMAIL_EXAMPLE, functionEntity, IAdminTest.createImageEntity());
         return iAdminDao.save(adminEntity);
     }


    public static String getAdminBearerToken(MockMvc mockMvc) throws Exception {
        var login = new Login();
        login.setEmail(IAdminTest.ADMIN_EMAIL_EXAMPLE);
        login.setPassword(IAdminTest.ADMIN_PASSWORD_EXAMPLE);

        var result = mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, ADMIN_SIGN_IN_URL)
                        .content(new ObjectMapper().writeValueAsString(login))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(result.getResponse().getContentAsString());

        return jsonNode.get("Authorization").asText();
    }


    public static String getStudentBearerToken(MockMvc mockMvc) throws Exception {
        var login = new Login();
        login.setEmail(IStudentTest.STUDENT_EMAIL_EXAMPLE);
        login.setPassword(IStudentTest.STUDENT_PASSWORD_EXAMPLE);

        var result = mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, STUDENT_SIGN_IN_URL)
                        .content(new ObjectMapper().writeValueAsString(login))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(result.getResponse().getContentAsString());

        return jsonNode.get("Authorization").asText();
    }



    public  static UserEntity saveAStudent(IUserDao iStudentDao, IStudentClassDao iStudentClassDao){
        // save  student;
        var studentClass = iStudentClassDao.save(IStudentTest.createStudentClassEntity());
        var studentEntity = IStudentTest.createStudentEntity(IStudentTest.STUDENT_EMAIL_EXAMPLE, studentClass, IAdminTest.createImageEntity());
        return iStudentDao.save(studentEntity);
    }

/*

    public String getStudentAuthToken() throws Exception {

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





    private     StudentEntity createStudentForLoginRequest( IStudentDao iStudentDao) {
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
        return iStudentDao.save(studentEntity);
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

    public String adminBearerToken() throws Exception {
        var req = this.mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(new Login(this.adminCreated.getEmail(), this.adminCreated.getPassword()))))

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

*/
}



