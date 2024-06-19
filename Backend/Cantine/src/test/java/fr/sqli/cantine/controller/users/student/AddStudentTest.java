package fr.sqli.cantine.controller.users.student;

import fr.sqli.cantine.controller.AbstractContainerConfig;
import fr.sqli.cantine.dao.IConfirmationTokenDao;
import fr.sqli.cantine.dao.IStudentClassDao;
import fr.sqli.cantine.dao.IStudentDao;
import fr.sqli.cantine.entity.StudentClassEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.MultiValueMap;

@SpringBootTest
@AutoConfigureMockMvc
public class AddStudentTest  extends AbstractContainerConfig implements IStudentTest {
    @Autowired
    private IStudentClassDao studentClassDao;
    @Autowired
    private IStudentDao studentDao;
    @Autowired
    private Environment  env;

    @Autowired
    private IConfirmationTokenDao iConfirmationTokenDao;

    private  MockMultipartFile imageData;
    @Autowired
    private  IStudentClassDao iStudentClassDao ;

    @Autowired
    private MockMvc mockMvc;

    private MultiValueMap <String, String> formData;

    private   StudentClassEntity studentClassEntity;
/*
    void  initDatabase() {
        this.studentClassEntity = new StudentClassEntity();
        studentClassEntity.setName("JAVA SQLI");
        this.studentClassDao.save(studentClassEntity);
    }
    void  cleanDatabase() {
        this.studentDao.deleteAll();
        this.studentClassDao.deleteAll();
        this.iConfirmationTokenDao.deleteAll();
    }

    void initFormData() throws IOException {
        this.formData =new LinkedMultiValueMap<>();
        this.formData.add("firstname", "Halim");
        this.formData.add("lastname", "Yahiaoui");
        this.formData.add("email", "halim.yahiaoui@social.aston-ecole.com");
        this.formData.add("password", "test33");
        this.formData.add("birthdateAsString", "2000-07-18");
        this.formData.add("town", "paris");
        this.formData.add("phone", "0631990189");
        this.formData.add("studentClass","JAVA SQLI");

        this.imageData = new MockMultipartFile(
                "image",                         // nom du champ de fichier
                IMAGE_NAME,          // nom du fichier
                "image/png",                    // type MIME
                new FileInputStream(IMAGE_FOR_TEST_PATH));

    }


    @BeforeEach
    void  init () throws IOException {
        cleanDatabase();
        initDatabase();
        initFormData();
    }

    @Test
    void assStudentWithImageAndPhone() throws Exception {

         var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(STUDENT_SIGNED_UP_SUCCESSFULLY));

        var student = this.studentDao.findByEmail(this.formData.getFirst("email"));
        Assertions.assertTrue(student.isPresent());
        Assertions.assertEquals(student.get().getFirstname(), this.formData.getFirst("firstname"));
        Assertions.assertEquals(student.get().getLastname(), this.formData.getFirst("lastname"));
        Assertions.assertEquals(student.get().getEmail(), this.formData.getFirst("email"));
        Assertions.assertNotNull(student.get().getImage());

        String  path  =  this.env.getProperty("sqli.cantine.image.student.path");

        path = path + "/" + student.get().getImage().getImagename();
        Assertions.assertTrue(
                new File(path).delete()
        );


    }
    //zhefjozhefjznerofknzoklb ///  Image  par defaut  losque il est  u

    @Test
    void addStudentWithOutImageAndPhone  () throws Exception {
        this.formData.remove("phone");
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   STUDENT_SIGN_UP)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(STUDENT_SIGNED_UP_SUCCESSFULLY));

        var student = this.studentDao.findByEmail(this.formData.getFirst("email"));
        Assertions.assertTrue(student.isPresent());
        Assertions.assertEquals(student.get().getFirstname(), this.formData.getFirst("firstname"));
        Assertions.assertEquals(student.get().getLastname(), this.formData.getFirst("lastname"));
        Assertions.assertEquals(student.get().getEmail(), this.formData.getFirst("email"));
    }

    @Test
    void addStudentWithExistingEmail() throws Exception {

        // create student with same email
        var student =  IStudentTest.createStudentEntity("halim.yahiaoui@social.aston-ecole.com" ,this.studentClassEntity);
        this.studentDao.save(student);


        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("ExistingStudent"))));

    }


    @Test
    void addStudentWithInvalidClass() throws Exception {
        this.formData.set("studentClass",  "wrongFunction");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("StudentClassNotFound"))));


    }
    @Test
    void  addStudentWithWrongClass() throws Exception {
        this.formData.set("studentClass",  "  ab ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("StudentClassNotFound"))));


    }

    @Test
    void  addStudentWithEmptyClass() throws Exception {
        this.formData.set("studentClass",  "  ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("StudentClassRequire"))));


    }
    @Test
    void  addStudentWithNullClass() throws Exception {
        this.formData.set("studentClass",  null);

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("StudentClassRequire"))));


    }


    @Test
    void  addStudentWithOutClass() throws Exception {
        this.formData.remove("studentClass");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("StudentClassRequire"))));


    }



    */
/***************************************** TESTS   PASSWORD  ************************************************//*


    @Test
    void  addStudentWithTooLongPassword() throws Exception {
        this.formData.set("password",  "a".repeat(91));

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LongPassword"))));


    }


    @Test
    void  addStudentWithTooShortPassword() throws Exception {
        this.formData.set("password",  "  ab ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("ShortPassword"))));


    }

    @Test
    void  addStudentWithEmptyPassword() throws Exception {
        this.formData.set("password",  "  ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("PasswordRequire"))));


    }
    @Test
    void  addStudentWithNullPassword() throws Exception {
        this.formData.set("password",  null);

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("PasswordRequire"))));


    }
    @Test
    void  addStudentWithOutPassword() throws Exception {
        this.formData.remove("password");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("PasswordRequire"))));


    }






    */
/***************************************** TESTS  EMAIL  ************************************************//*


    @Test
    void  addStudentWithInvalidEmail2() throws Exception {
        this.formData.set("email",  "halim.yahiaoui@outlook.fr");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidEmailFormat"))));


    }
    @Test
    void  addStudentWithInvalidEmail() throws Exception {
        this.formData.set("email",  "yugioh@yahoo.fr");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidEmailFormat"))));


    }
    @Test
    void  addStudentWithTooLongEmail() throws Exception {
        this.formData.set("email",  "a".repeat(1001));

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LongEmail"))));


    }



    @Test
    void  addStudentWithTooShortEmail() throws Exception {
        this.formData.set("email",  "  ab ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("ShortEmail"))));


    }
    @Test
    void addStudentWithNullEMAIL() throws Exception {
        this.formData.set("email",  null );

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("EmailRequire"))));


    }

    @Test
    void addStudentWithOutEmail() throws Exception {
        this.formData.remove("email");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("EmailRequire"))));


    }



    */
/***************************************** TESTS   PHONES   ************************************************//*

    @Test
    void  addStudentWithInvalidPhoneFormat3() throws Exception {
        this.formData.set("phone", " +33076289514 ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidPhoneFormat"))));
    }

    @Test
    void  addStudentWithInvalidPhoneFormat2() throws Exception {
        this.formData.set("phone",  " 06319907853654 ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidPhoneFormat"))));


    }

    @Test
    void  addStudentWithInvalidPhoneFormat() throws Exception {
        this.formData.set("phone",  " oksfki ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidPhoneFormat"))));


    }
    @Test
    void  addStudentWithEmptyPhone() throws Exception {
        this.formData.set("phone",  "  . ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidPhoneFormat"))));


    }



    */
/***************************************** TESTS   TOWN   ************************************************//*


    @Test
    void  addStudentWithTooLongTown() throws Exception {
        this.formData.set("town",  "a".repeat(1001));

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LongTown"))));


    }



    @Test
    void  addStudentWithTooShortTown() throws Exception {
        this.formData.set("town",  "  ab ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("ShortTown"))));


    }

    @Test
    void  addStudentWithEmptyTown() throws Exception {
        this.formData.set("town",  "  ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("TownRequire"))));


    }
    @Test
    void  addStudentWithNullTown() throws Exception {
        this.formData.set("town",  null);

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("TownRequire"))));


    }
    @Test
    void  addStudentWithOutTown() throws Exception {
        this.formData.remove("town");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("TownRequire"))));


    }









    */
/***************************************** TESTS   BirthdateAsString  ************************************************//*


    @Test
    void  addStudentWithEmptyInvalidBirthdateAsStringFormat4() throws Exception {
        this.formData.set("birthdateAsString",  "2000/07/18");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidBirthdateFormat"))));


    }
    @Test
    void  addStudentWithEmptyInvalidBirthdateAsStringFormat3() throws Exception {
        this.formData.set("birthdateAsString",  "18/07/2000");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidBirthdateFormat"))));


    }

    @Test
    void  addStudentWithEmptyInvalidBirthdateAsStringFormat2() throws Exception {
        this.formData.set("birthdateAsString",  "18-07-2000");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidBirthdateFormat"))));


    }



    @Test
    void  addStudentWithEmptyInvalidBirthdateAsStringFormat() throws Exception {
        this.formData.set("birthdateAsString",  "kzjrnozr,kfjfkrfkrf");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidBirthdateFormat"))));


    }


    @Test
    void  addStudentWithEmptyBirthdateAsString() throws Exception {
        this.formData.set("birthdateAsString",  "  ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("BirthdateRequire"))));


    }
    @Test
    void  addStudentWithNullBirthdateAsString() throws Exception {
        this.formData.set("birthdateAsString",  null);

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("BirthdateRequire"))));


    }
    @Test
    void  addStudentWithOutBirthdayAsString() throws Exception {
        this.formData.remove("birthdateAsString");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("BirthdateRequire"))));


    }







    */
/***************************************** TESTS   LASTNAME  ************************************************//*


    @Test
    void  addStudentWithTooLongLastname() throws Exception {
        this.formData.set("lastname",  "a".repeat(91));

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LongLastName"))));


    }



    @Test
    void  addStudentWithTooShortLastname() throws Exception {
        this.formData.set("lastname",  "  ab ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("ShortLastName"))));


    }

    @Test
    void  addStudentWithEmptyLastname() throws Exception {
        this.formData.set("lastname",  "  ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LastNameRequire"))));


    }
    @Test
    void  addStudentWithNullLastname() throws Exception {
        this.formData.set("lastname",  null);

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LastNameRequire"))));


    }
    @Test
    void  addStudentWithOutLastname() throws Exception {
        this.formData.remove("lastname");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LastNameRequire"))));


    }






    */
/***************************************** TESTS   FIRSTNAME  ************************************************//*


    @Test
    void  addStudentWithTooLongFirstname() throws Exception {
        this.formData.set("firstname",  "a".repeat(91));

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LongFirstName"))));


    }



    @Test
    void  addStudentWithTooShortFirstname() throws Exception {
        this.formData.set("firstname",  "  ab ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("ShortFirstName"))));


    }

    @Test
    void  addStudentWithEmptyFirstname() throws Exception {
        this.formData.set("firstname",  "  ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("FirstNameRequire"))));


    }
    @Test
    void  addStudentWithNullFirstname() throws Exception {
        this.formData.set("firstname",  null);

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("FirstNameRequire"))));


    }
    @Test
    void  addStudentWithOutFirstname() throws Exception {


        this.formData.remove("firstname");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   STUDENT_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("FirstNameRequire"))));


    }
*/








}







