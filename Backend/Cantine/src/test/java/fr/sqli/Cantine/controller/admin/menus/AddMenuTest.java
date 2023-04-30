package fr.sqli.Cantine.controller.admin.menus;


import fr.sqli.Cantine.controller.admin.AbstractContainerConfig;
import fr.sqli.Cantine.dao.IMealDao;
import fr.sqli.Cantine.dao.IMenuDao;
import fr.sqli.Cantine.entity.ImageEntity;
import fr.sqli.Cantine.entity.MealEntity;
import fr.sqli.Cantine.entity.MenuEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
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
import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AddMenuTest extends AbstractContainerConfig implements IMenuTest {
    @Autowired
    private IMenuDao menuDao;
    @Autowired
    private IMealDao mealDao;
    @Autowired
    private MockMvc mockMvc;

    private MultiValueMap<String, String> formData;

    private MockMultipartFile imageData;

    private int mealIDSavedInDB;


    @BeforeEach
    void initFormData() throws IOException {
        this.formData = IMenuTest.initFormData();
        this.imageData = new MockMultipartFile(
                "image",                         // nom du champ de fichier
                IMAGE_MENU_FOR_TEST_NAME,          // nom du fichier
                IMAGE_MENU_FORMAT_FOR_TEST,                    // type MIME
                new FileInputStream(IMAGE_MENU_FOR_TEST_PATH));
    }

    @AfterEach
    void cleanDB() {
        this.menuDao.deleteAll();
        this.mealDao.deleteAll();
    }

    MenuEntity initDB() throws FileNotFoundException {
        //  save  a  meal
        var meal = IMenuTest.createMeal();
        this.mealDao.save(meal);

        this.mealIDSavedInDB = meal.getId();

        var menu = IMenuTest.createMenu(List.of(meal));
        return this.menuDao.save(menu);

    }

    /************************************** Existing Menu ***********************************/
    @Test
    void addMenuWithExistingMenu3() throws Exception {
        var menu = initDB(); //  get menu  saved in DB to  use it  in  the  test

        this.formData.set("label", menu.getLabel().toLowerCase() );
        this.formData.set("price", menu.getPrice().toString()+"0000");
        this.formData.set("description", menu.getDescription().toUpperCase());
        this.formData.set("mealIDs", String.valueOf(this.mealIDSavedInDB));

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("ExistingMenu"))));

    }

    @Test
    void addMenuWithExistingMenu2() throws Exception {
        var menu = initDB(); //  get menu  saved in DB to  use it  in  the  test

        this.formData.set("label", "T  A     c    o      S  " );
        this.formData.set("price", menu.getPrice().toString()+"0000");
        this.formData.set("description", "T A C O  s  deS criP   tio      NMenu");
        this.formData.set("mealIDs", String.valueOf(this.mealIDSavedInDB));

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("ExistingMenu"))));

    }


    @Test
    void addMenuWithExistingMenu() throws Exception {
        var menu = initDB(); //  get menu  saved in DB to  use it  in  the  test

        this.formData.set("label", menu.getLabel());
        this.formData.set("price", menu.getPrice().toString());
        this.formData.set("description", menu.getDescription());
        this.formData.set("mealIDs", String.valueOf(this.mealIDSavedInDB));

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("ExistingMenu"))));

    }

    /************************************* Image *******************************************/

    @Test
    void addMenuWithInvalidImageFormat() throws Exception {
        // init  DataBase
        initDB();

        this.formData.set("mealIDs", String.valueOf(this.mealIDSavedInDB));

        this.imageData = new MockMultipartFile(
                "image",                         // nom du champ de fichier
                IMAGE_MENU_FOR_TEST_NAME,          // nom du fichier
                "image/svg",                    // type MIME
                new FileInputStream(IMAGE_MENU_FOR_TEST_PATH));

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidImageFormat"))));
    }


    @Test
    void addMenuWithInvalidImageName() throws Exception {

        this.imageData = new MockMultipartFile(
                "wrongImageName",                         // nom du champ de fichier
                IMAGE_MENU_FOR_TEST_NAME,          // nom du fichier
                "image/png",                    // type MIME
                new FileInputStream(IMAGE_MENU_FOR_TEST_PATH));

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Image"))));
    }


    @Test
    void addMenuTestWithOutImage() throws Exception {

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MENU_URL)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Image"))));
    }


    /*********************************** Price *******************************************/
    @Test
    void addMenuTestWithTooLongPrice() throws Exception {
        this.formData.set("price", "1000.1");
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("HighPrice"))));
    }

    @Test
    void addMenuTestWithNegativePrice() throws Exception {
        this.formData.set("price", "-1.5");
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("NegativePrice"))));
    }

    @Test
    void addMenuTestWithInvalidPrice4() throws Exception {
        this.formData.set("price", ".5-");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidArgument"))));
    }

    @Test
    void addMenuTestWithInvalidPrice3() throws Exception {
        this.formData.set("price", "-1c");
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidArgument"))));
    }

    @Test
    void addMenuTestWithInvalidPrice2() throws Exception {
        this.formData.set("price", "1.d");
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidArgument"))));
    }

    @Test
    void addMenuTestWithInvalidPrice() throws Exception {
        this.formData.set("price", "0edez");
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidArgument"))));
    }

    @Test
    void addMenuTestWitEmptyPrice() throws Exception {
        this.formData.set("price", "");
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Price"))));
    }

    @Test
    void addMenuTestWithNullPrice() throws Exception {
        this.formData.set("price", null);
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Price"))));
    }

    @Test
    void addMenuTestWithOutPrice() throws Exception {
        this.formData.remove("price");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Price"))));
    }


    /*********************************** Quantity *******************************************/

    @Test
    void addMenuTestWithQuantityOutBoundOfInteger() throws Exception {
        this.formData.set("quantity", "2000000000000000000000000000000000000000000000000000000000");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidArgument"))));
    }

    @Test
    void addMenuTestWithTooLongQuantity() throws Exception {
        this.formData.set("quantity", Integer.toString(Integer.MAX_VALUE - 99));
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("HighQuantity"))));
    }

    @Test
    void addMenuTestWithNegativeQuantity() throws Exception {
        this.formData.set("quantity", "-1");
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("NegativeQuantity"))));
    }

    @Test
    void addMenuTestWithInvalidQuantity3() throws Exception {
        this.formData.set("quantity", "-1.2");
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidArgument"))));
    }

    @Test
    void addMenuTestWithInvalidQuantity2() throws Exception {
        this.formData.set("quantity", "1.2");
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidArgument"))));
    }

    @Test
    void addMenuTestWithInvalidQuantity() throws Exception {
        this.formData.set("quantity", "null");
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidArgument"))));
    }

    @Test
    void addMenuTestWithEmptyQuantity() throws Exception {
        this.formData.set("quantity", "");
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Quantity"))));
    }

    @Test
    void addMenuTestWithNullQuantity() throws Exception {
        this.formData.set("quantity", null);
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Quantity"))));
    }

    @Test
    void addMenuTestWithOutQuantity() throws Exception {
        this.formData.remove("quantity");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Quantity"))));
    }


    /*********************************** Status *********************************************/
    @Test
    void AddMenuTestWithOutSideStatusValue3() throws Exception {
        this.formData.set("status", "3 ");


        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("OutSideStatusValue"))));
    }

    @Test
    void AddMenuTestWithOutSideStatusValue2() throws Exception {
        this.formData.set("status", "-5");


        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("OutSideStatusValue"))));
    }

    @Test
    void AddMenuTestWithOutSideStatusValue() throws Exception {
        this.formData.set("status", "5");


        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("OutSideStatusValue"))));
    }

    @Test
    void AddMealTestWithEmptyStatusValue() throws Exception {
        this.formData.set("status", " ");

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Status"))));
    }

    @Test
    void AddMenuTestWithInvalidStatusValue2() throws Exception {
        this.formData.set("status", "-5rffr");

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidArgument"))));
    }

    @Test
    void AddMenuTestWithInvalidStatusValue() throws Exception {
        this.formData.set("status", "564rffr");

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidArgument"))));
    }

    @Test
    void AddMenuTestWithNegativeStatusValue3() throws Exception {
        this.formData.set("status", "-1");

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)

                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("OutSideStatusValue"))));
    }

    @Test
    void AddMenuTestWithNullStatusValue() throws Exception {
        this.formData.set("status", null);

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Status"))));
    }

    @Test
    void AddMenuWithoutStatus() throws Exception {
        this.formData.remove("status");

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Status"))));
    }


    /*********************************** Description ****************************************/
    @Test
    void AddMealTestWithEmptyDescription() throws Exception {

        this.formData.set("description", "    "); // length  must be  < 3 without spaces

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Description"))));


    }

    @Test
    void testAddMenuWithTooShortDescription() throws Exception {
        this.formData.set("description", "aau");

        var result = this.mockMvc.perform(multipart(HttpMethod.POST, ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
        );

        result.andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("ShortDescriptionLength"))));
    }

    @Test
    void testAddMenuWithTooLongDescription() throws Exception {
        this.formData.set("description", "a".repeat(1701));

        var result = this.mockMvc.perform(multipart(HttpMethod.POST, ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
        );

        result.andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LongDescriptionLength"))));
    }

    @Test
    void testAddMenuWithNullDescription() throws Exception {
        this.formData.set("description", null);

        var result = this.mockMvc.perform(multipart(HttpMethod.POST, ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
        );

        result.andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Description"))));
    }

    @Test
    void testAddMenuWithOutDescription() throws Exception {
        this.formData.remove("description");


        var result = this.mockMvc.perform(multipart(HttpMethod.POST, ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
        );


        result.andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Description"))));
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
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("ShortLabelLength"))));
    }

    @Test
    void testAddMenuWithTooLongLabel() throws Exception {
        this.formData.set("label", "a".repeat(101));

        var result = this.mockMvc.perform(multipart(HttpMethod.POST, ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
        );

        result.andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LongLabelLength"))));
    }

    @Test
    void testAddMenuWithNullLabel() throws Exception {
        this.formData.set("label", null);

        var result = this.mockMvc.perform(multipart(HttpMethod.POST, ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
        );

        result.andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Label"))));
    }

    @Test
    void testAddMenuWithOutLabel() throws Exception {
        this.formData.remove("label");


        var result = this.mockMvc.perform(multipart(HttpMethod.POST, ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
        );


        result.andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Label"))));
    }


}//  end of class AddMenuTest

