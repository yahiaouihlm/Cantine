package fr.sqli.cantine.controller.users.student;

import fr.sqli.cantine.controller.AbstractContainerConfig;
import fr.sqli.cantine.controller.AbstractLoginRequest;
import fr.sqli.cantine.dao.*;
import fr.sqli.cantine.entity.ImageEntity;
import fr.sqli.cantine.entity.StudentClassEntity;
import fr.sqli.cantine.entity.StudentEntity;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
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
import java.nio.file.Files;


@SpringBootTest
@AutoConfigureMockMvc
public class UpdateStudentTest extends AbstractContainerConfig implements IStudentTest {


    @Autowired
    private IConfirmationTokenDao iConfirmationTokenDao;

    @Autowired
    private IAdminDao adminDao;
    @Autowired
    private IFunctionDao functionDao;
    private IStudentClassDao studentClassDao;
    private IStudentDao studentDao;
    private Environment env;
    private MockMultipartFile imageData;
    private MockMvc mockMvc;
    private StudentEntity studentEntity;
    private MultiValueMap<String, String> formData;
    private String authorizationToken;

    @Autowired
    public UpdateStudentTest(IStudentDao iStudentDao, IStudentClassDao iStudentClassDao, MockMvc mockMvc, Environment env) throws Exception {
        this.studentDao = iStudentDao;
        this.studentClassDao = iStudentClassDao;
        this.mockMvc = mockMvc;
        this.env = env;
        cleanDataBase();
        initDataBase();
        initFormData();

    }

    void initDataBase() throws Exception {
        this.studentEntity = AbstractLoginRequest.saveAStudent(this.studentDao, this.studentClassDao);
        this.authorizationToken = AbstractLoginRequest.getStudentBearerToken(this.mockMvc);
    }

    void cleanDataBase() {
        this.studentDao.deleteAll();
        this.studentClassDao.deleteAll();


    }

    void initFormData() throws IOException {
        this.formData = new LinkedMultiValueMap<>();
        this.formData.add("uuid", java.util.UUID.randomUUID().toString());
        this.formData.add("firstname", "Birus");
        this.formData.add("lastname", "Samaa");
        /*        this.formData.add("email", "halim.yahiaoui@social.aston-ecole.com");*/
        /*      this.formData.add("password", "test33");*/
        this.formData.add("birthdateAsString", "1999-07-18");
        this.formData.add("town", "paris");
        this.formData.add("phone", "0631990100");
        this.formData.add("studentClass", this.studentEntity.getStudentClass().getName());

        this.imageData = new MockMultipartFile(
                "image",                         // nom du champ de fichier
                IMAGE_NAME,          // nom du fichier
                "image/png",                    // type MIME
                new FileInputStream(IMAGE_FOR_TEST_PATH));

    }


    @BeforeAll
    static void copyImageTestFromTestDirectoryToImageStudentDirectory() throws IOException {
        String source = IMAGE_TEST_DIRECTORY_PATH + IMAGE_FOR_TEST_NAME;
        String destination = STUDENT_IMAGE_PATH + IMAGE_FOR_TEST_NAME;
        File sourceFile = new File(source);
        File destFile = new File(destination);
        Files.copy(sourceFile.toPath(), destFile.toPath());
    }

    @AfterAll
    static void deleteImageTestIFExists() throws IOException {
        String location = STUDENT_IMAGE_PATH + IMAGE_FOR_TEST_NAME;
        File destFile = new File(location);
        if (destFile.exists())
            Files.delete(destFile.toPath());
    }


    @Test
    void updateStudentWithImage() throws Exception {
        this.formData.set("uuid", this.studentEntity.getUuid());
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(super.responseMessage(responseMap.get("StudentUpdatedSuccessfully"))));

