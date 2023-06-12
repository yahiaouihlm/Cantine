package fr.sqli.Cantine.controller.student;

import fr.sqli.Cantine.controller.AbstractContainerConfig;
import fr.sqli.Cantine.dao.IConfirmationTokenDao;
import fr.sqli.Cantine.dao.IStudentClassDao;
import fr.sqli.Cantine.dao.IStudentDao;
import fr.sqli.Cantine.entity.StudentClassEntity;
import fr.sqli.Cantine.entity.StudentEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.FileInputStream;
import java.io.IOException;


@SpringBootTest
@AutoConfigureMockMvc
public class UpdateStudentTest   extends AbstractContainerConfig implements IStudentTest {

    @Autowired
    private IStudentClassDao studentClassDao;
    @Autowired
    private IStudentDao studentDao;
    @Autowired
    private Environment env;

    @Autowired
    private IConfirmationTokenDao iConfirmationTokenDao;

    private MockMultipartFile imageData;
    @Autowired
    private IStudentClassDao iStudentClassDao;
    @Autowired
    private MockMvc mockMvc;

    private StudentEntity studentEntity;
    private MultiValueMap<String, String> formData;

    private StudentClassEntity studentClassEntity;

    void initDataBase() {
        this.studentClassEntity = new StudentClassEntity();
        studentClassEntity.setName("JAVA SQLI");
        this.studentClassDao.save(studentClassEntity);
        this.studentEntity = IStudentTest.createStudentClassEntity("student", this.studentClassEntity);
        this.studentEntity =  this.studentDao.save(this.studentEntity);
    }

    void cleanDataBase() {
        this.studentDao.deleteAll();
        this.studentClassDao.deleteAll();
        this.iConfirmationTokenDao.deleteAll();
    }

    void initFormData() throws IOException {
        this.formData = new LinkedMultiValueMap<>();
        this.formData.add("id", this.studentEntity.getId().toString());
        this.formData.add("firstname", "Halim");
        this.formData.add("lastname", "Yahiaoui");
        /*        this.formData.add("email", "halim.yahiaoui@social.aston-ecole.com");*/
        /*      this.formData.add("password", "test33");*/
        this.formData.add("birthdateAsString", "2000-07-18");
        this.formData.add("town", "paris");
        this.formData.add("phone", "0631990189");
        this.formData.add("studentClass", "JAVA SQLI");

        this.imageData = new MockMultipartFile(
                "image",                         // nom du champ de fichier
                IMAGE_NAME,          // nom du fichier
                "image/png",                    // type MIME
                new FileInputStream(IMAGE_FOR_TEST_PATH));

    }


    @BeforeEach
    void init() throws IOException {
        cleanDataBase();
        initDataBase();
        initFormData();
    }




    @Test
    void updateStudentWithNotFoundID() throws Exception {
        var  idStudent = this.studentEntity.getId() + 11;
         this.formData.set("id", String.valueOf(idStudent));

         var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, UPDATE_STUDENT_INFO)
                 .file(this.imageData)
                 .params(this.formData)
                 .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


