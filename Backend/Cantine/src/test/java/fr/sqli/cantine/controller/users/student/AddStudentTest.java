package fr.sqli.cantine.controller.users.student;

import fr.sqli.cantine.controller.AbstractContainerConfig;
import fr.sqli.cantine.dao.IConfirmationTokenDao;
import fr.sqli.cantine.dao.IOrderDao;
import fr.sqli.cantine.dao.IStudentClassDao;
import fr.sqli.cantine.dao.IUserDao;
import fr.sqli.cantine.entity.StudentClassEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@SpringBootTest
@AutoConfigureMockMvc
public class AddStudentTest extends AbstractContainerConfig implements IStudentTest {

    private IStudentClassDao studentClassDao;
    private IUserDao studentDao;
    private Environment env;
    private IOrderDao iOrderDao;
    private IConfirmationTokenDao iConfirmationTokenDao;
    private MockMultipartFile imageData;
    private IStudentClassDao iStudentClassDao;
    private MockMvc mockMvc;
    private MultiValueMap<String, String> formData;
    private StudentClassEntity studentClassEntity;

    @Autowired
    public AddStudentTest(IOrderDao iOrderDao, IUserDao studentDao, IStudentClassDao studentClassDao, IConfirmationTokenDao iConfirmationTokenDao, Environment env, MockMvc mockMvc) throws IOException {
        this.studentDao = studentDao;
        this.studentClassDao = studentClassDao;
        this.mockMvc = mockMvc;
        this.iConfirmationTokenDao = iConfirmationTokenDao;
        this.env = env;
        this.iOrderDao = iOrderDao;
        cleanDatabase();
        initDatabase();
        initFormData();

    }

    void cleanDatabase() {
        this.iOrderDao.deleteAll();
        this.iConfirmationTokenDao.deleteAll();
        this.studentDao.deleteAll();
        this.studentClassDao.deleteAll();
    }

    void initDatabase() {
        this.studentClassEntity = this.studentClassDao.save(IStudentTest.createStudentClassEntity());
        var studentEntity = IStudentTest.createStudentEntity(IStudentTest.STUDENT_EMAIL_EXAMPLE, this.studentClassEntity, IStudentTest.createImageEntity());
        this.studentDao.save(studentEntity);
    }

    void initFormData() throws IOException {
        this.formData = new LinkedMultiValueMap<>();
        this.formData.add("firstname", "Halim");
        this.formData.add("lastname", "Yahiaoui");
        this.formData.add("email", "halim.yahiaoui@social.aston-ecole.com");
        this.formData.add("password", "test33");
        this.formData.add("birthdateAsString", "2000-07-18");
        this.formData.add("town", "paris");
        this.formData.add("phone", "0631990189");
        this.formData.add("studentClass", "class");

        this.imageData = new MockMultipartFile(
                "image",                         // nom du champ de fichier
                IMAGE_NAME,          // nom du fichier
                "image/png",                    // type MIME
                new FileInputStream(IMAGE_FOR_TEST_PATH));

    }





    @Test
    @Disabled
    void assStudentWithImageAndPhone() throws Exception {

         var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(super.responseMessage(responseMap.get("StudentAddedSuccessfully"))));

        var student = this.studentDao.findStudentByEmail(this.formData.getFirst("email"));
        Assertions.assertTrue(student.isPresent());

        Assertions.assertEquals(student.get().getFirstname(), this.formData.getFirst("firstname"));
        Assertions.assertEquals(student.get().getLastname(), this.formData.getFirst("lastname"));
        Assertions.assertEquals(student.get().getEmail(), this.formData.getFirst("email"));
        Assertions.assertNotNull(student.get().getImage());

        String  path  =  this.env.getProperty("sqli.cantine.image.student.path");

