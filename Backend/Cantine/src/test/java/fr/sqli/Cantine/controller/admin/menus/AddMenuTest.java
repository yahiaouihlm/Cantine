package fr.sqli.Cantine.controller.admin.menus;


import fr.sqli.Cantine.controller.admin.AbstractContainerConfig;
import fr.sqli.Cantine.dao.IMenuDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AddMenuTest extends AbstractContainerConfig implements IMenuTest {
    @Autowired
    private IMenuDao menuDao;

    @Autowired
    private MockMvc mockMvc;

    private MultiValueMap<String, String> formData;

    private MockMultipartFile imageData;


    @BeforeEach
    void initFormData() throws IOException {
        this.formData = new LinkedMultiValueMap<>();
        this.formData.add("label", "Tacos");
        this.formData.add("price", "3.87");
        this.formData.add("description", "Menu  description  of Tacos menu");
        this.formData.add("status", "1");
        this.formData.add("quantity", "10");
        this.imageData = new MockMultipartFile(
                "image",                         // nom du champ de fichier
                IMAGE_MENU_FOR_TEST_NAME,          // nom du fichier
                IMAGE_MENU_FORMAT_FOR_TEST,                    // type MIME
                new FileInputStream(IMAGE_MENU_FOR_TEST_PATH));
    }

    /*********************************** label ********************************************/
    @Test
    void AddMealTestWithEmptyLabel() throws Exception {

        this.formData.set("label", "    "); // length  must be  < 3 without spaces

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Label"))));


    }
    @Test
    void testAddMenuWithTooShortLabel() throws Exception {
        this.formData.set("label", "aa");

        var result = this.mockMvc.perform(multipart(HttpMethod.POST, ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
        );

        result.andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json( super.exceptionMessage(exceptionsMap.get("ShortLabelLength"))));
    }
    @Test
   void testAddMenuWithTooLongLabel() throws Exception {
        this.formData.set("label", "a".repeat(101));

        var result = this.mockMvc.perform(multipart(HttpMethod.POST, ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
        );

        result.andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json( super.exceptionMessage(exceptionsMap.get("LongLabelLength"))));
    }
    @Test
    void  testAddMenuWithNullLabel() throws Exception {
        this.formData.set("label", null);

        var result = this.mockMvc.perform(multipart(HttpMethod.POST, ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
        );

        result.andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json( super.exceptionMessage(exceptionsMap.get("Label"))));
    }
    @Test
    void testAddMenuWithOutLabel() throws Exception {
        this.formData.remove("label");


        var result = this.mockMvc.perform(multipart(HttpMethod.POST, ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
        );


        result.andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json( super.exceptionMessage(exceptionsMap.get("Label"))));
    }


}//  end of class AddMenuTest

