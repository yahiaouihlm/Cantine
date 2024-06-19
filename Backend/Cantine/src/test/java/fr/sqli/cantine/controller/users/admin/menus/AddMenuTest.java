/*
package fr.sqli.cantine.controller.users.admin.menus;


import fr.sqli.cantine.controller.AbstractContainerConfig;
import fr.sqli.cantine.dao.IMealDao;
import fr.sqli.cantine.dao.IMenuDao;
import fr.sqli.cantine.entity.MenuEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AddMenuTest extends AbstractContainerConfig implements IMenuTest {
    private static final Logger LOG = LogManager.getLogger();
    final String MENU_ADDED_SUCCESSFULLY = "MENU ADDED SUCCESSFULLY";
    @Autowired
    private IMenuDao menuDao;
    @Autowired
    private IMealDao mealDao;
    @Autowired
    private MockMvc mockMvc;

    private MultiValueMap<String, String> formData;

    private MockMultipartFile imageData;

    private Integer mealIDSavedInDB;

    private MenuEntity menuEntitySavedInDB;

    private MenuEntity menuSaved;

    void initFormData() throws IOException {
        this.mealIDSavedInDB = null; //  init  mealIDSavedInDB
        this.formData = IMenuTest.initFormData();
        this.imageData = new MockMultipartFile("image",                         // nom du champ de fichier
                IMAGE_MENU_FOR_TEST_NAME,          // nom du fichier
                IMAGE_MENU_FORMAT_FOR_TEST,                    // type MIME
                new FileInputStream(IMAGE_MENU_FOR_TEST_PATH));
    }

    void initDB (){
        var meal = createMeal();

        this.mealIDSavedInDB = this.mealDao.save(meal).getId();;

        var menu = createMenu(List.of(meal));
        this.menuSaved = this.menuDao.save(menu);

    }

    void cleanDB() {
        this.menuDao.deleteAll();
        this.mealDao.deleteAll();
    }
    @BeforeEach
    void init() throws IOException {
              initFormData();
              cleanDB();
              initDB();
    }



    */
/************************************** Add Menu ****************************************//*


    @Test
    void addMenuTest() throws Exception {
        // to  find the image  we have to  remove all  menus  saved in DB and  save  a  new  menu and  get  its  image  to  check  if  its saved seccessfully


        this.formData = new LinkedMultiValueMap<>();
        this.formData.add("label", "MenuTest");
        this.formData.add("description", "Menu  description  test");
        this.formData.add("price", "3.87");
        this.formData.add("status", "1");
        this.formData.add("quantity", "10");
        this.formData.set("mealIDs", String.valueOf(this.mealIDSavedInDB));

        var result = this.mockMvc.perform(multipart(ADD_MENU_URL)
                .file(this.imageData).params(this.formData).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(MENU_ADDED_SUCCESSFULLY));

        MenuEntity menuEntity = this.menuDao.findAll().stream() // we know that  there is  only 2 menu
                                   .filter(menu ->menu.getLabel().equals("MenuTest")).limit(1).toList().get(0);
         var imageName = menuEntity.getImage().getImagename();

        Assertions.assertTrue(new File(DIRECTORY_IMAGE_MENU + imageName).exists());
        Assertions.assertTrue(new File(DIRECTORY_IMAGE_MENU + imageName).delete());
    }


    */
