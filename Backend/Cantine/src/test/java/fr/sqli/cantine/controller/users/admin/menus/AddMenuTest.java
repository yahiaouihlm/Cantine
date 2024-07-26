
package fr.sqli.cantine.controller.users.admin.menus;


import fr.sqli.cantine.controller.AbstractContainerConfig;
import fr.sqli.cantine.controller.AbstractLoginRequest;
import fr.sqli.cantine.dao.*;
import fr.sqli.cantine.entity.*;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AddMenuTest extends AbstractContainerConfig implements IMenuTest {
    private static final Logger LOG = LogManager.getLogger();
    ;
    @Autowired
    private IUserDao iStudentDao;
    @Autowired
    private IStudentClassDao iStudentClassDao;
    private IMenuDao menuDao;

    private IMealDao mealDao;

    private MockMvc mockMvc;

    private IFunctionDao functionDao;

    private LinkedMultiValueMap formData;

    private MockMultipartFile imageData;
    private IOrderDao orderDao;
    private IUserDao adminDao;
    private MenuEntity menuEntitySavedInDB;
    private String authorizationToken;
    private MenuEntity menuSaved;

    @Autowired
    public AddMenuTest( IOrderDao iOrderDao,MockMvc mockMvc, IMenuDao menuDao, IMealDao mealDao, IFunctionDao functionDao, IUserDao adminDao) throws Exception {
        this.mockMvc = mockMvc;
        this.adminDao = adminDao;
        this.menuDao = menuDao;
        this.mealDao = mealDao;
        this.functionDao = functionDao;
        this.orderDao = iOrderDao;
        cleanDB();
        initFormData();
        initDB();
    }


    void initFormData() throws IOException {
        this.formData = IMenuTest.initFormData();
        this.imageData = new MockMultipartFile("image",                         // nom du champ de fichier
                IMAGE_MENU_FOR_TEST_NAME,          // nom du fichier
                IMAGE_MENU_FORMAT_FOR_TEST,                    // type MIME
                new FileInputStream(IMAGE_MENU_FOR_TEST_PATH));
    }

    void initDB() throws Exception {

        AbstractLoginRequest.saveAdmin(this.adminDao, this.functionDao);
        this.authorizationToken = AbstractLoginRequest.getAdminBearerToken(this.mockMvc);
        var meal = this.mealDao.save(IMenuTest.createMeal());

        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setName(IMAGE_MENU_FOR_TEST_NAME);

        MealEntity mealEntity = IMenuTest.createMealWith("MealTest2", "MealTest  description2", "MealTest  category test", new BigDecimal(10.0), 1, 10, imageEntity);
        mealEntity.setMeal_type(MealTypeEnum.ENTREE);

        this.mealDao.save(mealEntity);

        this.menuSaved = this.menuDao.save(IMenuTest.createMenu(List.of(meal, mealEntity)));

    }

    void cleanDB() {
        this.orderDao.deleteAll();
        this.menuDao.deleteAll();
        this.mealDao.deleteAll();
        this.adminDao.deleteAll();
        this.functionDao.deleteAll();
    }


    /************************************** Add Menu ****************************************/


    @Test
    void addMenuTest() throws Exception {
        // to  find the image  we have to  remove all  menus  saved in DB and  save  a  new  menu and  get  its  image  to  check  if  its saved seccessfully


        this.formData = new LinkedMultiValueMap<>();
        this.formData.add("label", "MenuTest");
        this.formData.add("description", "Menu  description  test");
        this.formData.add("price", "3.87");
        this.formData.add("status", "1");
        this.formData.add("quantity", "10");

        var meals = this.mealDao.findAll();
        ObjectMapper objectMapper = new ObjectMapper();
        this.formData.add("listOfMealsAsString", objectMapper.writeValueAsString(meals.stream().map(AbstractEntity::getId).toList()));

        //    this.formData.add("listOfMealsAsString", " [\"" + meals.get(0).getUuid() +","+meals.get(1).getUuid() + "\" ] ");


        var result = this.mockMvc.perform(multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(super.responseMessage(IMenuTest.responseMap.get("MenuAddedSuccessfully"))));


        MenuEntity menuEntity = this.menuDao.findAll().stream() // we know that  there is  only 2 menu
                .filter(menu -> menu.getLabel().equals("MenuTest")).limit(1).toList().get(0);
        var imageName = menuEntity.getImage().getName();

        Assertions.assertTrue(new File(DIRECTORY_IMAGE_MENU + imageName).exists());
        Assertions.assertTrue(new File(DIRECTORY_IMAGE_MENU + imageName).delete());
    }


    /************************************** Existing Menu ***********************************/

    @Test
    void addMenuWithExistingMenu3() throws Exception {
        //  get menu  saved in DB to  use it  in  the  test

        this.formData.set("label", this.menuSaved.getLabel().toLowerCase());
        this.formData.set("price", this.menuSaved.getPrice().toString() + "0000");
        this.formData.set("description", this.menuSaved.getDescription().toUpperCase());
        this.formData.add("listOfMealsAsString", " [\"" + java.util.UUID.randomUUID() + "\" ] ");

        var result = this.mockMvc.perform(multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isConflict()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("ExistingMenu"))));

    }

    @Test
    void addMenuWithExistingMenu2() throws Exception {


        this.formData.set("label", "T  A     c    o      S  ");
        this.formData.set("price", this.menuSaved.getPrice().toString() + "0000");
        this.formData.set("description", "T A C O  s  deS criP   tio      NMenu");
        this.formData.add("listOfMealsAsString", " [\"" + java.util.UUID.randomUUID() + "\" ] ");

        var result = this.mockMvc.perform(multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isConflict()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("ExistingMenu"))));

    }

    @Test
    void addMenuTestWithStudentToken() throws Exception {
        this.orderDao.deleteAll();
        this.iStudentDao.deleteAll();
        this.iStudentClassDao.deleteAll();

        AbstractLoginRequest.saveAStudent(this.iStudentDao, this.iStudentClassDao);
        var studentAuthorizationToken = AbstractLoginRequest.getStudentBearerToken(this.mockMvc);


        var result = this.mockMvc.perform(multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION,studentAuthorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isForbidden());

    }


    @Test
    void addMenuWithExistingMenu() throws Exception {


        this.formData.set("label", this.menuSaved.getLabel());
        this.formData.set("price", this.menuSaved.getPrice().toString());
        this.formData.set("description", this.menuSaved.getDescription());
        this.formData.add("listOfMealsAsString", " [\"" + java.util.UUID.randomUUID() + "\" ] ");

        var result = this.mockMvc.perform(multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isConflict()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("ExistingMenu"))));

    }

    @Test
    void addMenuWithUnavailableMeal() throws Exception {
        var meals = this.mealDao.findAll();
        meals.get(0).setStatus(0);
        var mealID = this.mealDao.save(meals.get(0)).getId();
        // this.formData.add("listOfMealsAsString", " [\"" +mealID +  "," +meals.get(1).getUuid()+   "\" ] ");
        ObjectMapper objectMapper = new ObjectMapper();

        this.formData.add("listOfMealsAsString", objectMapper.writeValueAsString(List.of(mealID, meals.get(1).getId())));


        var result = this.mockMvc.perform(multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isForbidden());
    }


    /*************************************** MealIDs *************************************/

    @Test
    void addMenuWithFewMealTest() throws Exception {
        // to  find the image  we have to  remove all  menus  saved in DB and  save  a  new  menu and  get  its  image  to  check  if  its saved seccessfully


        this.formData = new LinkedMultiValueMap<>();
        this.formData.add("label", "MenuTest");
        this.formData.add("description", "Menu  description  test");
        this.formData.add("price", "3.87");
        this.formData.add("status", "1");
        this.formData.add("quantity", "10");

        this.formData.add("listOfMealsAsString", " [\"" + this.mealDao.findAll().get(0).getId() + "\" ] ");


        var result = this.mockMvc.perform(multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("fewMealInTheMenu"))));
    }


    @Test
    void addMenuWithInvalidMealIDsnOTFound() throws Exception {
        List<String> mealsUuid = this.mealDao.findAll().stream().map(AbstractEntity::getId).toList();
        this.formData.add("listOfMealsAsString", " [\"" + java.util.UUID.randomUUID() + "," + mealsUuid.get(0) + "\" ] ");


        var result = this.mockMvc.perform(multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("MealNotFoundOnMenu"))));
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
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("NoMealInTheMenu"))));
    }

    @Test
    void addMenuWithEmptyMealIDs() throws Exception {
        this.formData.remove("listOfMealsAsString", "");

        this.imageData = new MockMultipartFile("image",                         // nom du champ de fichier
                IMAGE_MENU_FOR_TEST_NAME,          // nom du fichier
                "image/svg",                    // type MIME
                new FileInputStream(IMAGE_MENU_FOR_TEST_PATH));

        var result = this.mockMvc.perform(multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("NoMealInTheMenu"))));
    }

    @Test
    void addMenuWithNullMealIDs() throws Exception {
        this.formData.remove("listOfMealsAsString", null);

        this.imageData = new MockMultipartFile("image",                         // nom du champ de fichier
                IMAGE_MENU_FOR_TEST_NAME,          // nom du fichier
                "image/svg",                    // type MIME
                new FileInputStream(IMAGE_MENU_FOR_TEST_PATH));

        var result = this.mockMvc.perform(multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("NoMealInTheMenu"))));
    }

    @Test
    void addMenuWithOutMealIDs() throws Exception {

        this.imageData = new MockMultipartFile("image",                         // nom du champ de fichier
                IMAGE_MENU_FOR_TEST_NAME,          // nom du fichier
                "image/svg",                    // type MIME
                new FileInputStream(IMAGE_MENU_FOR_TEST_PATH));

        var result = this.mockMvc.perform(multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("NoMealInTheMenu"))));
    }

    @Test
    void updateMenuWithInvalidMealsIDs() throws Exception {

        this.formData.set("listOfMealsAsString", List.of(new String("1")).toString());

        var result = this.mockMvc.perform(multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidMealID"))));
    }

    /************************************* Image *******************************************/


    @Test
    void addMenuWithInvalidImageName() throws Exception {

        List<String> mealsUuid = this.mealDao.findAll().stream().map(AbstractEntity::getId).toList();
        this.formData.add("listOfMealsAsString", mealsUuid.get(0) + "," + mealsUuid.get(1));

        this.imageData = new MockMultipartFile("wrongImageName",                         // nom du champ de fichier
                IMAGE_MENU_FOR_TEST_NAME,          // nom du fichier
                "image/png",                    // type MIME
                new FileInputStream(IMAGE_MENU_FOR_TEST_PATH));

        var result = this.mockMvc.perform(multipart(ADD_MENU_URL).file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Image"))));
    }


    @Test
    void addMenuTestWithOutImage() throws Exception {

        var result = this.mockMvc.perform(multipart(ADD_MENU_URL)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Image"))));
    }


    /*********************************** Price *******************************************/

    @Test
    void addMenuTestWithTooLongPrice() throws Exception {
        this.formData.set("price", "1000.1");
        var result = this.mockMvc.perform(multipart(ADD_MENU_URL)
                .file(this.imageData).params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("HighPrice"))));
    }

    @Test
    void addMenuTestWithNegativePrice() throws Exception {
        this.formData.set("price", "-1.5");
        var result = this.mockMvc.perform(multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("NegativePrice"))));
    }

    @Test
    void addMenuTestWithInvalidPrice4() throws Exception {
        this.formData.set("price", ".5-");

        var result = this.mockMvc.perform(multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidValue"))));
    }

    @Test
    void addMenuTestWithInvalidPrice3() throws Exception {
        this.formData.set("price", "-1c");
        var result = this.mockMvc.perform(multipart(ADD_MENU_URL)
                .file(this.imageData).params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidValue"))));
    }

    @Test
    void addMenuTestWithInvalidPrice2() throws Exception {
        this.formData.set("price", "1.d");
        var result = this.mockMvc.perform(multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidValue"))));
    }

    @Test
    void addMenuTestWithInvalidPrice() throws Exception {
        this.formData.set("price", "0edez");
        var result = this.mockMvc.perform(multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidValue"))));
    }

    @Test
    void addMenuTestWitEmptyPrice() throws Exception {
        this.formData.set("price", "");
        var result = this.mockMvc.perform(multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Price"))));
    }

    @Test
    void addMenuTestWithNullPrice() throws Exception {
        this.formData.set("price", null);
        var result = this.mockMvc.perform(multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Price"))));
    }

    @Test
    void addMenuTestWithOutPrice() throws Exception {
        this.formData.remove("price");

        var result = this.mockMvc.perform(multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Price"))));
    }


    /*********************************** Quantity *******************************************/

    @Test
    void addMenuTestWithQuantityOutBoundOfInteger() throws Exception {
        this.formData.set("quantity", "2000000000000000000000000000000000000000000000000000000000");

        var result = this.mockMvc.perform(multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidValue"))));
    }

    @Test
    void addMenuTestWithTooLongQuantity() throws Exception {
        this.formData.set("quantity", Integer.toString(Integer.MAX_VALUE - 99));
        var result = this.mockMvc.perform(multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("HighQuantity"))));
    }

    @Test
    void addMenuTestWithNegativeQuantity() throws Exception {
        this.formData.set("quantity", "-1");
        var result = this.mockMvc.perform(multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("NegativeQuantity"))));
    }

    @Test
    void addMenuTestWithInvalidQuantity3() throws Exception {
        this.formData.set("quantity", "-1.2");
        var result = this.mockMvc.perform(multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidValue"))));
    }

    @Test
    void addMenuTestWithInvalidQuantity2() throws Exception {
        this.formData.set("quantity", "1.2");
        var result = this.mockMvc.perform(multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidValue"))));
    }

    @Test
    void addMenuTestWithInvalidQuantity() throws Exception {
        this.formData.set("quantity", "null");
        var result = this.mockMvc.perform(multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidValue"))));
    }

    @Test
    void addMenuTestWithEmptyQuantity() throws Exception {
        this.formData.set("quantity", "");
        var result = this.mockMvc.perform(multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Quantity"))));
    }

    @Test
    void addMenuTestWithNullQuantity() throws Exception {
        this.formData.set("quantity", null);
        var result = this.mockMvc.perform(multipart(ADD_MENU_URL).file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Quantity"))));
    }

    @Test
    void addMenuTestWithOutQuantity() throws Exception {
        this.formData.remove("quantity");

        var result = this.mockMvc.perform(multipart(ADD_MENU_URL)
                .file(this.imageData).params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Quantity"))));
    }


    /*********************************** Status *********************************************/

    @Test
    void AddMenuTestWithOutSideStatusValue3() throws Exception {
        this.formData.set("status", "3 ");


        // when : call addMeal
        var result = this.mockMvc.perform(multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("OutSideStatusValue"))));
    }

    @Test
    void AddMenuTestWithOutSideStatusValue2() throws Exception {
        this.formData.set("status", "-5");


        // when : call addMeal
        var result = this.mockMvc.perform(multipart(ADD_MENU_URL)
                .file(this.imageData).params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("OutSideStatusValue"))));
    }

    @Test
    void AddMenuTestWithOutSideStatusValue() throws Exception {
        this.formData.set("status", "5");


        // when : call addMeal
        var result = this.mockMvc.perform(multipart(ADD_MENU_URL).file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("OutSideStatusValue"))));
    }

    @Test
    void AddMealTestWithEmptyStatusValue() throws Exception {
        this.formData.set("status", " ");

        // when : call addMeal
        var result = this.mockMvc.perform(multipart(ADD_MENU_URL)
                .file(this.imageData).params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Status"))));
    }

    @Test
    void AddMenuTestWithInvalidStatusValue2() throws Exception {
        this.formData.set("status", "-5rffr");

        // when : call addMeal
        var result = this.mockMvc.perform(multipart(ADD_MENU_URL)
                .file(this.imageData).params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidValue"))));
    }

    @Test
    void AddMenuTestWithInvalidStatusValue() throws Exception {
        this.formData.set("status", "564rffr");

        // when : call addMeal
        var result = this.mockMvc.perform(multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidValue"))));
    }

    @Test
    void AddMenuTestWithNegativeStatusValue3() throws Exception {
        this.formData.set("status", "-1");

        // when : call addMeal
        var result = this.mockMvc.perform(multipart(ADD_MENU_URL).file(this.imageData).params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("OutSideStatusValue"))));
    }

    @Test
    void AddMenuTestWithNullStatusValue() throws Exception {
        this.formData.set("status", null);

        // when : call addMeal
        var result = this.mockMvc.perform(multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Status"))));
    }

    @Test
    void AddMenuWithoutStatus() throws Exception {
        this.formData.remove("status");

        // when : call addMeal
        var result = this.mockMvc.perform(multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Status"))));
    }


    /*********************************** Description ****************************************/

    @Test
    void AddMealTestWithEmptyDescription() throws Exception {

        this.formData.set("description", "    "); // length  must be  < 3 without spaces

        var result = this.mockMvc.perform(multipart(ADD_MENU_URL)
                .file(this.imageData).params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Description"))));


    }

    @Test
    void testAddMenuWithTooShortDescription() throws Exception {
        this.formData.set("description", "aau");

        var result = this.mockMvc.perform(multipart(HttpMethod.POST, ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken));

        result.andExpect(status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("ShortDescriptionLength"))));
    }

    @Test
    void testAddMenuWithTooLongDescription() throws Exception {
        this.formData.set("description", "a".repeat(3001));

        var result = this.mockMvc.perform(multipart(HttpMethod.POST, ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken));

        result.andExpect(status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LongDescriptionLength"))));
    }

    @Test
    void testAddMenuWithNullDescription() throws Exception {
        this.formData.set("description", null);

        var result = this.mockMvc.perform(multipart(HttpMethod.POST, ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken));

        result.andExpect(status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Description"))));
    }

    @Test
    void testAddMenuWithOutDescription() throws Exception {
        this.formData.remove("description");


        var result = this.mockMvc.perform(multipart(HttpMethod.POST, ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken));


        result.andExpect(status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Description"))));
    }


    /*********************************** label ********************************************/

    @Test
    void AddMenuTestWithEmptyLabel() throws Exception {

        this.formData.set("label", "    "); // length  must be  < 3 without spaces

        var result = this.mockMvc.perform(multipart(ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Label"))));


    }

    @Test
    void testAddMenuWithTooShortLabel() throws Exception {
        this.formData.set("label", "aa");

        var result = this.mockMvc.perform(multipart(HttpMethod.POST, ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken));

        result.andExpect(status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("ShortLabelLength"))));
    }

    @Test
    void testAddMenuWithTooLongLabel() throws Exception {
        this.formData.set("label", "a".repeat(101));

        var result = this.mockMvc.perform(multipart(HttpMethod.POST, ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken));

        result.andExpect(status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LongLabelLength"))));
    }

    @Test
    void testAddMenuWithNullLabel() throws Exception {
        this.formData.set("label", null);

        var result = this.mockMvc.perform(multipart(HttpMethod.POST, ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken)
        );

        result.andExpect(status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Label"))));
    }

    @Test
    void testAddMenuWithOutLabel() throws Exception {
        this.formData.remove("label");


        var result = this.mockMvc.perform(multipart(HttpMethod.POST, ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData)
                .header(HttpHeaders.AUTHORIZATION, this.authorizationToken));


        result.andExpect(status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Label"))));
    }


    @Test
    void testAddMenuWithOutAuthToken() throws Exception {
        var result = this.mockMvc.perform(multipart(HttpMethod.POST, ADD_MENU_URL)
                .file(this.imageData)
                .params(this.formData));

        result.andExpect(status().isUnauthorized());
    }

}//  end of class AddMenuTest