        var student = this.studentDao.findById(this.studentEntity.getId());
        Assertions.assertTrue(student.isPresent());
        Assertions.assertEquals(student.get().getFirstname(), this.formData.getFirst("firstname"));
        Assertions.assertEquals(student.get().getLastname(), this.formData.getFirst("lastname"));
        Assertions.assertEquals(student.get().getTown(), this.formData.getFirst("town"));

        String path = this.env.getProperty("sqli.cantine.image.student.path");

        path = path + "/" + student.get().getImage().getImagename();

        Assertions.assertTrue(
                new File(path).delete()
        );


    }

    @Test
    void updateStudentWithOutImage() throws Exception {

        this.formData.set("uuid", this.studentEntity.getUuid());
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_STUDENT_INFO)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(super.responseMessage(responseMap.get("StudentUpdatedSuccessfully"))));


        var student = this.studentDao.findById(this.studentEntity.getId());
        Assertions.assertTrue(student.isPresent());
        Assertions.assertEquals(student.get().getFirstname(), this.formData.getFirst("firstname"));
        Assertions.assertEquals(student.get().getLastname(), this.formData.getFirst("lastname"));
        Assertions.assertEquals(student.get().getTown(), this.formData.getFirst("town"));
        Assertions.assertNotNull(student.get().getImage());


    }

    @Test
    void updateStudentWithAdminAuthToken() throws Exception {

        AbstractLoginRequest.saveAdmin(this.adminDao, this.functionDao);
        String adminAuthToken = AbstractLoginRequest.getAdminBearerToken(this.mockMvc);


        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_STUDENT_INFO)

                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, adminAuthToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isForbidden());

    }


    @Test
    void updateStudentWithNotFoundID() throws Exception {

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("StudentNotFound"))));

    }


    /***************************************** TESTS   CLASS   ************************************************/

    @Test
    void updateStudentWithInvalidClass() throws Exception {
        this.formData.set("studentClass", "wrongFunction");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("StudentClassNotFound"))));


    }

    @Test
    void updateStudentWithWrongClass() throws Exception {
        this.formData.set("studentClass", "  ab ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("StudentClassNotFound"))));


    }

    @Test
    void updateStudentWithEmptyClass() throws Exception {
        this.formData.set("studentClass", "  ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("StudentClassRequire"))));


    }

    @Test
    void updateStudentWithNullClass() throws Exception {
        this.formData.set("studentClass", null);

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("StudentClassRequire"))));


    }


    @Test
    void updateStudentWithOutClass() throws Exception {
        this.formData.remove("studentClass");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("StudentClassRequire"))));


    }


    /***************************************** TESTS   PHONES   ************************************************/

    @Test
    void updateStudentWithInvalidPhoneFormat3() throws Exception {
        this.formData.set("phone", " +33076289514 ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidPhoneFormat"))));
    }

    @Test
    void updateStudentWithInvalidPhoneFormat2() throws Exception {
        this.formData.set("phone", " 06319907853654 ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidPhoneFormat"))));


    }

    @Test
    void updateStudentWithInvalidPhoneFormat() throws Exception {
        this.formData.set("phone", " oksfki ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidPhoneFormat"))));


    }

    @Test
    void updateStudentWithEmptyPhone() throws Exception {
        this.formData.set("phone", "  . ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidPhoneFormat"))));


    }


    /***************************************** TESTS   TOWN   ************************************************/


    @Test
    void updateStudentWithTooLongTown() throws Exception {
        this.formData.set("town", "a".repeat(1001));

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LongTown"))));


    }


    @Test
    void updateStudentWithTooShortTown() throws Exception {
        this.formData.set("town", "  ab ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("ShortTown"))));


    }

    @Test
    void updateStudentWithEmptyTown() throws Exception {
        this.formData.set("town", "  ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("TownRequire"))));


    }

    @Test
    void updateStudentWithNullTown() throws Exception {
        this.formData.set("town", null);

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("TownRequire"))));


    }

    @Test
    void updateStudentWithOutTown() throws Exception {
        this.formData.remove("town");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("TownRequire"))));


    }


    /***************************************** TESTS   BirthdateAsString  ************************************************/


    @Test
    void updateStudentWithEmptyInvalidBirthdateAsStringFormat4() throws Exception {
        this.formData.set("birthdateAsString", "2000/07/18");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidBirthdateFormat"))));


    }

    @Test
    void updateStudentWithEmptyInvalidBirthdateAsStringFormat3() throws Exception {
        this.formData.set("birthdateAsString", "18/07/2000");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidBirthdateFormat"))));


    }

    @Test
    void updateStudentWithEmptyInvalidBirthdateAsStringFormat2() throws Exception {
        this.formData.set("birthdateAsString", "18-07-2000");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidBirthdateFormat"))));


    }

    @Test
    void updateStudentWithEmptyInvalidBirthdateAsStringFormat() throws Exception {
        this.formData.set("birthdateAsString", "kzjrnozr,kfjfkrfkrf");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidBirthdateFormat"))));


    }


    @Test
    void updateStudentWithEmptyBirthdateAsString() throws Exception {
        this.formData.set("birthdateAsString", "  ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("BirthdateRequire"))));


    }

    @Test
    void updateStudentWithNullBirthdateAsString() throws Exception {
        this.formData.set("birthdateAsString", null);

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("BirthdateRequire"))));


    }

    @Test
    void updateStudentWithOutBirthdayAsString() throws Exception {
        this.formData.remove("birthdateAsString");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("BirthdateRequire"))));


    }

    /***************************************** TESTS   LASTNAME  ************************************************/


    @Test
    void updateStudentWithTooLongLastname() throws Exception {
        this.formData.set("lastname", "a".repeat(91));

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LongLastName"))));


    }


    @Test
    void updateStudentWithTooShortLastname() throws Exception {
        this.formData.set("lastname", "  ab ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("ShortLastName"))));


    }

    @Test
    void updateStudentWithEmptyLastname() throws Exception {
        this.formData.set("lastname", "  ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LastNameRequire"))));


    }

    @Test
    void updateStudentWithNullLastname() throws Exception {
        this.formData.set("lastname", null);

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LastNameRequire"))));


    }

    @Test
    void updateStudentWithOutLastname() throws Exception {
        this.formData.remove("lastname");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LastNameRequire"))));


    }


    /***************************************** TESTS   FIRSTNAME  ************************************************/


    @Test
    void updateStudentWithTooLongFirstname() throws Exception {
        this.formData.set("firstname", "a".repeat(91));

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LongFirstName"))));


    }


    @Test
    void updateStudentWithTooShortFirstname() throws Exception {
        this.formData.set("firstname", "  ab ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("ShortFirstName"))));


    }

    @Test
    void updateStudentWithEmptyFirstname() throws Exception {
        this.formData.set("firstname", "  ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("FirstNameRequire"))));


    }

    @Test
    void updateStudentWithNullFirstname() throws Exception {
        this.formData.set("firstname", null);

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("FirstNameRequire"))));


    }

    @Test
    void updateStudentWithOutFirstname() throws Exception {
        this.formData.remove("firstname");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("FirstNameRequire"))));


    }


    /***************************************** TESTS   ID  ************************************************/

    @Test
    void updateStudentWithInvalidId() throws Exception {
        this.formData.set("uuid", "knaezfk");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidUuid"))));

    }


    @Test
    void updateStudentWithWrongId2() throws Exception {
        this.formData.set("uuid", "-1.2");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidUuid"))));

    }

    @Test
    void updateStudentWithEmptyId() throws Exception {
        this.formData.set("uuid", "");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidUuid"))));

    }

    @Test
    void updateStudentWithNullId() throws Exception {
        this.formData.set("uuid", null);

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidUuid"))));

    }

    @Test
    void updateStudentWithOutAuthToken() throws Exception {
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, UPDATE_STUDENT_INFO)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isUnauthorized());

    }


}

