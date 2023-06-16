package fr.sqli.Cantine.controller.admin.adminDashboard.account;

import fr.sqli.Cantine.controller.AbstractContainerConfig;
import fr.sqli.Cantine.dao.IAdminDao;
import fr.sqli.Cantine.dao.IConfirmationTokenDao;
import fr.sqli.Cantine.dao.IFunctionDao;
import fr.sqli.Cantine.dao.IImageDao;
import fr.sqli.Cantine.entity.AdminEntity;
import fr.sqli.Cantine.entity.FunctionEntity;
import fr.sqli.Cantine.entity.ImageEntity;
import org.junit.jupiter.api.*;
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
import java.nio.file.Files;


@SpringBootTest
@AutoConfigureMockMvc
public class UpdateAdminInformation  extends AbstractContainerConfig implements  IAdminTest {

    private  final  String paramReq = "?"+"idAdmin"+"=";
    @Autowired
    private IFunctionDao functionDao;
    @Autowired
    private IAdminDao adminDao;

    @Autowired
    private IConfirmationTokenDao iConfirmationTokenDao;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IImageDao   imageDao;
    @Autowired
    private Environment environment;

    private MockMultipartFile imageData;
    private MultiValueMap<String, String> formData;
    private FunctionEntity savedFunction;
    private AdminEntity savedAdmin;

    @BeforeAll
    static void  copyImageTestFromTestDirectoryToImageMenuDirectory() throws IOException {
        String source = IMAGE_MEAL_TEST_DIRECTORY_PATH + IMAGE_ADMIN_FOR_TEST_NAME;
        String destination = ADMIN_IMAGE_PATH + IMAGE_ADMIN_FOR_TEST_NAME;
        File sourceFile = new File(source);
        File destFile = new File(destination);
        Files.copy(sourceFile.toPath(), destFile.toPath());
    }

    @AfterAll
    static void deleteImageTestIFExists() throws IOException {
        String location = ADMIN_IMAGE_PATH + IMAGE_ADMIN_FOR_TEST_NAME;
        File destFile = new File(location);
        if (destFile.exists())
            Files.delete(destFile.toPath());
    }


    void initDataBase() {
        FunctionEntity function = new FunctionEntity();
        function.setName("Manager");
        this.savedFunction = this.functionDao.save(function);
        var admin  =  IAdminTest.createAdminWith("halim.yahiaoui@social.aston-ecole.com",  this.savedFunction);
        this.savedAdmin =   this.adminDao.save(admin);
    }
    void  cleanDtaBase() {
        this.iConfirmationTokenDao.deleteAll();// remove  all confirmationtokenEntity  to  keep  the  database  Integrity
        this.adminDao.deleteAll();
        this.functionDao.deleteAll();

    }
    void initFormData() throws IOException {
        this.formData =new LinkedMultiValueMap<>();
        this.formData.add("id", "1");
        this.formData.add("firstname", "Halim");
        this.formData.add("lastname", "Yahiaoui");
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
    /***************************************** TESTS  UPDATE ADMIN  WITHOUT IMAGE  ************************************************/

    @Test
    void updateAdmin() throws Exception {
        this.formData.set("firstname", "Halim-Updated");
        this.formData.set("lastname", "Yahiaoui-Updated");
        this.formData.set("birthdateAsString", "2000-07-18");
        this.formData.set("town", "chicago");
        this.formData.set("address", "North Bergen New Jersey USA");
        this.formData.set("phone", "0631800190");

        var  idMealToUpdate =  this.adminDao.findAll().get(0).getId();
        this.formData.set("id" , String.valueOf(idMealToUpdate)  );

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, ADMIN_UPDATE_INFO )
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(MockMvcResultMatchers.content().string(ADMIN_INFO_UPDATED_SUCCESSFULLY));

        var adminUpdated = this.adminDao.findById(idMealToUpdate).get();


        Assertions.assertEquals(this.formData.get("firstname").get(0), adminUpdated.getFirstname());
        Assertions.assertEquals(this.formData.get("lastname").get(0), adminUpdated.getLastname());
        Assertions.assertEquals(this.formData.get("town").get(0), adminUpdated.getTown());
        Assertions.assertEquals(this.formData.get("address").get(0), adminUpdated.getAddress());
        Assertions.assertEquals(this.formData.get("phone").get(0), adminUpdated.getPhone());

        var imageUpdated = new File(ADMIN_IMAGE_PATH + adminUpdated.getImage().getImagename());

        Assertions.assertTrue(imageUpdated.delete());

    }