/************************************** Existing Menu ***********************************//*

    @Test
    void addMenuWithExistingMenu3() throws Exception {
        //  get menu  saved in DB to  use it  in  the  test

        this.formData.set("label", this.menuSaved.getLabel().toLowerCase());
        this.formData.set("price", this.menuSaved.getPrice().toString() + "0000");
        this.formData.set("description", this.menuSaved.getDescription().toUpperCase());
        this.formData.set("mealIDs", String.valueOf(this.mealIDSavedInDB));

        var result = this.mockMvc.perform(multipart(ADD_MENU_URL).file(this.imageData).params(this.formData).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isConflict()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("ExistingMenu"))));

    }

    @Test
    void addMenuWithExistingMenu2() throws Exception {


        this.formData.set("label", "T  A     c    o      S  ");
        this.formData.set("price", this.menuSaved.getPrice().toString() + "0000");
        this.formData.set("description", "T A C O  s  deS criP   tio      NMenu");
        this.formData.set("mealIDs", String.valueOf(this.mealIDSavedInDB));

        var result = this.mockMvc.perform(multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isConflict()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("ExistingMenu"))));

    }


    @Test
    void addMenuWithExistingMenu() throws Exception {


        this.formData.set("label", this.menuSaved.getLabel());
        this.formData.set("price", this.menuSaved.getPrice().toString());
        this.formData.set("description", this.menuSaved.getDescription());
        this.formData.set("mealIDs", String.valueOf(this.mealIDSavedInDB));

        var result = this.mockMvc.perform(multipart(ADD_MENU_URL).file(this.imageData).params(this.formData).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isConflict()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("ExistingMenu"))));

    }

    @Test
   void addMenuWithUnavailableMeal() throws Exception {
        var  meal   =  this.mealDao.findAll().get(0);
        meal.setStatus(0);
       var  mealID  =  this.mealDao.save(meal).getId();

            this.formData.set("mealIDs", String.valueOf(mealID));

       var result = this.mockMvc.perform(multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    */
/*************************************** MealIDs *************************************//*


    @Test
    void addMenuWithInvalidMealIDs3() throws Exception {
        this.formData.remove("mealIDs", "{1, 2}");

        this.imageData = new MockMultipartFile("image",                         // nom du champ de fichier
                IMAGE_MENU_FOR_TEST_NAME,          // nom du fichier
                "image/svg",                    // type MIME
                new FileInputStream(IMAGE_MENU_FOR_TEST_PATH));

        var result = this.mockMvc.perform(multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("MenuWithOutMeals"))));
    }

    @Test
    void addMenuWithInvalidMealIDs2() throws Exception {
        this.formData.remove("mealIDs", "[1, 2]");

        this.imageData = new MockMultipartFile("image",                         // nom du champ de fichier
                IMAGE_MENU_FOR_TEST_NAME,          // nom du fichier
                "image/svg",                    // type MIME
                new FileInputStream(IMAGE_MENU_FOR_TEST_PATH));

        var result = this.mockMvc.perform(multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("MenuWithOutMeals"))));
    }

    @Test
    void addMenuWithInvalidMealIDs() throws Exception {
        this.formData.remove("mealIDs", "jhnzserbj");

        this.imageData = new MockMultipartFile("image",                         // nom du champ de fichier
                IMAGE_MENU_FOR_TEST_NAME,          // nom du fichier
                "image/svg",                    // type MIME
                new FileInputStream(IMAGE_MENU_FOR_TEST_PATH));

        var result = this.mockMvc.perform(multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("MenuWithOutMeals"))));
    }

    @Test
    void addMenuWithEmptyMealIDs() throws Exception {
        this.formData.remove("mealIDs", "");

        this.imageData = new MockMultipartFile("image",                         // nom du champ de fichier
                IMAGE_MENU_FOR_TEST_NAME,          // nom du fichier
                "image/svg",                    // type MIME
                new FileInputStream(IMAGE_MENU_FOR_TEST_PATH));

        var result = this.mockMvc.perform(multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("MenuWithOutMeals"))));
    }

    @Test
    void addMenuWithNullMealIDs() throws Exception {
        this.formData.remove("mealIDs", null);

        this.imageData = new MockMultipartFile("image",                         // nom du champ de fichier
                IMAGE_MENU_FOR_TEST_NAME,          // nom du fichier
                "image/svg",                    // type MIME
                new FileInputStream(IMAGE_MENU_FOR_TEST_PATH));

        var result = this.mockMvc.perform(multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("MenuWithOutMeals"))));
    }

    @Test
    void addMenuWithOutMealIDs() throws Exception {
        this.formData.remove("mealIDs");

        this.imageData = new MockMultipartFile("image",                         // nom du champ de fichier
                IMAGE_MENU_FOR_TEST_NAME,          // nom du fichier
                "image/svg",                    // type MIME
                new FileInputStream(IMAGE_MENU_FOR_TEST_PATH));

        var result = this.mockMvc.perform(multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("MenuWithOutMeals"))));
    }

    @Test
    void updateMenuWithInvalidMealsIDs() throws Exception {

        this.formData.set("mealIDs", List.of(new String("1")).toString());

        var result = this.mockMvc.perform(multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isNotFound()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("NoMealFound"))));
    }

    */