         result.andExpect(MockMvcResultMatchers.status().isNotFound())
                 .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("StudentNotFound"))));

    }





    /***************************************** TESTS   CLASS   ************************************************/
    @Test
    void updateStudentWithInvalidClass() throws Exception {
        this.formData.set("studentClass",  "wrongFunction");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("StudentClassNotFound"))));


    }
    @Test
    void  updateStudentWithWrongClass() throws Exception {
        this.formData.set("studentClass",  "  ab ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("StudentClassNotFound"))));


    }

    @Test
    void  updateStudentWithEmptyClass() throws Exception {
        this.formData.set("studentClass",  "  ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("StudentClassRequire"))));


    }
    @Test
    void  updateStudentWithNullClass() throws Exception {
        this.formData.set("studentClass",  null);

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("StudentClassRequire"))));


    }


    @Test
    void  updateStudentWithOutClass() throws Exception {
        this.formData.remove("studentClass");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("StudentClassRequire"))));


    }






    /***************************************** TESTS   PHONES   ************************************************/
    @Test
    void  updateStudentWithInvalidPhoneFormat3() throws Exception {
        this.formData.set("phone", " +33076289514 ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidPhoneFormat"))));
    }

    @Test
    void  updateStudentWithInvalidPhoneFormat2() throws Exception {
        this.formData.set("phone",  " 06319907853654 ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidPhoneFormat"))));


    }

    @Test
    void  updateStudentWithInvalidPhoneFormat() throws Exception {
        this.formData.set("phone",  " oksfki ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidPhoneFormat"))));


    }
    @Test
    void  updateStudentWithEmptyPhone() throws Exception {
        this.formData.set("phone",  "  . ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidPhoneFormat"))));


    }





    /***************************************** TESTS   TOWN   ************************************************/

    @Test
    void  updateStudentWithTooLongTown() throws Exception {
        this.formData.set("town",  "a".repeat(1001));

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LongTown"))));


    }



    @Test
    void  updateStudentWithTooShortTown() throws Exception {
        this.formData.set("town",  "  ab ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("ShortTown"))));


    }

    @Test
    void updateStudentWithEmptyTown() throws Exception {
        this.formData.set("town",  "  ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("TownRequire"))));


    }
    @Test
    void  updateStudentWithNullTown() throws Exception {
        this.formData.set("town",  null);

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("TownRequire"))));


    }
    @Test
    void  updateStudentWithOutTown() throws Exception {
        this.formData.remove("town");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("TownRequire"))));


    }









    /***************************************** TESTS   BirthdateAsString  ************************************************/

    @Test
    void  updateStudentWithEmptyInvalidBirthdateAsStringFormat4() throws Exception {
        this.formData.set("birthdateAsString",  "2000/07/18");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidBirthdateFormat"))));


    }
    @Test
    void  updateStudentWithEmptyInvalidBirthdateAsStringFormat3() throws Exception {
        this.formData.set("birthdateAsString",  "18/07/2000");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidBirthdateFormat"))));


    }

    @Test
    void updateStudentWithEmptyInvalidBirthdateAsStringFormat2() throws Exception {
        this.formData.set("birthdateAsString",  "18-07-2000");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidBirthdateFormat"))));


    }

    @Test
    void  updateStudentWithEmptyInvalidBirthdateAsStringFormat() throws Exception {
        this.formData.set("birthdateAsString",  "kzjrnozr,kfjfkrfkrf");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidBirthdateFormat"))));


    }


    @Test
    void  updateStudentWithEmptyBirthdateAsString() throws Exception {
        this.formData.set("birthdateAsString",  "  ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("BirthdateRequire"))));


    }
    @Test
    void  updateStudentWithNullBirthdateAsString() throws Exception {
        this.formData.set("birthdateAsString",  null);

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("BirthdateRequire"))));


    }
    @Test
    void  updateStudentWithOutBirthdayAsString() throws Exception {
        this.formData.remove("birthdateAsString");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("BirthdateRequire"))));


    }







    /***************************************** TESTS   LASTNAME  ************************************************/

    @Test
    void  updateStudentWithTooLongLastname() throws Exception {
        this.formData.set("lastname",  "a".repeat(91));

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LongLastName"))));


    }



    @Test
    void  updateStudentWithTooShortLastname() throws Exception {
        this.formData.set("lastname",  "  ab ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("ShortLastName"))));


    }

    @Test
    void  updateStudentWithEmptyLastname() throws Exception {
        this.formData.set("lastname",  "  ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LastNameRequire"))));


    }
    @Test
    void  updateStudentWithNullLastname() throws Exception {
        this.formData.set("lastname",  null);

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LastNameRequire"))));


    }
    @Test
    void  updateStudentWithOutLastname() throws Exception {
        this.formData.remove("lastname");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LastNameRequire"))));


    }





    /***************************************** TESTS   FIRSTNAME  ************************************************/

    @Test
    void updateStudentWithTooLongFirstname() throws Exception {
        this.formData.set("firstname", "a".repeat(91));

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LongFirstName"))));


    }


    @Test
    void updateStudentWithTooShortFirstname() throws Exception {
        this.formData.set("firstname", "  ab ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("ShortFirstName"))));


    }

    @Test
    void updateStudentWithEmptyFirstname() throws Exception {
        this.formData.set("firstname", "  ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("FirstNameRequire"))));


    }

    @Test
    void updateStudentWithNullFirstname() throws Exception {
        this.formData.set("firstname", null);

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("FirstNameRequire"))));


    }

    @Test
    void updateStudentWithOutFirstname() throws Exception {
        this.formData.remove("firstname");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("FirstNameRequire"))));


    }




    /***************************************** TESTS   ID  ************************************************/

    @Test
    void  updateStudentWithInvalidId() throws Exception {
        this.formData.set("id", "knaezfk");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidArgument"))));

    }

    @Test
    void  updateStudentWithWrongId() throws Exception {
        this.formData.set("id", "1.2");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidArgument"))));

    }

    @Test
    void  updateStudentWithWrongId2() throws Exception {
        this.formData.set("id", "-1.2");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidArgument"))));

    }
    @Test
    void  updateStudentWithEmptyId() throws Exception {
        this.formData.set("id", "");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidStudentId"))));

    }
    @Test
    void  updateStudentWithNullId() throws Exception {
        this.formData.set("id", null);

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidStudentId"))));

    }


}

