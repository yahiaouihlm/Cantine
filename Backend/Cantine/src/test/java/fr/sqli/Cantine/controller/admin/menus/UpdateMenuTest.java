package fr.sqli.Cantine.controller.admin.menus;

import fr.sqli.Cantine.controller.admin.AbstractContainerConfig;
import fr.sqli.Cantine.dao.IMealDao;
import fr.sqli.Cantine.dao.IMenuDao;
import fr.sqli.Cantine.entity.MealEntity;
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
import java.io.IOException;
import java.math.BigDecimal;
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

    private MealEntity mealSaved   ;
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
        this.mealSaved =  IMenuTest.createMeal();
        mealDao.save( this.mealSaved);
        var menu = IMenuTest.createMenu(List.of( this.mealSaved));
        this.menuSaved =   menuDao.save(menu);
    }

    @AfterEach
    void cleanDaBase () {
        mealDao.deleteAll();
        menuDao.deleteAll();
    }
    /**************************************  Update Menu  With Existing  Menu *************************************/
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

        this.formData.set("mealIDs", List.of(this.menuSaved.getMeals().get(0).getId()).toString());

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, UPDATE_MENU_URL + paramReq + this.menuSaved.getId())
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isConflict()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("ExistingMenu"))));
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

        this.formData.set("mealIDs", List.of(this.menuSaved.getMeals().get(0).getId()).toString());

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, UPDATE_MENU_URL + paramReq + this.menuSaved.getId())
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isConflict()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("ExistingMenu"))));
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

        this.formData.set("mealIDs", List.of(this.menuSaved.getMeals().get(0).getId()).toString());

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, UPDATE_MENU_URL + paramReq + this.menuSaved.getId())
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isConflict()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("ExistingMenu"))));
    }


    /************************************** Menu  With  Out  Meals  **************************************/

    @Test
    void  updateMenuWithOutMeals() throws Exception {

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, UPDATE_MENU_URL + paramReq + this.menuSaved.getId())
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("MenuWithOutMeals"))));
    }

    /*********************************** Price *******************************************/
    @Test
    void updateMenuTestWithTooLongPriceTest() throws Exception {
        this.formData.set("price", "1000.1");
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, UPDATE_MENU_URL + paramReq + this.menuSaved.getId())
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("HighPrice"))));
    }

    @Test
    void updateMenuTestWithNegativePriceTest() throws Exception {
        this.formData.set("price", "-1.5");
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, UPDATE_MENU_URL + paramReq + this.menuSaved.getId())
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("NegativePrice"))));
    }

    @Test
    void updateMenuTestWithInvalidPrice4Test() throws Exception {
        this.formData.set("price", ".5-");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, UPDATE_MENU_URL + paramReq + this.menuSaved.getId())
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidArgument"))));
    }

    @Test
    void updateMenuTestWithInvalidPrice3Test() throws Exception {
        this.formData.set("price", "-1c");
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, UPDATE_MENU_URL + paramReq + this.menuSaved.getId())
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidArgument"))));
    }

    @Test
    void updateMenuTestWithInvalidPrice2Test() throws Exception {
        this.formData.set("price", "1.d");
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, UPDATE_MENU_URL + paramReq + this.menuSaved.getId())
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidArgument"))));
    }

    @Test
    void updateMenuTestWithInvalidPriceTest() throws Exception {
        this.formData.set("price", "0edez");
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, UPDATE_MENU_URL + paramReq + this.menuSaved.getId())
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidArgument"))));
    }

    @Test
    void updateMenuTestWitEmptyPriceTest() throws Exception {
        this.formData.set("price", "");
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, UPDATE_MENU_URL + paramReq + this.menuSaved.getId())
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Price"))));
    }

    @Test
    void updateMenuTestWithNullPriceTest() throws Exception {
        this.formData.set("price", null);
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, UPDATE_MENU_URL + paramReq + this.menuSaved.getId())
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Price"))));
    }

    @Test
    void updateMenuTestWithOutPriceTest() throws Exception {
        this.formData.remove("price");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, UPDATE_MENU_URL + paramReq + this.menuSaved.getId())
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Price"))));
    }


    /*********************************** Quantity *******************************************/

    @Test
    void updateTestWithQuantityOutBoundOfIntegerTest() throws Exception {
        this.formData.set("quantity", "2000000000000000000000000000000000000000000000000000000000");
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, UPDATE_MENU_URL + paramReq + this.menuSaved.getId())
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidArgument"))));
    }

    @Test
    void updateMenuTestWithTooLongQuantityTest() throws Exception {
        this.formData.set("quantity", Integer.toString(Integer.MAX_VALUE - 99));
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, UPDATE_MENU_URL + paramReq + this.menuSaved.getId())
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("HighQuantity"))));
    }

    @Test
    void updateMenuTestWithNegativeQuantityTest() throws Exception {
        this.formData.set("quantity", "-1");
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, UPDATE_MENU_URL + paramReq + this.menuSaved.getId())
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("NegativeQuantity"))));
    }

    @Test
    void updateMenuTestWithInvalidQuantity3Test() throws Exception {
        this.formData.set("quantity", "-1.2");
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, UPDATE_MENU_URL + paramReq + this.menuSaved.getId())
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidArgument"))));
    }

    @Test
    void updateMenuTestWithInvalidQuantity2Test() throws Exception {
        this.formData.set("quantity", "1.2");
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, UPDATE_MENU_URL + paramReq + this.menuSaved.getId())
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidArgument"))));
    }

    @Test
    void updateMenuTestWithInvalidQuantityTest() throws Exception {
        this.formData.set("quantity", "null");
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, UPDATE_MENU_URL + paramReq + this.menuSaved.getId())
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidArgument"))));
    }

    @Test
    void updateMenuTestWithEmptyQuantityTest() throws Exception {
        this.formData.set("quantity", "");
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, UPDATE_MENU_URL + paramReq + this.menuSaved.getId())
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Quantity"))));
    }

    @Test
    void updateMenuTestWithNullQuantityTest() throws Exception {
        this.formData.set("quantity", null);
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, UPDATE_MENU_URL + paramReq + this.menuSaved.getId())
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Quantity"))));
    }

    @Test
    void updateMenuTestWithOutQuantityTest() throws Exception {
        this.formData.remove("quantity");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, UPDATE_MENU_URL + paramReq + this.menuSaved.getId())
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Quantity"))));
    }






    /*********************************** Status *********************************************/
    @Test
    void updateMenuWithOutSideStatusValue3Test() throws Exception {
        this.formData.set("status", "3 ");


        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, UPDATE_MENU_URL + paramReq + this.menuSaved.getId())
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));



        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("OutSideStatusValue"))));
    }

    @Test
    void updateMenuWithOutSideStatusValue2Test() throws Exception {
        this.formData.set("status", "-5");


        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, UPDATE_MENU_URL + paramReq + this.menuSaved.getId())
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));



        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("OutSideStatusValue"))));
    }

    @Test
    void updateMenuWithOutSideStatusValueTest() throws Exception {
        this.formData.set("status", "5");


        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, UPDATE_MENU_URL + paramReq + this.menuSaved.getId())
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));



        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("OutSideStatusValue"))));
    }

    @Test
    void updateMealWithEmptyStatusValueTest() throws Exception {
        this.formData.set("status", " ");

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, UPDATE_MENU_URL + paramReq + this.menuSaved.getId())
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));



        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Status"))));
    }

    @Test
    void updateMenuWithInvalidStatusValue2Test() throws Exception {
        this.formData.set("status", "-5rffr");

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, UPDATE_MENU_URL + paramReq + this.menuSaved.getId())
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));



        // then :
        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidArgument"))));
    }

    @Test
    void updateMenuWithInvalidStatusValueTest() throws Exception {
        this.formData.set("status", "564rffr");

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, UPDATE_MENU_URL + paramReq + this.menuSaved.getId())
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));



        // then :
        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidArgument"))));
    }

    @Test
    void updateMenuWithNegativeStatusValue3Test() throws Exception {
        this.formData.set("status", "-1");

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, UPDATE_MENU_URL + paramReq + this.menuSaved.getId())
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("OutSideStatusValue"))));
    }

    @Test
    void updateMenuWithNullStatusValueTest() throws Exception {
        this.formData.set("status", null);

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, UPDATE_MENU_URL + paramReq + this.menuSaved.getId())
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));



        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Status"))));
    }

    @Test
    void updateMenuWithoutStatusTest() throws Exception {
        this.formData.remove("status");

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, UPDATE_MENU_URL + paramReq + this.menuSaved.getId())
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));



        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Status"))));
    }



    /*********************************** Description ****************************************/
    @Test
    void updateMenuWithEmptyDescriptionTest() throws Exception {

        this.formData.set("description", "    "); // length  must be  < 3 without spaces

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, UPDATE_MENU_URL + paramReq + this.menuSaved.getId())
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Description"))));


    }

    @Test
    void updateMenuWithTooShortDescriptionTest() throws Exception {
        this.formData.set("description", "aau");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, UPDATE_MENU_URL + paramReq + this.menuSaved.getId())
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("ShortDescriptionLength"))));
    }

    @Test
    void updateMenuWithTooLongDescriptionTest() throws Exception {
        this.formData.set("description", "a".repeat(1701));

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, UPDATE_MENU_URL + paramReq + this.menuSaved.getId())
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LongDescriptionLength"))));
    }

    @Test
    void updateMenuWithNullDescriptionTest() throws Exception {
        this.formData.set("description", null);

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, UPDATE_MENU_URL + paramReq + this.menuSaved.getId())
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Description"))));
    }

    @Test
    void updateMenuWithOutDescriptionTest() throws Exception {
        this.formData.remove("description");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, UPDATE_MENU_URL + paramReq + this.menuSaved.getId())
                .file(this.imageData)
                .params(this.formData)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(status().isBadRequest()).andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Description"))));
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