/************************************* Image *******************************************//*


    @Test
    void addMenuWithInvalidImageFormat() throws Exception {

        this.formData.set("mealIDs", String.valueOf(this.mealIDSavedInDB));

        this.imageData = new MockMultipartFile("image",                         // nom du champ de fichier
                IMAGE_MENU_FOR_TEST_NAME,          // nom du fichier
                "image/svg",                    // type MIME
                new FileInputStream(IMAGE_MENU_FOR_TEST_PATH));

        var result = this.mockMvc.perform(multipart(ADD_MENU_URL).file(this.imageData).params(this.formData).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidImageFormat"))));
    }


    @Test
    void addMenuWithInvalidImageName() throws Exception {

        this.imageData = new MockMultipartFile("wrongImageName",                         // nom du champ de fichier
                IMAGE_MENU_FOR_TEST_NAME,          // nom du fichier
                "image/png",                    // type MIME
                new FileInputStream(IMAGE_MENU_FOR_TEST_PATH));

        var result = this.mockMvc.perform(multipart(ADD_MENU_URL).file(this.imageData).params(this.formData).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Image"))));
    }


    @Test
    void addMenuTestWithOutImage() throws Exception {

        var result = this.mockMvc.perform(multipart(ADD_MENU_URL).params(this.formData).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Image"))));
    }


    */
/*********************************** Price *******************************************//*

    @Test
    void addMenuTestWithTooLongPrice() throws Exception {
        this.formData.set("price", "1000.1");
        var result = this.mockMvc.perform(multipart(ADD_MENU_URL).file(this.imageData).params(this.formData).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("HighPrice"))));
    }

    @Test
    void addMenuTestWithNegativePrice() throws Exception {
        this.formData.set("price", "-1.5");
        var result = this.mockMvc.perform(multipart(ADD_MENU_URL).file(this.imageData).params(this.formData).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("NegativePrice"))));
    }

    @Test
    void addMenuTestWithInvalidPrice4() throws Exception {
        this.formData.set("price", ".5-");

        var result = this.mockMvc.perform(multipart(ADD_MENU_URL).file(this.imageData).params(this.formData).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidArgument"))));
    }

    @Test
    void addMenuTestWithInvalidPrice3() throws Exception {
        this.formData.set("price", "-1c");
        var result = this.mockMvc.perform(multipart(ADD_MENU_URL).file(this.imageData).params(this.formData).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidArgument"))));
    }

    @Test
    void addMenuTestWithInvalidPrice2() throws Exception {
        this.formData.set("price", "1.d");
        var result = this.mockMvc.perform(multipart(ADD_MENU_URL).file(this.imageData).params(this.formData).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidArgument"))));
    }

    @Test
    void addMenuTestWithInvalidPrice() throws Exception {
        this.formData.set("price", "0edez");
        var result = this.mockMvc.perform(multipart(ADD_MENU_URL).file(this.imageData).params(this.formData).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidArgument"))));
    }

    @Test
    void addMenuTestWitEmptyPrice() throws Exception {
        this.formData.set("price", "");
        var result = this.mockMvc.perform(multipart(ADD_MENU_URL).file(this.imageData).params(this.formData).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Price"))));
    }

    @Test
    void addMenuTestWithNullPrice() throws Exception {
        this.formData.set("price", null);
        var result = this.mockMvc.perform(multipart(ADD_MENU_URL).file(this.imageData).params(this.formData).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Price"))));
    }

    @Test
    void addMenuTestWithOutPrice() throws Exception {
        this.formData.remove("price");

        var result = this.mockMvc.perform(multipart(ADD_MENU_URL).file(this.imageData).params(this.formData).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Price"))));
    }


    */