        path = path + "/" + student.get().getImage().getName();
        Assertions.assertTrue(
                new File(path).delete()
        );


    }

    @Test
    @Disabled
    void addStudentWithOutImageAndPhone() throws Exception {
        this.formData.remove("phone");
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, STUDENT_SIGN_UP)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(super.responseMessage(responseMap.get("StudentAddedSuccessfully"))));

        var student = this.studentDao.findStudentByEmail(this.formData.getFirst("email"));
        Assertions.assertTrue(student.isPresent());
        Assertions.assertEquals(student.get().getFirstname(), this.formData.getFirst("firstname"));
        Assertions.assertEquals(student.get().getLastname(), this.formData.getFirst("lastname"));
        Assertions.assertEquals(student.get().getEmail(), this.formData.getFirst("email"));
    }

    @Test
    void addStudentWithExistingEmail() throws Exception {

        this.formData.set("email", IStudentTest.STUDENT_EMAIL_EXAMPLE);
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("ExistingStudent"))));

    }

    /***************************************n STUDENT CLASS  TESTS ************************************************/
    @Test
    void addStudentWithInvalidClass() throws Exception {
        this.formData.set("studentClass", "wrongFunction");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("StudentClassNotFound"))));


    }

    @Test
    void addStudentWithWrongClass() throws Exception {
        this.formData.set("studentClass", "  ab ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("StudentClassNotFound"))));


    }

    @Test
    void addStudentWithEmptyClass() throws Exception {
        this.formData.set("studentClass", "  ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("StudentClassRequire"))));


    }

    @Test
    void addStudentWithNullClass() throws Exception {
        this.formData.set("studentClass", null);

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("StudentClassRequire"))));


    }


    @Test
    void addStudentWithOutClass() throws Exception {
        this.formData.remove("studentClass");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("StudentClassRequire"))));


    }

    /***************************************** TESTS   PASSWORD  ************************************************/


    @Test
    void addStudentWithTooLongPassword() throws Exception {
        this.formData.set("password", "a".repeat(91));

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LongPassword"))));


    }


    @Test
    void addStudentWithTooShortPassword() throws Exception {
        this.formData.set("password", "  ab ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("ShortPassword"))));


    }

    @Test
    void addStudentWithEmptyPassword() throws Exception {
        this.formData.set("password", "  ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("PasswordRequire"))));


    }

    @Test
    void addStudentWithNullPassword() throws Exception {
        this.formData.set("password", null);

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("PasswordRequire"))));


    }

    @Test
    void addStudentWithOutPassword() throws Exception {
        this.formData.remove("password");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("PasswordRequire"))));


    }


    /***************************************** TESTS  EMAIL  ************************************************/


    @Test
    void addStudentWithInvalidEmail2() throws Exception {
        this.formData.set("email", "halim.yahiaoui@outlook.fr");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidEmailFormat"))));


    }

    @Test
    void addStudentWithInvalidEmail() throws Exception {
        this.formData.set("email", "yugioh@yahoo.fr");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidEmailFormat"))));


    }

    @Test
    void addStudentWithTooLongEmail() throws Exception {
        this.formData.set("email", "a".repeat(1001));

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LongEmail"))));


    }


    @Test
    void addStudentWithTooShortEmail() throws Exception {
        this.formData.set("email", "  ab ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("ShortEmail"))));


    }

    @Test
    void addStudentWithNullEMAIL() throws Exception {
        this.formData.set("email", null);

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("EmailRequire"))));


    }

    @Test
    void addStudentWithOutEmail() throws Exception {
        this.formData.remove("email");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("EmailRequire"))));


    }


    /***************************************** TESTS   PHONES   ************************************************/

    @Test
    void addStudentWithInvalidPhoneFormat3() throws Exception {
        this.formData.set("phone", " +33076289514 ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidPhoneFormat"))));
    }

    @Test
    void addStudentWithInvalidPhoneFormat2() throws Exception {
        this.formData.set("phone", " 06319907853654 ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidPhoneFormat"))));


    }

    @Test
    void addStudentWithInvalidPhoneFormat() throws Exception {
        this.formData.set("phone", " oksfki ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidPhoneFormat"))));


    }

    @Test
    void addStudentWithEmptyPhone() throws Exception {
        this.formData.set("phone", "  . ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidPhoneFormat"))));


    }


    /***************************************** TESTS   TOWN   ************************************************/


    @Test
    void addStudentWithTooLongTown() throws Exception {
        this.formData.set("town", "a".repeat(1001));

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LongTown"))));


    }


    @Test
    void addStudentWithTooShortTown() throws Exception {
        this.formData.set("town", "  ab ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("ShortTown"))));


    }

    @Test
    void addStudentWithEmptyTown() throws Exception {
        this.formData.set("town", "  ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("TownRequire"))));


    }

    @Test
    void addStudentWithNullTown() throws Exception {
        this.formData.set("town", null);

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("TownRequire"))));


    }

    @Test
    void addStudentWithOutTown() throws Exception {
        this.formData.remove("town");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("TownRequire"))));


    }


    /***************************************** TESTS   BirthdateAsString  ************************************************/


    @Test
    void addStudentWithEmptyInvalidBirthdateAsStringFormat4() throws Exception {
        this.formData.set("birthdateAsString", "2000/07/18");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidBirthdateFormat"))));


    }

    @Test
    void addStudentWithEmptyInvalidBirthdateAsStringFormat3() throws Exception {
        this.formData.set("birthdateAsString", "18/07/2000");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidBirthdateFormat"))));


    }

    @Test
    void addStudentWithEmptyInvalidBirthdateAsStringFormat2() throws Exception {
        this.formData.set("birthdateAsString", "18-07-2000");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidBirthdateFormat"))));


    }


    @Test
    void addStudentWithEmptyInvalidBirthdateAsStringFormat() throws Exception {
        this.formData.set("birthdateAsString", "kzjrnozr,kfjfkrfkrf");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidBirthdateFormat"))));


    }


    @Test
    void addStudentWithEmptyBirthdateAsString() throws Exception {
        this.formData.set("birthdateAsString", "  ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("BirthdateRequire"))));


    }

    @Test
    void addStudentWithNullBirthdateAsString() throws Exception {
        this.formData.set("birthdateAsString", null);

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("BirthdateRequire"))));


    }

    @Test
    void addStudentWithOutBirthdayAsString() throws Exception {
        this.formData.remove("birthdateAsString");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("BirthdateRequire"))));


    }


    /***************************************** TESTS   LASTNAME  ************************************************/


    @Test
    void addStudentWithTooLongLastname() throws Exception {
        this.formData.set("lastname", "a".repeat(91));

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LongLastName"))));


    }


    @Test
    void addStudentWithTooShortLastname() throws Exception {
        this.formData.set("lastname", "  ab ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("ShortLastName"))));


    }

    @Test
    void addStudentWithEmptyLastname() throws Exception {
        this.formData.set("lastname", "  ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LastNameRequire"))));


    }

    @Test
    void addStudentWithNullLastname() throws Exception {
        this.formData.set("lastname", null);

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LastNameRequire"))));


    }

    @Test
    void addStudentWithOutLastname() throws Exception {
        this.formData.remove("lastname");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LastNameRequire"))));


    }


    /***************************************** TESTS   FIRSTNAME  ************************************************/


    @Test
    void addStudentWithTooLongFirstname() throws Exception {
        this.formData.set("firstname", "a".repeat(91));

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LongFirstName"))));


    }


    @Test
    void addStudentWithTooShortFirstname() throws Exception {
        this.formData.set("firstname", "  ab ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("ShortFirstName"))));


    }

    @Test
    void addStudentWithEmptyFirstname() throws Exception {
        this.formData.set("firstname", "  ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("FirstNameRequire"))));


    }

    @Test
    void addStudentWithNullFirstname() throws Exception {
        this.formData.set("firstname", null);

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("FirstNameRequire"))));


    }

    @Test
    void addStudentWithOutFirstname() throws Exception {


        this.formData.remove("firstname");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("FirstNameRequire"))));


    }


}







