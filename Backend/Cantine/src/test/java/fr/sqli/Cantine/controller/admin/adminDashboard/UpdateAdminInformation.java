package fr.sqli.Cantine.controller.admin.adminDashboard;

import fr.sqli.Cantine.controller.admin.AbstractContainerConfig;
import fr.sqli.Cantine.dao.AdminDao;
import fr.sqli.Cantine.dao.IFunctionDao;
import fr.sqli.Cantine.entity.AdminEntity;
import fr.sqli.Cantine.entity.FunctionEntity;
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
public class UpdateAdminInformation  extends AbstractContainerConfig implements  IAdminTest {
    private  final  String  paramReq = "?"+"idAdmin"+"=";
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
    private AdminEntity savedAdmin;

    void initDataBase() {
        FunctionEntity function = new FunctionEntity();
        function.setName("Manager");
        this.savedFunction = this.functionDao.save(function);
        var admin  =  IAdminTest.createAdminWith("halim.yahiaoui@social.aston-ecole.com",  this.savedFunction);
    }
    void  cleanDtaBase() {
        this.adminDao.deleteAll();
        this.functionDao.deleteAll();
    }
    void initFormData() throws IOException {
        this.formData =new LinkedMultiValueMap<>();
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



    /***************************************** TESTS   ADDRESS   ************************************************/

    @Test
    void  updateAdminInfoWithTooLongAddress() throws Exception {
        this.formData.set("address",  "a".repeat(3001));

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO + paramReq + "1")
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LongAddress"))));


    }



    @Test
    void updateAdminInfoWithTooShortAddress() throws Exception {
        this.formData.set("address",  "  ab ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO + paramReq + "1")
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("ShortAddress"))));


    }

    @Test
    void  updateAdminInfoWithEmptyAddress() throws Exception {
        this.formData.set("address",  "  ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO + paramReq + "1")
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("AddressRequire"))));


    }
    @Test
    void  updateAdminInfoWithNullAddress() throws Exception {
        this.formData.set("address",  null);

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO + paramReq + "1")
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("AddressRequire"))));


    }
    @Test
    void updateAdminInfoWithOutAddress() throws Exception {
        this.formData.remove("address");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO + paramReq + "1")
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

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO + paramReq + "1")
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LongTown"))));


    }



    @Test
    void  updateAdminInfoWithTooShortTown() throws Exception {
        this.formData.set("town",  "  ab ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO + paramReq + "1")
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("ShortTown"))));


    }

    @Test
    void  updateAdminInfoWithEmptyTown() throws Exception {
        this.formData.set("town",  "  ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO + paramReq + "1")
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("TownRequire"))));


    }
    @Test
    void  updateAdminInfoWithNullTown() throws Exception {
        this.formData.set("town",  null);

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO + paramReq + "1")
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("TownRequire"))));


    }
    @Test
    void  updateAdminInfoWithOutTown() throws Exception {
        this.formData.remove("town");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO + paramReq + "1")
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

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO + paramReq + "1")
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidBirthdateFormat"))));


    }
    @Test
    void  updateAdminInfoWithEmptyInvalidBirthdateAsStringFormat3() throws Exception {
        this.formData.set("birthdateAsString",  "18/07/2000");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO + paramReq + "1")
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidBirthdateFormat"))));


    }

    @Test
    void  updateAdminInfoWithEmptyInvalidBirthdateAsStringFormat2() throws Exception {
        this.formData.set("birthdateAsString",  "18-07-2000");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO + paramReq + "1")
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidBirthdateFormat"))));


    }



    @Test
    void  updateAdminInfoWithEmptyInvalidBirthdateAsStringFormat() throws Exception {
        this.formData.set("birthdateAsString",  "kzjrnozr,kfjfkrfkrf");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO + paramReq + "1")
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidBirthdateFormat"))));


    }


    @Test
    void  updateAdminInfoWithEmptyBirthdateAsString() throws Exception {
        this.formData.set("birthdateAsString",  "  ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO + paramReq + "1")
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("BirthdateRequire"))));


    }
    @Test
    void  updateAdminInfoWithNullBirthdateAsString() throws Exception {
        this.formData.set("birthdateAsString",  null);

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO + paramReq + "1")
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("BirthdateRequire"))));


    }
    @Test
    void  updateAdminInfoWithOutBirthdayAsString() throws Exception {
        this.formData.remove("birthdateAsString");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO + paramReq + "1")
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

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO + paramReq + "1")
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LongLastName"))));


    }



    @Test
    void  updateAdminInfoWithTooShortLastname() throws Exception {
        this.formData.set("lastname",  "  ab ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO + paramReq + "1")
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("ShortLastName"))));


    }

    @Test
    void  updateAdminInfoWithEmptyLastname() throws Exception {
        this.formData.set("lastname",  "  ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO + paramReq + "1")
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LastNameRequire"))));


    }
    @Test
    void  updateAdminInfoWithNullLastname() throws Exception {
        this.formData.set("lastname",  null);

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO + paramReq + "1")
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LastNameRequire"))));


    }
    @Test
    void  updateAdminInfoWithOutLastname() throws Exception {
        this.formData.remove("lastname");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO + paramReq + "1")
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


        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO + paramReq + "1")
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LongFirstName"))));


    }



    @Test
    void  updateAdminInfoWithTooShortFirstname() throws Exception {
        this.formData.set("firstname",  "  ab ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO + paramReq + "1")
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("ShortFirstName"))));


    }

    @Test
    void  updateAdminInfoWithEmptyFirstname() throws Exception {
        this.formData.set("firstname",  "  ");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO + paramReq + "1")
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("FirstNameRequire"))));


    }
    @Test
    void  updateAdminInfoWithNullFirstname() throws Exception {
        this.formData.set("firstname",  null);

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO + paramReq + "1")
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("FirstNameRequire"))));


    }
    @Test
    void  updateAdminInfoWithOutFirstname() throws Exception {
        this.formData.remove("firstname");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO + paramReq + "1")
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("FirstNameRequire"))));


    }







    /*****************************  TESTS FOR  ID ADMIN  ********************************/

    @Test
    void updateAdminInfoWithDoubleIdAdmin () throws Exception {
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO + paramReq + "1.5")
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidParam"))));
    }
    @Test
    void updateAdminInfoWithNegativeIdAdmin () throws Exception {
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO + paramReq + "-5")
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidId"))));
    }
    @Test
    void updateAdminInfoWithInvalidIdAdmin () throws Exception {
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO + paramReq + "jjedh5")
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidParam"))));
    }
    @Test
    void updateAdminInfoWithNullIdAdmin () throws Exception {
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO + paramReq + null)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidParam"))));
    }
    @Test
    void updateAdminInfoWithEmptyIdAdmin () throws Exception {
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO + paramReq)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("MissingParam"))));
    }
    @Test
    void updateAdminInfoWithOutIdAdmin () throws Exception {
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT,   ADMIN_UPDATE_INFO)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("MissingParam"))));
    }
}