/*********************************** Quantity *******************************************//*


    @Test
    void addMenuTestWithQuantityOutBoundOfInteger() throws Exception {
        this.formData.set("quantity", "2000000000000000000000000000000000000000000000000000000000");

        var result = this.mockMvc.perform(multipart(ADD_MENU_URL).file(this.imageData).params(this.formData).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidArgument"))));
    }

    @Test
    void addMenuTestWithTooLongQuantity() throws Exception {
        this.formData.set("quantity", Integer.toString(Integer.MAX_VALUE - 99));
        var result = this.mockMvc.perform(multipart(ADD_MENU_URL).file(this.imageData).params(this.formData).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("HighQuantity"))));
    }

    @Test
    void addMenuTestWithNegativeQuantity() throws Exception {
        this.formData.set("quantity", "-1");
        var result = this.mockMvc.perform(multipart(ADD_MENU_URL).file(this.imageData).params(this.formData).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("NegativeQuantity"))));
    }

    @Test
    void addMenuTestWithInvalidQuantity3() throws Exception {
        this.formData.set("quantity", "-1.2");
        var result = this.mockMvc.perform(multipart(ADD_MENU_URL).file(this.imageData).params(this.formData).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidArgument"))));
    }

    @Test
    void addMenuTestWithInvalidQuantity2() throws Exception {
        this.formData.set("quantity", "1.2");
        var result = this.mockMvc.perform(multipart(ADD_MENU_URL).file(this.imageData).params(this.formData).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidArgument"))));
    }

    @Test
    void addMenuTestWithInvalidQuantity() throws Exception {
        this.formData.set("quantity", "null");
        var result = this.mockMvc.perform(multipart(ADD_MENU_URL).file(this.imageData).params(this.formData).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidArgument"))));
    }

    @Test
    void addMenuTestWithEmptyQuantity() throws Exception {
        this.formData.set("quantity", "");
        var result = this.mockMvc.perform(multipart(ADD_MENU_URL).file(this.imageData).params(this.formData).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Quantity"))));
    }

    @Test
    void addMenuTestWithNullQuantity() throws Exception {
        this.formData.set("quantity", null);
        var result = this.mockMvc.perform(multipart(ADD_MENU_URL).file(this.imageData).params(this.formData).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Quantity"))));
    }

    @Test
    void addMenuTestWithOutQuantity() throws Exception {
        this.formData.remove("quantity");

        var result = this.mockMvc.perform(multipart(ADD_MENU_URL).file(this.imageData).params(this.formData).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Quantity"))));
    }


    */
/*********************************** Status *********************************************//*

    @Test
    void AddMenuTestWithOutSideStatusValue3() throws Exception {
        this.formData.set("status", "3 ");


        // when : call addMeal
        var result = this.mockMvc.perform(multipart(ADD_MENU_URL).file(this.imageData).params(this.formData).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("OutSideStatusValue"))));
    }

    @Test
    void AddMenuTestWithOutSideStatusValue2() throws Exception {
        this.formData.set("status", "-5");


        // when : call addMeal
        var result = this.mockMvc.perform(multipart(ADD_MENU_URL).file(this.imageData).params(this.formData).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("OutSideStatusValue"))));
    }

    @Test
    void AddMenuTestWithOutSideStatusValue() throws Exception {
        this.formData.set("status", "5");


        // when : call addMeal
        var result = this.mockMvc.perform(multipart(ADD_MENU_URL).file(this.imageData).params(this.formData).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("OutSideStatusValue"))));
    }

    @Test
    void AddMealTestWithEmptyStatusValue() throws Exception {
        this.formData.set("status", " ");

        // when : call addMeal
        var result = this.mockMvc.perform(multipart(ADD_MENU_URL).file(this.imageData).params(this.formData).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Status"))));
    }

    @Test
    void AddMenuTestWithInvalidStatusValue2() throws Exception {
        this.formData.set("status", "-5rffr");

        // when : call addMeal
        var result = this.mockMvc.perform(multipart(ADD_MENU_URL).file(this.imageData).params(this.formData).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidArgument"))));
    }

    @Test
    void AddMenuTestWithInvalidStatusValue() throws Exception {
        this.formData.set("status", "564rffr");

        // when : call addMeal
        var result = this.mockMvc.perform(multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidArgument"))));
    }

    @Test
    void AddMenuTestWithNegativeStatusValue3() throws Exception {
        this.formData.set("status", "-1");

        // when : call addMeal
        var result = this.mockMvc.perform(multipart(ADD_MENU_URL).file(this.imageData).params(this.formData)

                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("OutSideStatusValue"))));
    }

    @Test
    void AddMenuTestWithNullStatusValue() throws Exception {
        this.formData.set("status", null);

        // when : call addMeal
        var result = this.mockMvc.perform(multipart(ADD_MENU_URL).file(this.imageData).params(this.formData).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Status"))));
    }

    @Test
    void AddMenuWithoutStatus() throws Exception {
        this.formData.remove("status");

        // when : call addMeal
        var result = this.mockMvc.perform(multipart(ADD_MENU_URL).file(this.imageData).params(this.formData).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Status"))));
    }


    */
