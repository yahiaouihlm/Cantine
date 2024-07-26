package fr.sqli.cantine.controller.users.admin.menus;

import fr.sqli.cantine.controller.AbstractContainerConfig;
import fr.sqli.cantine.controller.AbstractLoginRequest;
import fr.sqli.cantine.dao.*;
import fr.sqli.cantine.entity.ImageEntity;
import fr.sqli.cantine.entity.MealEntity;
import fr.sqli.cantine.entity.MenuEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UpdateMenuTest extends AbstractContainerConfig implements IMenuTest {
    private static final Logger LOG = LogManager.getLogger();
    private IOrderDao orderDao;
    private IUserDao adminDao;
    private IFunctionDao functionDao;
    private IMenuDao menuDao;
    private IMealDao mealDao;
    private MockMultipartFile imageData;
    private MenuEntity menuSaved;
    private MealEntity mealSaved;
    private MockMvc mockMvc;
    private String authorizationToken;
    private MultiValueMap<String, String> formData;


    @Autowired
    public  UpdateMenuTest(IMenuDao menuDao, IMealDao mealDao, MockMvc mockMvc, IOrderDao orderDao, IUserDao adminDao, IFunctionDao functionDao) throws Exception {
        this.menuDao = menuDao;
        this.mealDao = mealDao;
        this.mockMvc = mockMvc;
        this.orderDao = orderDao;
        this.adminDao = adminDao;
        this.functionDao = functionDao;
        cleanDaBase();
        initForData();
        initDaBase ();
    }



    void cleanDaBase () {
        this.orderDao.deleteAll();
        this.menuDao.deleteAll();
        this.mealDao.deleteAll();
        this.adminDao.deleteAll();
        this.functionDao.deleteAll();
    }


    void initForData() throws IOException {
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
    void initDaBase () throws Exception {

        AbstractLoginRequest.saveAdmin(this.adminDao, this.functionDao);
        this.authorizationToken = AbstractLoginRequest.getAdminBearerToken(this.mockMvc);

        this.mealSaved =  IMenuTest.createMeal();
        mealDao.save( this.mealSaved);
        var menu = IMenuTest.createMenu(List.of( this.mealSaved));
        this.menuSaved =   menuDao.save(menu);
        this.formData.add("id", this.menuSaved.getId());
    }


    @Test
    void updateMenuTest() throws Exception {

        this.formData.set("label", "MenuTestUpdated");
        this.formData.set("description", "Menu  description  test  updated");
        this.formData.set("price", "3.807"); //  please make  attention  that is value  will be  rounded  the     3.810

        MealEntity meal = IMenuTest.createMeal();
        meal.setLabel("New  Meal");
        meal.setDescription("New  Meal  description");
        meal = mealDao.save(meal);
        List<String> mealsIds = Arrays.asList(this.menuSaved.getMeals().get(0).getId(), meal.getId());

        this.formData.add("listOfMealsAsString", new ObjectMapper().writeValueAsString(mealsIds));

        var result = this.mockMvc.perform(multipart(HttpMethod.POST, UPDATE_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(super.responseMessage(IMenuTest.responseMap.get("MenuUpdatedSuccessfully"))));

        var oldImageName = this.menuSaved.getImage().getName();
        var menusaved = this.menuDao.findById(this.menuSaved.getId()).get();
        Assertions.assertEquals("MenuTestUpdated", menusaved.getLabel());
        Assertions.assertEquals("Menu  description  test  updated", menusaved.getDescription());
        Assertions.assertEquals(new BigDecimal("3.81"), menusaved.getPrice());

        var imageName = menusaved.getImage().getName();

        var image = new File(DIRECTORY_IMAGE_MENU + imageName);
        Assertions.assertTrue(image.exists());
        Assertions.assertTrue(image.delete());
        var oldImage = new File(DIRECTORY_IMAGE_MENU + oldImageName);
        Assertions.assertFalse(oldImage.exists()); //  the old image was deleted successfully
    }



/*************************************** Update Menu  With  Valid  Data  without Image   ***************************************/


@Test
void updateMenuWithOutImage() throws Exception {

    MealEntity meal = IMenuTest.createMeal();
    meal.setLabel("New  Meal");
    meal.setDescription("New  Meal  description");
    meal = mealDao.save(meal);
    List<String> mealsIds = Arrays.asList(this.menuSaved.getMeals().get(0).getId(), meal.getId());

    this.formData.add("listOfMealsAsString", new ObjectMapper().writeValueAsString(mealsIds));


    var result = this.mockMvc.perform(multipart(HttpMethod.POST, UPDATE_MENU_URL)
            .params(this.formData)
            .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

    result.andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().json(super.responseMessage(IMenuTest.responseMap.get("MenuUpdatedSuccessfully"))));

}




/*************************************** Update Menu  With  Invalid  Image  ***************************************/


@Test
void updateMenuWithInvalidImageFormat2() throws Exception {
    MealEntity meal = IMenuTest.createMeal();
    meal.setLabel("New  Meal");
    meal.setDescription("New  Meal  description");
    meal = mealDao.save(meal);
    List<String> mealsIds = Arrays.asList(this.menuSaved.getMeals().get(0).getId(), meal.getId());

    this.formData.add("listOfMealsAsString", new ObjectMapper().writeValueAsString(mealsIds));


    this.imageData = new MockMultipartFile("image",                         // nom du champ de fichier
            IMAGE_MENU_FOR_TEST_NAME,          // nom du fichier
            "image/svg",                    // type MIME
            new FileInputStream(IMAGE_MENU_FOR_TEST_PATH));


    var result = this.mockMvc.perform(multipart(HttpMethod.POST, UPDATE_MENU_URL)
            .file(this.imageData)
            .params(this.formData)
            .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

    result.andExpect(MockMvcResultMatchers.status().isNotAcceptable()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidImageFormat"))));

}

    @Test
    void updateMenuWithInvalidImageFormat() throws Exception {
        MealEntity meal = IMenuTest.createMeal();
        meal.setLabel("New  Meal");
        meal.setDescription("New  Meal  description");
        meal = mealDao.save(meal);

        List<String> mealsIds = Arrays.asList(this.menuSaved.getMeals().get(0).getId(), meal.getId());

        this.formData.add("listOfMealsAsString", new ObjectMapper().writeValueAsString(mealsIds));


        this.imageData = new MockMultipartFile("image",                         // nom du champ de fichier
                IMAGE_MENU_FOR_TEST_NAME,          // nom du fichier
                "image/gif",                    // type MIME
                new FileInputStream(IMAGE_MENU_FOR_TEST_PATH));


        LOG.error("imageData.getContentType() = " + imageData.getSize());

        var result = this.mockMvc.perform(multipart(HttpMethod.POST, UPDATE_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable()).
                andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidImageFormat"))));
    }



/*************************************  Update Menu  With Existing  Menu ************************************/

    @Test
    void updateMenuTestExistingMenuTest3() throws Exception {
        // create new Menu in  DataBase
        MenuEntity menu =   IMenuTest.createMenu(List.of( this.mealSaved));
        menu.setLabel("Menu2");
        menu.setDescription("Menu  description  test 2");
        menu.setPrice(new BigDecimal("5"));
        this.menuDao.save(menu);

        // update  Menu with  try  to  change  label  to  existing  label and  description  to  existing  description and  price  to  existing  price in other menu
        this.formData.set("label", "M e n u 2".toUpperCase());
        this.formData.set("description", "M  E n u  d  escri pti on  T e S t 2");
        this.formData.set("price", "5.000");

        this.formData.add("listOfMealsAsString", " [\"" + this.menuSaved.getMeals().get(0).getId()+ "\" ] ");

        var result = this.mockMvc.perform(multipart(HttpMethod.POST, UPDATE_MENU_URL)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("ExistingMenu"))));
    }

    @Test
    void updateMenuTestExistingMenuTest2() throws Exception {
        // create new Menu in  DataBase
        MenuEntity menu =   IMenuTest.createMenu(List.of( this.mealSaved));
        menu.setLabel("Menu2");
        menu.setDescription("Menu  description  test 2");
        menu.setPrice(new BigDecimal("5"));
        this.menuDao.save(menu);

        // update  Menu with  try  to  change  label  to  existing  label and  description  to  existing  description and  price  to  existing  price in other menu
        this.formData.set("label", "M e n u 2");
        this.formData.set("description", "M  e n u  d  escri pti on  t e s t 2");
        this.formData.set("price", "5");
        this.formData.add("listOfMealsAsString", " [\"" + this.menuSaved.getMeals().get(0).getId() + "\" ] ");


        var result = this.mockMvc.perform(multipart(HttpMethod.POST, UPDATE_MENU_URL)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("ExistingMenu"))));
    }

    @Test
    void updateMenuTestExistingMenuTest() throws Exception {
        // create new Menu in  DataBase
        MenuEntity menu =   IMenuTest.createMenu(List.of( this.mealSaved));
        menu.setLabel("Menu2");
        menu.setDescription("Menu  description  test 2");
        menu.setPrice(new BigDecimal("5"));
        this.menuDao.save(menu);

        // update  Menu with  try  to  change  label  to  existing  label and  description  to  existing  description and  price  to  existing  price in other menu
        this.formData.set("label", "Menu2");
        this.formData.set("description", "Menu  description  test 2");
        this.formData.set("price", "5");
        this.formData.add("listOfMealsAsString", " [\"" + this.menuSaved.getMeals().get(0).getId() + "\" ] ");



        var result = this.mockMvc.perform(multipart(HttpMethod.POST, UPDATE_MENU_URL)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("ExistingMenu"))));
    }