    /***************************************** TESTS  UPDATE ADMIN  WITHOUT IMAGE  ************************************************/

    @Test
    void updateAdminWithDefaultImage() throws Exception {
       cleanDtaBase();

       // make  a  new  admin  with  a default  image
       var defaultImageAdmin  =  this.environment.getProperty("sqli.cantine.default.persons.admin.imagename");
       var defaultImg = new ImageEntity();
         defaultImg.setImagename(defaultImageAdmin);
         this.savedAdmin.setImage(defaultImg);

            this.adminDao.save(this.savedAdmin);

        this.formData.set("firstname", "Halim-Updated");
        this.formData.set("lastname", "Yahiaoui-Updated");
        this.formData.set("birthdateAsString", "2000-07-18");
        this.formData.set("town", "chicago");
        this.formData.set("address", "North Bergen New Jersey USA");
        this.formData.set("phone", "0631800190");



        var  idMealToUpdate =  this.adminDao.findAll().get(0).getId();
        this.formData.set("id" , String.valueOf(idMealToUpdate) );

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, ADMIN_UPDATE_INFO )
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(MockMvcResultMatchers.content().string(ADMIN_INFO_UPDATED_SUCCESSFULLY));


        var adminUpdated = this.adminDao.findById(idMealToUpdate).get();
        Assertions.assertEquals(this.formData.get("firstname").get(0), adminUpdated.getFirstname());
        Assertions.assertEquals(this.formData.get("lastname").get(0), adminUpdated.getLastname());
        Assertions.assertEquals(this.formData.get("town").get(0), adminUpdated.getTown());
        Assertions.assertEquals(this.formData.get("address").get(0), adminUpdated.getAddress());
        Assertions.assertEquals(this.formData.get("phone").get(0), adminUpdated.getPhone());


