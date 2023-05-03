package fr.sqli.Cantine.controller.admin.menus;

import fr.sqli.Cantine.controller.admin.AbstractContainerConfig;
import fr.sqli.Cantine.dao.IMealDao;
import fr.sqli.Cantine.dao.IMenuDao;
import fr.sqli.Cantine.entity.MenuEntity;
import org.junit.jupiter.api.AfterEach;
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
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UpdateMenuTest extends AbstractContainerConfig implements IMenuTest {
    //
    private  final  String  paramReq = "?"+"idMenu"+"=";
    @Autowired
    private IMenuDao menuDao;
    @Autowired
    private IMealDao mealDao;
    private MockMultipartFile  imageData;

    private MenuEntity menuSaved   ;
   @Autowired
   MockMvc mockMvc;


    private MultiValueMap<String, String> formData;

    @BeforeEach
    void init() throws IOException {
        this.formData =  new LinkedMultiValueMap<>();
        this.formData.add("label", "MenuTest");
        this.formData.add("description", "Menu  description  test");
        this.formData.add("price", "3.87");
        this.formData.add("status", "1");
        this.formData.add("quantity", "10");
        this.imageData = new MockMultipartFile("image",                         // nom du champ de fichier
                IMAGE_MENU_FOR_TEST_NAME,          // nom du fichier
                IMAGE_MENU_FORMAT_FOR_TEST,                    // type MIME
                new FileInputStream(IMAGE_MENU_FOR_TEST_PATH));

    }
    @BeforeEach
    void initDaBase () {
        var meal =  IMenuTest.createMeal();
        mealDao.save(meal);
        var menu = IMenuTest.createMenu(List.of(meal));
        this.menuSaved =   menuDao.save(menu);
    }

    @AfterEach
    void cleanDaBase () {
        mealDao.deleteAll();
        menuDao.deleteAll();
    }

    /*********************************************** LABEL *******************************************************/
    @Test
    void updateMenuWithEmptyLabelTest() throws Exception {

        this.formData.set("label", "    "); // length  must be  < 3 without spaces

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, UPDATE_MENU_URL + paramReq + this.menuSaved.getId())
                .file(this.imageData).params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Label"))));


    }

    @Test
    void updateMenuWithTooShortLabelTest() throws Exception {
        this.formData.set("label", "aa");

        var result = this.mockMvc.perform(multipart(HttpMethod.PUT, UPDATE_MENU_URL + paramReq + this.menuSaved.getId())
                .file(this.imageData)
                .params(this.formData));

        result.andExpect(status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("ShortLabelLength"))));
    }

    @Test
    void updateMenuWithTooLongLabelTest() throws Exception {
        this.formData.set("label", "a".repeat(101));

        var result = this.mockMvc.perform(multipart(HttpMethod.PUT, UPDATE_MENU_URL + paramReq + this.menuSaved.getId())
                .file(this.imageData)
                .params(this.formData));

        result.andExpect(status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LongLabelLength"))));
    }

    @Test
    void UpdateMenuWithNullLabelTest() throws Exception {
        this.formData.set("label", null);

        var result = this.mockMvc.perform(multipart(HttpMethod.PUT, UPDATE_MENU_URL + paramReq + this.menuSaved.getId())
                .file(this.imageData)
                .params(this.formData));

        result.andExpect(status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Label"))));
    }

    @Test
    void UpdateMenuWithOutLabelTest() throws Exception {
        this.formData.remove("label");


        var result = this.mockMvc.perform(multipart(HttpMethod.PUT, UPDATE_MENU_URL + paramReq + this.menuSaved.getId())
                .file(this.imageData)
                .params(this.formData));


        result.andExpect(status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Label"))));
    }



}