/************************************* Update  Menu  With  Invalid Meals ID  *************************************/


    @Test
    void  addMenuWithInvalidMealNotFound() throws Exception {
        this.formData.add("listOfMealsAsString", " [\"" + java.util.UUID.randomUUID() + "\" ] ");

        var result = this.mockMvc.perform(multipart(HttpMethod.POST, UPDATE_MENU_URL)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("MealNotFoundOnMenu"))));
    }
    @Test
    void  addMenuWithInvalidMealIDs () throws Exception {
        this.formData.remove("mealIDs" , "jhnzserbj" );

        var result = this.mockMvc.perform(multipart(HttpMethod.POST, UPDATE_MENU_URL)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("MenuWithOutMeals"))));
    }
    @Test
    void  addMenuWithEmptyMealIDs () throws Exception {
        this.formData.remove("mealIDs" , "" );


        var result = this.mockMvc.perform(multipart(HttpMethod.POST, UPDATE_MENU_URL)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("MenuWithOutMeals"))));
    }

    @Test
    void  addMenuWithNullMealIDs () throws Exception {
        this.formData.remove("mealIDs" , null );



        var result = this.mockMvc.perform(multipart(HttpMethod.POST, UPDATE_MENU_URL)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("MenuWithOutMeals"))));
    }
    @Test
    void  addMenuWithOutMealIDs () throws Exception {
        this.formData.remove("mealIDs");


        var result = this.mockMvc.perform(multipart(HttpMethod.POST, UPDATE_MENU_URL)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("MenuWithOutMeals"))));
    }
    @Test
    void  updateMenuWithInvalidMealsIDs () throws Exception {

        this.formData.add("listOfMealsAsString", " [\"" + "deed58de" + "\" ] ");

        var result = this.mockMvc.perform(multipart(HttpMethod.POST, UPDATE_MENU_URL)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidMealID"))));
    }

    @Test
    void  updateMenuWithOutMeals() throws Exception {

        var result = this.mockMvc.perform(multipart(HttpMethod.POST, UPDATE_MENU_URL)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("MenuWithOutMeals"))));
    }


