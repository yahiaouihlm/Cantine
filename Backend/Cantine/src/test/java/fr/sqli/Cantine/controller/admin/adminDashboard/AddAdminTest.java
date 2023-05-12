package fr.sqli.Cantine.controller.admin.adminDashboard;

import fr.sqli.Cantine.controller.admin.AbstractContainerConfig;
import fr.sqli.Cantine.dao.AdminDao;
import fr.sqli.Cantine.dao.IFunctionDao;
import fr.sqli.Cantine.entity.FunctionEntity;
import org.junit.jupiter.api.Assertions;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;


@SpringBootTest
@AutoConfigureMockMvc
public class AddAdminTest  extends AbstractContainerConfig  implements  IAdminTest {

    @Autowired
    private IFunctionDao functionDao;
    @Autowired
    private AdminDao adminDao;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private Environment environment;

    private MockMultipartFile imageData;
    private MultiValueMap<String, String> formData;
    private FunctionEntity savedFunction;
    void initDataBase() {
        FunctionEntity function = new FunctionEntity();
        function.setName("Manager");
        this.savedFunction = this.functionDao.save(function);
    }

    void  cleanDtaBase() {
        this.adminDao.deleteAll();
        this.functionDao.deleteAll();
    }
    void initFormData() throws IOException {
        this.formData =new LinkedMultiValueMap<>();
        this.formData.add("firstname", "Halim");
        this.formData.add("lastname", "Yahiaoui");
        this.formData.add("email", "halim.yahiaoui@social.aston-ecole.com");
        this.formData.add("password", "test33");
        this.formData.add("birthdateAsString", "2000-07-18");
        this.formData.add("town", "paris");
        this.formData.add("address", "102  rue de cheret 75013 paris");
        this.formData.add("phone", "0631990189");
        this.formData.add("function","Manager");

        this.imageData = new MockMultipartFile(
                "image",                         // nom du champ de fichier
                IMAGE_NAME,          // nom du fichier
                "image/png",                    // type MIME
                new FileInputStream(IMAGE_FOR_TEST_PATH));

    }


    @BeforeEach
    void    init() throws IOException {
        cleanDtaBase();
        initDataBase();
        initFormData();
    }


    @Test
    void addAdmin () throws Exception {

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   ADMIN_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(ADMIN_ADDED_SUCCESSFULLY));

        var  admin = this.adminDao.findByEmail(this.formData.getFirst("email")).get();
        Assertions.assertEquals(admin.getFirstname(), this.formData.getFirst("firstname"));
        Assertions.assertEquals(admin.getLastname(), this.formData.getFirst("lastname"));
        Assertions.assertEquals(admin.getEmail(), this.formData.getFirst("email"));
        Assertions.assertFalse(admin.getPassword().equals(this.formData.getFirst("password"))); // password is encrypted

