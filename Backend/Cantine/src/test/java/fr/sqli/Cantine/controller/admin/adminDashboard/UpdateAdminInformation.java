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
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidRequest"))));
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