/********************************** Price ******************************************/

    @Test
    void updateMenuTestWithTooLongPriceTest() throws Exception {
        this.formData.set("price", "1000.1");
        var result = this.mockMvc.perform(multipart(HttpMethod.POST, UPDATE_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("HighPrice"))));
    }

    @Test
    void updateMenuTestWithNegativePriceTest() throws Exception {
        this.formData.set("price", "-1.5");
        var result = this.mockMvc.perform(multipart(HttpMethod.POST, UPDATE_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("NegativePrice"))));
    }

    @Test
    void updateMenuTestWithInvalidPrice4Test() throws Exception {
        this.formData.set("price", ".5-");

        var result = this.mockMvc.perform(multipart(HttpMethod.POST, UPDATE_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidValue"))));
    }

    @Test
    void updateMenuTestWithInvalidPrice3Test() throws Exception {
        this.formData.set("price", "-1c");
        var result = this.mockMvc.perform(multipart(HttpMethod.POST, UPDATE_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidValue"))));
    }

    @Test
    void updateMenuTestWithInvalidPrice2Test() throws Exception {
        this.formData.set("price", "1.d");
        var result = this.mockMvc.perform(multipart(HttpMethod.POST, UPDATE_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidValue"))));
    }

    @Test
    void updateMenuTestWithInvalidPriceTest() throws Exception {
        this.formData.set("price", "0edez");
        var result = this.mockMvc.perform(multipart(HttpMethod.POST, UPDATE_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidValue"))));
    }

    @Test
    void updateMenuTestWitEmptyPriceTest() throws Exception {
        this.formData.set("price", "");
        var result = this.mockMvc.perform(multipart(HttpMethod.POST, UPDATE_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Price"))));
    }

    @Test
    void updateMenuTestWithNullPriceTest() throws Exception {
        this.formData.set("price", null);
        var result = this.mockMvc.perform(multipart(HttpMethod.POST, UPDATE_MENU_URL )
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Price"))));
    }

    @Test
    void updateMenuTestWithOutPriceTest() throws Exception {
        this.formData.remove("price");

        var result = this.mockMvc.perform(multipart(HttpMethod.POST, UPDATE_MENU_URL )
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Price"))));
    }


/********************************** Quantity ******************************************/


    @Test
    void updateTestWithQuantityOutBoundOfIntegerTest() throws Exception {
        this.formData.set("quantity", "2000000000000000000000000000000000000000000000000000000000");
        var result = this.mockMvc.perform(multipart(HttpMethod.POST, UPDATE_MENU_URL )
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidValue"))));
    }

    @Test
    void updateMenuTestWithTooLongQuantityTest() throws Exception {
        this.formData.set("quantity", Integer.toString(Integer.MAX_VALUE - 99));
        var result = this.mockMvc.perform(multipart(HttpMethod.POST, UPDATE_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("HighQuantity"))));
    }

    @Test
    void updateMenuTestWithNegativeQuantityTest() throws Exception {
        this.formData.set("quantity", "-1");
        var result = this.mockMvc.perform(multipart(HttpMethod.POST, UPDATE_MENU_URL )
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("NegativeQuantity"))));
    }

    @Test
    void updateMenuTestWithInvalidQuantity3Test() throws Exception {
        this.formData.set("quantity", "-1.2");
        var result = this.mockMvc.perform(multipart(HttpMethod.POST, UPDATE_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidValue"))));
    }

    @Test
    void updateMenuTestWithInvalidQuantity2Test() throws Exception {
        this.formData.set("quantity", "1.2");
        var result = this.mockMvc.perform(multipart(HttpMethod.POST, UPDATE_MENU_URL )
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidValue"))));
    }

    @Test
    void updateMenuTestWithInvalidQuantityTest() throws Exception {
        this.formData.set("quantity", "null");
        var result = this.mockMvc.perform(multipart(HttpMethod.POST, UPDATE_MENU_URL )
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidValue"))));
    }

    @Test
    void updateMenuTestWithEmptyQuantityTest() throws Exception {
        this.formData.set("quantity", "");
        var result = this.mockMvc.perform(multipart(HttpMethod.POST, UPDATE_MENU_URL )
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Quantity"))));
    }

    @Test
    void updateMenuTestWithNullQuantityTest() throws Exception {
        this.formData.set("quantity", null);
        var result = this.mockMvc.perform(multipart(HttpMethod.POST, UPDATE_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Quantity"))));
    }

    @Test
    void updateMenuTestWithOutQuantityTest() throws Exception {
        this.formData.remove("quantity");

        var result = this.mockMvc.perform(multipart(HttpMethod.POST, UPDATE_MENU_URL )
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Quantity"))));
    }






/********************************** Status ********************************************/

    @Test
    void updateMenuWithOutSideStatusValue3Test() throws Exception {
        this.formData.set("status", "3 ");


        // when : call addMeal
        var result = this.mockMvc.perform(multipart(HttpMethod.POST, UPDATE_MENU_URL )
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));



        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("OutSideStatusValue"))));
    }

    @Test
    void updateMenuWithOutSideStatusValue2Test() throws Exception {
        this.formData.set("status", "-5");


        // when : call addMeal
        var result = this.mockMvc.perform(multipart(HttpMethod.POST, UPDATE_MENU_URL )
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));



        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("OutSideStatusValue"))));
    }

    @Test
    void updateMenuWithOutSideStatusValueTest() throws Exception {
        this.formData.set("status", "5");


        // when : call addMeal
        var result = this.mockMvc.perform(multipart(HttpMethod.POST, UPDATE_MENU_URL )
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));



        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("OutSideStatusValue"))));
    }

    @Test
    void updateMealWithEmptyStatusValueTest() throws Exception {
        this.formData.set("status", " ");

        // when : call addMeal
        var result = this.mockMvc.perform(multipart(HttpMethod.POST, UPDATE_MENU_URL )
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));



        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Status"))));
    }

    @Test
    void updateMenuWithInvalidStatusValue2Test() throws Exception {
        this.formData.set("status", "-5rffr");

        // when : call addMeal
        var result = this.mockMvc.perform(multipart(HttpMethod.POST, UPDATE_MENU_URL )
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));



        // then :
        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidValue"))));
    }

    @Test
    void updateMenuWithInvalidStatusValueTest() throws Exception {
        this.formData.set("status", "564rffr");

        // when : call addMeal
        var result = this.mockMvc.perform(multipart(HttpMethod.POST, UPDATE_MENU_URL )
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidValue"))));
    }

    @Test
    void updateMenuWithNegativeStatusValue3Test() throws Exception {
        this.formData.set("status", "-1");

        // when : call addMeal
        var result = this.mockMvc.perform(multipart(HttpMethod.POST, UPDATE_MENU_URL )
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("OutSideStatusValue"))));
    }

    @Test
    void updateMenuWithNullStatusValueTest() throws Exception {
        this.formData.set("status", null);

        // when : call addMeal
        var result = this.mockMvc.perform(multipart(HttpMethod.POST, UPDATE_MENU_URL )
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));



        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Status"))));
    }

    @Test
    void updateMenuWithoutStatusTest() throws Exception {
        this.formData.remove("status");

        // when : call addMeal
        var result = this.mockMvc.perform(multipart(HttpMethod.POST, UPDATE_MENU_URL )
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));



        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Status"))));
    }



/********************************** Description ***************************************/

    @Test
    void updateMenuWithEmptyDescriptionTest() throws Exception {

        this.formData.set("description", "    "); // length  must be  < 3 without spaces

        var result = this.mockMvc.perform(multipart(HttpMethod.POST, UPDATE_MENU_URL )
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Description"))));


    }

    @Test
    void updateMenuWithTooShortDescriptionTest() throws Exception {
        this.formData.set("description", "aau");

        var result = this.mockMvc.perform(multipart(HttpMethod.POST, UPDATE_MENU_URL )
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("ShortDescriptionLength"))));
    }

    @Test
    void updateMenuWithTooLongDescriptionTest() throws Exception {
        this.formData.set("description", "a".repeat(3001));

        var result = this.mockMvc.perform(multipart(HttpMethod.POST, UPDATE_MENU_URL )
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LongDescriptionLength"))));
    }

    @Test
    void updateMenuWithNullDescriptionTest() throws Exception {
        this.formData.set("description", null);

        var result = this.mockMvc.perform(multipart(HttpMethod.POST, UPDATE_MENU_URL )
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Description"))));
    }

    @Test
    void updateMenuWithOutDescriptionTest() throws Exception {
        this.formData.remove("description");

        var result = this.mockMvc.perform(multipart(HttpMethod.POST, UPDATE_MENU_URL )
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Description"))));
    }



/********************************************** LABEL ******************************************************/

    @Test
    void updateMenuWithEmptyLabelTest() throws Exception {

        this.formData.set("label", "    "); // length  must be  < 3 without spaces

        var result = this.mockMvc.perform(multipart(HttpMethod.POST, UPDATE_MENU_URL )
                .file(this.imageData).params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Label"))));


    }

    @Test
    void updateMenuWithTooShortLabelTest() throws Exception {

        this.formData.set("label", "aa");

        var result = this.mockMvc.perform(multipart(HttpMethod.POST, UPDATE_MENU_URL )
                .file(this.imageData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .params(this.formData));

        result.andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("ShortLabelLength"))));
    }

    @Test
    void updateMenuWithTooLongLabelTest() throws Exception {

        this.formData.set("label", "a".repeat(101));

        var result = this.mockMvc.perform(multipart(HttpMethod.POST, UPDATE_MENU_URL)
                .file(this.imageData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .params(this.formData));

        result.andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LongLabelLength"))));
    }

    @Test
    void UpdateMenuWithNullLabelTest() throws Exception {

        this.formData.set("label", null);

        var result = this.mockMvc.perform(multipart(HttpMethod.POST, UPDATE_MENU_URL)
                .file(this.imageData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .params(this.formData));

        result.andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Label"))));
    }

    @Test
    void UpdateMenuWithOutLabelTest() throws Exception {

        this.formData.remove("label");


        var result = this.mockMvc.perform(multipart(HttpMethod.POST, UPDATE_MENU_URL)
                .file(this.imageData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .params(this.formData));


        result.andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Label"))));
    }



    @Test
    void UpdateMenuWithOutToken() throws Exception {

        this.formData.remove("label");


        var result = this.mockMvc.perform(multipart(HttpMethod.POST, UPDATE_MENU_URL)
                .file(this.imageData)
                .params(this.formData));


        result.andExpect(status().isUnauthorized());
    }


}