        Assertions.assertTrue(
                new File(ADMIN_IMAGE_PATH + adminUpdated.getImage().getImagename()).delete()
        );

    }

    @Test
    void updateAdminInfoWithOutImage() throws Exception {
        this.formData.set("firstname", "Halim-Updated");
        this.formData.set("lastname", "Yahiaoui-Updated");
        this.formData.set("birthdateAsString", "2000-07-18");
        this.formData.set("town", "chicago");
        this.formData.set("address", "North Bergen New Jersey USA");
        this.formData.set("phone", "0631800190");

        var  idMealToUpdate =  this.adminDao.findAll().get(0).getId();
        this.formData.set("id" , String.valueOf(idMealToUpdate) );

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, ADMIN_UPDATE_INFO )
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(MockMvcResultMatchers.content().string(ADMIN_INFO_UPDATED_SUCCESSFULLY));

        var adminUpdated = this.adminDao.findById(idMealToUpdate).get();


        Assertions.assertEquals(this.formData.get("firstname").get(0), adminUpdated.getFirstname());
        Assertions.assertEquals(this.formData.get("lastname").get(0), adminUpdated.getLastname());
        Assertions.assertEquals(this.formData.get("town").get(0), adminUpdated.getTown());
        Assertions.assertEquals(this.formData.get("address").get(0), adminUpdated.getAddress());
        Assertions.assertEquals(this.formData.get("phone").get(0), adminUpdated.getPhone());
        Assertions.assertEquals(IMAGE_NAME, adminUpdated.getImage().getImagename());


    }


    /***************************************** TESTS   FUNCTION   ************************************************/
    @Test
    void  updateAdminInfoWithInvalidFunction() throws Exception {
        this.formData.set("function",  "wrongFunction");
        var idMealToUpdate =   1 ;
        this.formData.set("id" , String.valueOf(idMealToUpdate) );

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("FunctionNotFound"))));


    }
    @Test
    void  updateAdminInfoWithWrongFunction() throws Exception {
        this.formData.set("function",  "  ab ");
        var idMealToUpdate =   1 ;
        this.formData.set("id" , String.valueOf(idMealToUpdate) );

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO )
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));



        result.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("FunctionNotFound"))));


    }

    @Test
    void  updateAdminInfoWithEmptyFunction() throws Exception {
        this.formData.set("function",  "  ");
        var idMealToUpdate =   1 ;
        this.formData.set("id" , String.valueOf(idMealToUpdate) );
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));



        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("FunctionRequire"))));


    }
    @Test
    void  updateAdminInfoWithNullFunction() throws Exception {
        this.formData.set("function",  null);
        var idMealToUpdate =   1 ;
        this.formData.set("id" , String.valueOf(idMealToUpdate) );

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));



        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("FunctionRequire"))));


    }
    @Test
    void  updateAdminInfoWithOutFunction() throws Exception {
        this.formData.remove("function");
        var idMealToUpdate =   1 ;
        this.formData.set("id" , String.valueOf(idMealToUpdate) );
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("FunctionRequire"))));


    }



    /*********************************** TESTS  IAMGE  **********************************************************/


    @Test
    void  updateAdminInfoWithWrongImageFormat() throws Exception {
        var  idMealToUpdate =  this.adminDao.findAll().get(0).getId();
        this.imageData = new MockMultipartFile(
                "image",                         // nom du champ de fichier
                IMAGE_NAME,          // nom du fichier
                "images/pdf",                    // type MIME
                new FileInputStream(IMAGE_FOR_TEST_PATH));
        this.formData.set("id" , String.valueOf(idMealToUpdate) );
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidImageFormat"))));


    }

    @Test
    void  updateAdminInfoWithInvalidImageFormat() throws Exception {
        var  idMealToUpdate =  this.adminDao.findAll().get(0).getId();
        this.imageData = new MockMultipartFile(
                "image",                         // nom du champ de fichier
                IMAGE_NAME,          // nom du fichier
                "images/svg",                    // type MIME
                new FileInputStream(IMAGE_FOR_TEST_PATH));
        this.formData.set("id" , String.valueOf(idMealToUpdate) );
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidImageFormat"))));


    }


    /***************************************** TESTS   PHONES   ************************************************/


    @Test
    void  updateAdminInfoWithInvalidPhoneFormat3() throws Exception {
        this.formData.set("phone", " +33076289514 ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO )
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidPhoneFormat"))));
    }

    @Test
    void  updateAdminInfoWithInvalidPhoneFormat2() throws Exception {
        this.formData.set("phone",  " 06319907853654 ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO )
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidPhoneFormat"))));


    }

    @Test
    void  updateAdminInfoWithInvalidPhoneFormat() throws Exception {
        this.formData.set("phone",  " oksfki ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO )
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidPhoneFormat"))));


    }
    @Test
    void  updateAdminInfoWithEmptyPhone() throws Exception {
        this.formData.set("phone",  "  ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO )
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("PhoneRequire"))));


    }
    @Test
    void updateAdminInfoWithNullPhone() throws Exception {
        this.formData.set("phone",  null);

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO )
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("PhoneRequire"))));


    }
    @Test
    void updateAdminInfoWithOutPhone() throws Exception {
        this.formData.remove("phone");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO )
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("PhoneRequire"))));


    }











    /***************************************** TESTS   ADDRESS   ************************************************/

    @Test
    void  updateAdminInfoWithTooLongAddress() throws Exception {
        this.formData.set("address",  "a".repeat(3001));

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO )
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LongAddress"))));


    }



    @Test
    void updateAdminInfoWithTooShortAddress() throws Exception {
        this.formData.set("address",  "  ab ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO )
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("ShortAddress"))));


    }

    @Test
    void  updateAdminInfoWithEmptyAddress() throws Exception {
        this.formData.set("address",  "  ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO )
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("AddressRequire"))));


    }
    @Test
    void  updateAdminInfoWithNullAddress() throws Exception {
        this.formData.set("address",  null);

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO )
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("AddressRequire"))));


    }
    @Test
    void updateAdminInfoWithOutAddress() throws Exception {
        this.formData.remove("address");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO )
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("AddressRequire"))));


    }









    /***************************************** TESTS   TOWN   ************************************************/

    @Test
    void  updateAdminInfoWithTooLongTown() throws Exception {
        this.formData.set("town",  "a".repeat(1001));

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO )
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LongTown"))));


    }



    @Test
    void  updateAdminInfoWithTooShortTown() throws Exception {
        this.formData.set("town",  "  ab ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO )
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("ShortTown"))));


    }

    @Test
    void  updateAdminInfoWithEmptyTown() throws Exception {
        this.formData.set("town",  "  ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO )
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("TownRequire"))));


    }
    @Test
    void  updateAdminInfoWithNullTown() throws Exception {
        this.formData.set("town",  null);

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO )
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("TownRequire"))));


    }
    @Test
    void  updateAdminInfoWithOutTown() throws Exception {
        this.formData.remove("town");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO )
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("TownRequire"))));


    }









    /***************************************** TESTS   BirthdateAsString  ************************************************/

    @Test
    void  updateAdminInfoWithEmptyInvalidBirthdateAsStringFormat4() throws Exception {
        this.formData.set("birthdateAsString",  "2000/07/18");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO )
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidBirthdateFormat"))));


    }
    @Test
    void  updateAdminInfoWithEmptyInvalidBirthdateAsStringFormat3() throws Exception {
        this.formData.set("birthdateAsString",  "18/07/2000");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO )
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidBirthdateFormat"))));


    }

    @Test
    void  updateAdminInfoWithEmptyInvalidBirthdateAsStringFormat2() throws Exception {
        this.formData.set("birthdateAsString",  "18-07-2000");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO )
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidBirthdateFormat"))));


    }



    @Test
    void  updateAdminInfoWithEmptyInvalidBirthdateAsStringFormat() throws Exception {
        this.formData.set("birthdateAsString",  "kzjrnozr,kfjfkrfkrf");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO )
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidBirthdateFormat"))));


    }


    @Test
    void  updateAdminInfoWithEmptyBirthdateAsString() throws Exception {
        this.formData.set("birthdateAsString",  "  ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO )
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("BirthdateRequire"))));


    }
    @Test
    void  updateAdminInfoWithNullBirthdateAsString() throws Exception {
        this.formData.set("birthdateAsString",  null);

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO )
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("BirthdateRequire"))));


    }
    @Test
    void  updateAdminInfoWithOutBirthdayAsString() throws Exception {
        this.formData.remove("birthdateAsString");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO )
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("BirthdateRequire"))));


    }






    /***************************************** TESTS   LASTNAME  ************************************************/

    @Test
    void  updateAdminInfoWithTooLongLastname() throws Exception {
        this.formData.set("lastname",  "a".repeat(91));

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO )
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LongLastName"))));


    }



    @Test
    void  updateAdminInfoWithTooShortLastname() throws Exception {
        this.formData.set("lastname",  "  ab ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO )
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("ShortLastName"))));


    }

    @Test
    void  updateAdminInfoWithEmptyLastname() throws Exception {
        this.formData.set("lastname",  "  ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO )
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LastNameRequire"))));


    }
    @Test
    void  updateAdminInfoWithNullLastname() throws Exception {
        this.formData.set("lastname",  null);

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO )
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LastNameRequire"))));


    }
    @Test
    void  updateAdminInfoWithOutLastname() throws Exception {
        this.formData.remove("lastname");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO )
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LastNameRequire"))));


    }






    /***************************************** TESTS   FIRSTNAME  ************************************************/

    @Test
    void  updateAdminInfoWithTooLongFirstname() throws Exception {
        this.formData.set("firstname",  "a".repeat(91));


        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO )
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LongFirstName"))));


    }



    @Test
    void  updateAdminInfoWithTooShortFirstname() throws Exception {
        this.formData.set("firstname",  "  ab ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO )
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("ShortFirstName"))));


    }

    @Test
    void  updateAdminInfoWithEmptyFirstname() throws Exception {
        this.formData.set("firstname",  "  ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO )
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("FirstNameRequire"))));


    }
    @Test
    void  updateAdminInfoWithNullFirstname() throws Exception {
        this.formData.set("firstname",  null);

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO )
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("FirstNameRequire"))));


    }
    @Test
    void  updateAdminInfoWithOutFirstname() throws Exception {
        this.formData.remove("firstname");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO )
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("FirstNameRequire"))));


    }







    /*****************************  TESTS FOR  ID ADMIN  ********************************/

    @Test
    void updateAdminInfoWithAdminNotFound () throws Exception {
        var idMeal = this.adminDao.findAll().get(0).getId() + 1000;
        this.formData.set("id" , String.valueOf(idMeal) );
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("AdminNotFound"))));
    }
    @Test
    void updateAdminInfoWithDoubleIdAdmin () throws Exception {
        this.formData.set("id" , "1.5" );
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidParam"))));
    }
    @Test
    void updateAdminInfoWithNegativeIdAdmin () throws Exception {
        this.formData.set("id" , "-5" );
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO )
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidId"))));
    }
    @Test
    void updateAdminInfoWithInvalidIdAdmin () throws Exception {
        this.formData.set("id" , "jjedh5" );
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidParam"))));
    }
    @Test
    void updateAdminInfoWithNullIdAdmin () throws Exception {
        this.formData.set("id" , null );
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidId"))));
    }
    @Test
    void updateAdminInfoWithEmptyIdAdmin () throws Exception {
        this.formData.set("id" , "");
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO + paramReq)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidId"))));
    }
    @Test
    void updateAdminInfoWithOutIdAdmin () throws Exception {
        this.formData.remove("id");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidId"))));
    }
}
