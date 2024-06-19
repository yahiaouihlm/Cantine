package fr.sqli.cantine.controller.users.student;

import fr.sqli.cantine.controller.AbstractContainerConfig;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


@SpringBootTest
@AutoConfigureMockMvc
public class GetStudentTest  extends AbstractContainerConfig implements IStudentTest {
    final  String paramReq = "?" + "idStudent" + "=";

    @Autowired
    private IStudentClassDao studentClassDao;

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private IStudentDao studentDao;


   private StudentEntity studentEntity1;
    private  StudentEntity studentEntity2;


    void  cleanUpDb(){
        this.studentDao.deleteAll();
        this.studentClassDao.deleteAll();
    }

    @BeforeEach
    void  initDB () {
        cleanUpDb();

        StudentClassEntity studentClassEntity = new StudentClassEntity();
        studentClassEntity.setName("SQLI JAVA");
        this.studentClassDao.save(studentClassEntity);

        this.studentEntity1 = IStudentTest.createStudentEntity("halim@social.aston-ecole.com" ,studentClassEntity, IStudentTest.createImageEntity());
        this.studentEntity2 = IStudentTest.createStudentEntity("yahiaoui@social.aston-ecole.com",studentClassEntity,IStudentTest.createImageEntity())  ;
        this.studentEntity1 = studentDao.save(this.studentEntity1);
        this.studentEntity2 = studentDao.save(this.studentEntity2);
    }

    @Test
    void  getStudentByIdTest () throws Exception {
        var  idStudent = this.studentEntity1.getId();

        var result = this.mockMvc.perform(MockMvcRequestBuilders.get(GET_STUDENT_BY_ID
                        + paramReq + idStudent)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(MockMvcResultMatchers.jsonPath("id").value(CoreMatchers.is(idStudent)));
        result.andExpect(MockMvcResultMatchers.jsonPath("firstname").value(CoreMatchers.is(this.studentEntity1.getFirstname())));
        result.andExpect(MockMvcResultMatchers.jsonPath("lastname").value(CoreMatchers.is(this.studentEntity1.getLastname())));
        result.andExpect(MockMvcResultMatchers.jsonPath("email").value(CoreMatchers.is(this.studentEntity1.getEmail())));
    }






    /*****************************  TESTS FOR  ID Student  ********************************/
    @Test
    void getStudentByIDWithStudentNotFound() throws Exception {
        var idStudent = this.studentDao.findAll().stream().map(StudentEntity::getId)
                .max(Integer::compareTo).get() + 1;

        var result = this.mockMvc.perform(MockMvcRequestBuilders.get(GET_STUDENT_BY_ID
                        + paramReq + idStudent)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("StudentNotFound"))));
    }


    @Test
    void getStudentByIDWithDoubleIdStudent () throws Exception {

        var result = this.mockMvc.perform(MockMvcRequestBuilders.get(GET_STUDENT_BY_ID
                        + paramReq + "1.5" )
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidArgument"))));
    }

    @Test
    void getStudentByIDWithNegativeIdStudent () throws Exception {

        var result = this.mockMvc.perform(MockMvcRequestBuilders.get(GET_STUDENT_BY_ID
                        + paramReq + "-5" )
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidStudentId"))));
    }

    @Test
    void getStudentByIDWithInvalidIdStudent () throws Exception {

        var result = this.mockMvc.perform(MockMvcRequestBuilders.get(GET_STUDENT_BY_ID
                        + paramReq + "jjedh5" )
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidArgument"))));
    }
    @Test
    void  getStudentByIDWithNullIdStudent () throws Exception {

        var result = this.mockMvc.perform(MockMvcRequestBuilders.get(GET_STUDENT_BY_ID
                        + paramReq + null )
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidArgument"))));
    }

    @Test
    void getStudentByIDWithEmptyIdStudent() throws Exception {
        var result = this.mockMvc.perform(MockMvcRequestBuilders.get(GET_STUDENT_BY_ID
                        + paramReq +"" )
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("MissingPram"))));
    }



    @Test
    void getStudentByIDWithOutIdStudent () throws Exception {
        var result = this.mockMvc.perform(MockMvcRequestBuilders.get(GET_STUDENT_BY_ID
                        + paramReq )
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content()
                        .json(super.exceptionMessage(exceptionsMap.get("MissingPram"))));
    }


}