/*********************************** Description ****************************************//*

    @Test
    void AddMealTestWithEmptyDescription() throws Exception {

        this.formData.set("description", "    "); // length  must be  < 3 without spaces

        var result = this.mockMvc.perform(multipart(ADD_MENU_URL).file(this.imageData).params(this.formData).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Description"))));


    }

    @Test
    void testAddMenuWithTooShortDescription() throws Exception {
        this.formData.set("description", "aau");

        var result = this.mockMvc.perform(multipart(HttpMethod.POST, ADD_MENU_URL).file(this.imageData).params(this.formData));

        result.andExpect(status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("ShortDescriptionLength"))));
    }

    @Test
    void testAddMenuWithTooLongDescription() throws Exception {
        this.formData.set("description", "a".repeat(1701));

        var result = this.mockMvc.perform(multipart(HttpMethod.POST, ADD_MENU_URL).file(this.imageData).params(this.formData));

        result.andExpect(status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LongDescriptionLength"))));
    }

    @Test
    void testAddMenuWithNullDescription() throws Exception {
        this.formData.set("description", null);

        var result = this.mockMvc.perform(multipart(HttpMethod.POST, ADD_MENU_URL).file(this.imageData).params(this.formData));

        result.andExpect(status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Description"))));
    }

    @Test
    void testAddMenuWithOutDescription() throws Exception {
        this.formData.remove("description");


        var result = this.mockMvc.perform(multipart(HttpMethod.POST, ADD_MENU_URL).file(this.imageData).params(this.formData));


        result.andExpect(status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Description"))));
    }


    */
/*********************************** label ********************************************//*

    @Test
    void AddMenuTestWithEmptyLabel() throws Exception {

        this.formData.set("label", "    "); // length  must be  < 3 without spaces

        var result = this.mockMvc.perform(multipart(ADD_MENU_URL).file(this.imageData).params(this.formData).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Label"))));


    }

    @Test
    void testAddMenuWithTooShortLabel() throws Exception {
        this.formData.set("label", "aa");

        var result = this.mockMvc.perform(multipart(HttpMethod.POST, ADD_MENU_URL).file(this.imageData).params(this.formData));

        result.andExpect(status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("ShortLabelLength"))));
    }

    @Test
    void testAddMenuWithTooLongLabel() throws Exception {
        this.formData.set("label", "a".repeat(101));

        var result = this.mockMvc.perform(multipart(HttpMethod.POST, ADD_MENU_URL).file(this.imageData).params(this.formData));

        result.andExpect(status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LongLabelLength"))));
    }

    @Test
    void testAddMenuWithNullLabel() throws Exception {
        this.formData.set("label", null);

        var result = this.mockMvc.perform(multipart(HttpMethod.POST, ADD_MENU_URL).file(this.imageData).params(this.formData));

        result.andExpect(status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Label"))));
    }

    @Test
    void testAddMenuWithOutLabel() throws Exception {
        this.formData.remove("label");


        var result = this.mockMvc.perform(multipart(HttpMethod.POST, ADD_MENU_URL).file(this.imageData).params(this.formData));


        result.andExpect(status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Label"))));
    }


}//  end of class AddMenuTest

*/