        var imageName =  admin.getImage().getImagename();
        Assertions.assertTrue(new File(ADMIN_IMAGE_PATH + imageName).delete());
    }




    @Test
    void addAdminWithOutImage () throws Exception {

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   ADMIN_SIGN_UP)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(ADMIN_ADDED_SUCCESSFULLY));

       var  admin = this.adminDao.findByEmail(this.formData.getFirst("email")).get();
         Assertions.assertEquals(admin.getFirstname(), this.formData.getFirst("firstname"));
            Assertions.assertEquals(admin.getLastname(), this.formData.getFirst("lastname"));
            Assertions.assertEquals(admin.getEmail(), this.formData.getFirst("email"));
           Assertions.assertFalse(admin.getPassword().equals(this.formData.getFirst("password"))); // password is encrypted

         var imageName =  admin.getImage().getImagename();
         Assertions.assertEquals(imageName, environment.getProperty("sqli.cantine.default.persons.admin.imagename"));

    }




    @Test

    void addAdminWithSameEmail() throws Exception {
        var email   =   "yahiaoui@social.aston-ecole.com";
        // save  in  database An  Admin  with  email  "yahiaoui@yahoo.fr"
        var  adminSaved  =  IAdminTest.createAdminWith(email, this.savedFunction);

        this.adminDao.save(adminSaved);

        // try to  save  another  admin  with  the  same  email
        this.formData.set("email", email);
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   ADMIN_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("ExistingAdmin"))));

    }






    /***************************************** TESTS   IMAGES  ************************************************/

    @Test
    void  addAdminWithWrongImageFormat() throws Exception {
        this.imageData = new MockMultipartFile(
                "image",                         // nom du champ de fichier
                IMAGE_NAME,          // nom du fichier
                "images/pdf",                    // type MIME
                new FileInputStream(IMAGE_FOR_TEST_PATH));

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   ADMIN_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidImageFormat"))));


    }

    @Test
    void  addAdminWithInvalidImageFormat() throws Exception {
        this.imageData = new MockMultipartFile(
                "image",                         // nom du champ de fichier
                IMAGE_NAME,          // nom du fichier
                "images/svg",                    // type MIME
                new FileInputStream(IMAGE_FOR_TEST_PATH));

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   ADMIN_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidImageFormat"))));


    }

    /***************************************** TESTS   Function  ************************************************/
    @Test
    void  addAdminWithInvalidFunction() throws Exception {
        this.formData.set("function",  "wrongFunction");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   ADMIN_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("FunctionNotFound"))));


    }
    @Test
    void  addAdminWithWrongFunction() throws Exception {
        this.formData.set("function",  "  ab ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   ADMIN_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("FunctionNotFound"))));


    }

    @Test
    void  addAdminWithEmptyFunction() throws Exception {
        this.formData.set("function",  "  ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   ADMIN_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("FunctionRequire"))));


    }
    @Test
    void  addAdminWithNullFunction() throws Exception {
        this.formData.set("function",  null);

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   ADMIN_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("FunctionRequire"))));


    }
    @Test
    void  addAdminWithOutFunction() throws Exception {
        this.formData.remove("function");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   ADMIN_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("FunctionRequire"))));


    }



    /***************************************** TESTS   PASSWORD  ************************************************/

    @Test
    void  addAdminWithTooLongPassword() throws Exception {
        this.formData.set("password",  "a".repeat(91));

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   ADMIN_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LongPassword"))));


    }



    @Test
    void  addAdminWithTooShortPassword() throws Exception {
        this.formData.set("password",  "  ab ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   ADMIN_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("ShortPassword"))));


    }

    @Test
    void  addAdminWithEmptyPassword() throws Exception {
        this.formData.set("password",  "  ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   ADMIN_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("PasswordRequire"))));


    }
    @Test
    void  addAdminWithNullPassword() throws Exception {
        this.formData.set("password",  null);

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   ADMIN_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("PasswordRequire"))));


    }
    @Test
    void  addAdminWithOutPassword() throws Exception {
        this.formData.remove("password");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   ADMIN_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("PasswordRequire"))));


    }





    /***************************************** TESTS  EMAIL  ************************************************/

    @Test
    void  addAdminWithInvalidEmail2() throws Exception {
        this.formData.set("email",  "halim.yahiaoui@outlook.fr");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   ADMIN_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidEmailFormat"))));


    }
    @Test
    void  addAdminWithInvalidEmail() throws Exception {
        this.formData.set("email",  "yugioh@yahoo.fr");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   ADMIN_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidEmailFormat"))));


    }
    @Test
    void  addAdminWithTooLongEmail() throws Exception {
        this.formData.set("email",  "a".repeat(1001));

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   ADMIN_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LongEmail"))));


    }



    @Test
    void  addAdminWithTooShortEmail() throws Exception {
        this.formData.set("email",  "  ab ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   ADMIN_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("ShortEmail"))));


    }
    @Test
    void addAdminWithNullEMAIL() throws Exception {
        this.formData.set("email",  null );

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   ADMIN_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("EmailRequire"))));


    }

    @Test
    void addAdminWithOutEmail() throws Exception {
        this.formData.remove("email");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   ADMIN_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("EmailRequire"))));


    }





    /***************************************** TESTS   PHONES   ************************************************/


    @Test
    void  addAdminWithInvalidPhoneFormat3() throws Exception {
        this.formData.set("phone", " +33076289514 ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, ADMIN_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidPhoneFormat"))));
    }

        @Test
    void  addAdminWithInvalidPhoneFormat2() throws Exception {
        this.formData.set("phone",  " 06319907853654 ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   ADMIN_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidPhoneFormat"))));


    }

    @Test
    void  addAdminWithInvalidPhoneFormat() throws Exception {
        this.formData.set("phone",  " oksfki ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   ADMIN_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidPhoneFormat"))));


    }
    @Test
    void  addAdminWithEmptyPhone() throws Exception {
        this.formData.set("phone",  "  ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   ADMIN_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("PhoneRequire"))));


    }
    @Test
    void  addAdminWithNullPhone() throws Exception {
        this.formData.set("phone",  null);

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   ADMIN_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("PhoneRequire"))));


    }
    @Test
    void addAdminWithOutPhone() throws Exception {
        this.formData.remove("phone");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   ADMIN_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("PhoneRequire"))));


    }







    /***************************************** TESTS   TOWN   ************************************************/

    @Test
    void  addAdminWithTooLongAddress() throws Exception {
        this.formData.set("address",  "a".repeat(3001));

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   ADMIN_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LongAddress"))));


    }



    @Test
    void  addAdminWithTooShortAddress() throws Exception {
        this.formData.set("address",  "  ab ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   ADMIN_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("ShortAddress"))));


    }

    @Test
    void  addAdminWithEmptyAddress() throws Exception {
        this.formData.set("address",  "  ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   ADMIN_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("AddressRequire"))));


    }
    @Test
    void  addAdminWithNullAddress() throws Exception {
        this.formData.set("address",  null);

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   ADMIN_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("AddressRequire"))));


    }
    @Test
    void addAdminWithOutAddress() throws Exception {
        this.formData.remove("address");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   ADMIN_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("AddressRequire"))));


    }










    /***************************************** TESTS   TOWN   ************************************************/

    @Test
    void  addAdminWithTooLongTown() throws Exception {
        this.formData.set("town",  "a".repeat(1001));

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   ADMIN_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LongTown"))));


    }



    @Test
    void  addAdminWithTooShortTown() throws Exception {
        this.formData.set("town",  "  ab ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   ADMIN_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("ShortTown"))));


    }

    @Test
    void  addAdminWithEmptyTown() throws Exception {
        this.formData.set("town",  "  ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   ADMIN_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("TownRequire"))));


    }
    @Test
    void  addAdminWithNullTown() throws Exception {
        this.formData.set("town",  null);

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   ADMIN_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("TownRequire"))));


    }
    @Test
    void  addAdminWithOutTown() throws Exception {
        this.formData.remove("town");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   ADMIN_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("TownRequire"))));


    }











    /***************************************** TESTS   LASTNAME  ************************************************/

    @Test
    void  addAdminWithEmptyInvalidBirthdateAsStringFormat4() throws Exception {
        this.formData.set("birthdateAsString",  "2000/07/18");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   ADMIN_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidBirthdateFormat"))));


    }
    @Test
    void  addAdminWithEmptyInvalidBirthdateAsStringFormat3() throws Exception {
        this.formData.set("birthdateAsString",  "18/07/2000");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   ADMIN_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidBirthdateFormat"))));


    }

    @Test
    void  addAdminWithEmptyInvalidBirthdateAsStringFormat2() throws Exception {
        this.formData.set("birthdateAsString",  "18-07-2000");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   ADMIN_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidBirthdateFormat"))));


    }



    @Test
    void  addAdminWithEmptyInvalidBirthdateAsStringFormat() throws Exception {
        this.formData.set("birthdateAsString",  "kzjrnozr,kfjfkrfkrf");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   ADMIN_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidBirthdateFormat"))));


    }


    @Test
    void  addAdminWithEmptyBirthdateAsString() throws Exception {
        this.formData.set("birthdateAsString",  "  ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   ADMIN_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("BirthdateRequire"))));


    }
    @Test
    void  addAdminWithNullBirthdateAsString() throws Exception {
        this.formData.set("birthdateAsString",  null);

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   ADMIN_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("BirthdateRequire"))));


    }
    @Test
    void  addAdminWithOutBirthdayAsString() throws Exception {
        this.formData.remove("birthdateAsString");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   ADMIN_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("BirthdateRequire"))));


    }


    /***************************************** TESTS   LASTNAME  ************************************************/

    @Test
    void  addAdminWithTooLongLastname() throws Exception {
        this.formData.set("lastname",  "a".repeat(91));

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   ADMIN_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LongLastName"))));


    }



    @Test
    void  addAdminWithTooShortLastname() throws Exception {
        this.formData.set("lastname",  "  ab ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   ADMIN_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("ShortLastName"))));


    }

    @Test
    void  addAdminWithEmptyLastname() throws Exception {
        this.formData.set("lastname",  "  ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   ADMIN_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LastNameRequire"))));


    }
    @Test
    void  addAdminWithNullLastname() throws Exception {
        this.formData.set("lastname",  null);

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   ADMIN_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LastNameRequire"))));


    }
    @Test
    void  addAdminWithOutLastname() throws Exception {
        this.formData.remove("lastname");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   ADMIN_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LastNameRequire"))));


    }






    /***************************************** TESTS   FIRSTNAME  ************************************************/

    @Test
    void  addAdminWithTooLongFirstname() throws Exception {
        this.formData.set("firstname",  "a".repeat(91));

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   ADMIN_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LongFirstName"))));


    }



    @Test
    void  addAdminWithTooShortFirstname() throws Exception {
        this.formData.set("firstname",  "  ab ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   ADMIN_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("ShortFirstName"))));


    }

    @Test
    void  addAdminWithEmptyFirstname() throws Exception {
        this.formData.set("firstname",  "  ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   ADMIN_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("FirstNameRequire"))));


    }
    @Test
    void  addAdminWithNullFirstname() throws Exception {
        this.formData.set("firstname",  null);

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   ADMIN_SIGN_UP)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("FirstNameRequire"))));


    }
    @Test
    void  addAdminWithOutFirstname() throws Exception {
        this.formData.remove("firstname");

     var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST,   ADMIN_SIGN_UP)
             .file(this.imageData)
             .params(this.formData)
             .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


     result.andExpect(MockMvcResultMatchers.status().isBadRequest())
             .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("FirstNameRequire"))));


    }

}
