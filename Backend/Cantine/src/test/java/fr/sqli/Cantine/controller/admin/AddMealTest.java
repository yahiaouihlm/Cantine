package fr.sqli.Cantine.controller.admin;

import fr.sqli.Cantine.dao.IMealDao;
import fr.sqli.Cantine.entity.ImageEntity;
import fr.sqli.Cantine.entity.MealEntity;
import fr.sqli.Cantine.service.admin.meals.MealService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
import java.util.Map;

@SpringBootTest
@AutoConfigureMockMvc
public class AddMealTest extends AbstractMealTest {
    private final Map<String, String> exceptionsMap = Map.ofEntries(
            Map.entry("Label", "LABEL_IS_MANDATORY"),
            Map.entry("Category", "CATEGORY_IS_MANDATORY"),
            Map.entry("Description", "DESCRIPTION_IS_MANDATORY"),
            Map.entry("Price", "PRICE_IS_MANDATORY"),
            Map.entry("Quantity", "QUANTITY_IS_MANDATORY"),
            Map.entry("Status", "STATUS_IS_MANDATORY"),
            Map.entry("Image", "IMAGE_IS_MANDATORY"),
            Map.entry("ShortLabelLength", "LABEL_IS_TOO_SHORT"),
            Map.entry("LongLabelLength", "LABEL_IS_TOO_LONG"),
            Map.entry("ShortDescriptionLength", "DESCRIPTION_IS_TOO_SHORT"),
            Map.entry("LongDescriptionLength", "DESCRIPTION_IS_TOO_LONG"),
            Map.entry("ShortCategoryLength", "CATEGORY_IS_TOO_SHORT"),
            Map.entry("LongCategoryLength", "CATEGORY_IS_TOO_LONG"),
            Map.entry("InvalidArgument", "ARGUMENT NOT VALID"),
            Map.entry("OutSideStatusValue", "STATUS MUST BE 0 OR 1 FOR ACTIVE OR INACTIVE"),
            Map.entry("HighPrice", "PRICE MUST BE LESS THAN 1000"),
            Map.entry("HighQuantity", "QUANTITY_IS_TOO_HIGH"),
            Map.entry("NegativePrice", "PRICE MUST BE GREATER THAN 0"),
            Map.entry("NegativeQuantity", "QUANTITY MUST BE GREATER THAN 0"),
            Map.entry("InvalidImageFormat" , "INVALID IMAGE TYPE ONLY PNG , JPG , JPEG OR SVG  ARE ACCEPTED"),
            Map.entry("MealAddedSuccessfully", "MEAL ADDED SUCCESSFULLY")
    );

    @Autowired
    private MealService mealService;

    @Autowired
    private IMealDao mealDao;

    @Autowired
    private MockMvc mockMvc;

    private MultiValueMap<String, String> formData;

    private MockMultipartFile imageData;


    @BeforeEach
    public void initFormData() throws IOException {
        this.formData = new LinkedMultiValueMap<>();
        this.formData.add("label", "MealTest");
        this.formData.add("price", "1.5");
        this.formData.add("category", "MealTest category");
        this.formData.add("description", "MealTest description");
        this.formData.add("status", "1");
        this.formData.add("quantity", "10");
        this.imageData = new MockMultipartFile(
                "image",                         // nom du champ de fichier
                "ImageMealForTest.jpg",          // nom du fichier
                "image/jpg",                    // type MIME
                new FileInputStream("images/meals/ImageMealForTest.jpg"));

    }

    /* TODO ;  check  Existing Meal and  image */

    //  make  one  Meal in  the  database
    // this  meal  will  be  used  only for    the  tests  of  the  method  addMealTestWithExistingMeal because we have to make a meal in DataBase


    public void  initDataBase() {
        ImageEntity image = new ImageEntity();
        image.setImagename("ImageMealForTest.jpg");

        MealEntity mealEntity = new MealEntity("MealTest","MealTest category", "MealTest description"
                , new BigDecimal(1.5), 10,1, image);

        this.mealDao.save(mealEntity);
    }
    //  clear  the  database  after  all
    // this  method  will  be  used  only for  the  tests  of  the  method  addMealTestWithExistingMeal because we have to clear the database after all tests (addMealTestWithExistingMeal)

    public void  clearDataBase() {
        this.mealDao.deleteAll();
    }

    @Test
    void addMealTestWithExistingMealWithAddSpacesToCategory() throws Exception {
        initDataBase(); //  make  one  Meal in  the  database
        this.formData.remove("category");
        this.formData.add("category", "   M e a l Test c  ate gor y ");


        var  errorMessage = "THE MEAL WITH AN LABEL = " + this.formData.getFirst("label")+ " AND A CATEGORY = " + this.formData.getFirst("category").trim()
                + " AND A DESCRIPTION = " + this.formData.getFirst("description") + " IS ALREADY PRESENT IN THE DATABASE ";

        // 3  Test  With  Trying  to  add The Same Meal again
        var result2  =  this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result2.andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(errorMessage)));

        clearDataBase(); //  clear  the  database  after  all

    }
     @Test
     void addMealTestWithExistingMealWithAddSpacesToDescription() throws Exception {
         initDataBase(); //  make  one  Meal in  the  database
         this.formData.remove("description");
         this.formData.add("description", "   MealTest description   ");


         var  errorMessage = "THE MEAL WITH AN LABEL = " + this.formData.getFirst("label")+ " AND A CATEGORY = " + this.formData.getFirst("category")
                 + " AND A DESCRIPTION = " + this.formData.getFirst("description").trim() + " IS ALREADY PRESENT IN THE DATABASE ";

         // 3  Test  With  Trying  to  add The Same Meal again
         var result2  =  this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                 .file(this.imageData)
                 .params(this.formData)
                 .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


         result2.andExpect(MockMvcResultMatchers.status().isConflict())
                 .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(errorMessage)));

         clearDataBase(); //  clear  the  database  after  all

     }


 ////////////////Label

    @Test
    @DisplayName("add Meal with  the  same  label+same spaces    and  the  same  category  and  the  same  description  of  an  existing  meal  in  the  database")
    void addMealTestWithExistingMealWithAddSpacesToLabel3() throws Exception {
        initDataBase(); //  make  one  Meal in  the  database

        this.formData.remove("label");
        this.formData.add("label", "MealTes t");


        var  errorMessage = "THE MEAL WITH AN LABEL = " + this.formData.getFirst("label").replaceAll("\\s+", "")+ " AND A CATEGORY = " + this.formData.getFirst("category")
                + " AND A DESCRIPTION = " + this.formData.getFirst("description") + " IS ALREADY PRESENT IN THE DATABASE ";

        // 3  Test  With  Trying  to  add The Same Meal again
        var result2  =  this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result2.andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(errorMessage)));

        clearDataBase(); //  clear  the  database  after  all
    }



    @Test
    @DisplayName("add Meal with  the  same  label+same spaces    and  the  same  category  and  the  same  description  of  an  existing  meal  in  the  database")
    void addMealTestWithExistingMealWithAddSpacesToLabel2() throws Exception {
        initDataBase(); //  make  one  Meal in  the  database

        this.formData.remove("label");
        this.formData.add("label", " M eal T e s t ");


        var  errorMessage = "THE MEAL WITH AN LABEL = " + this.formData.getFirst("label").replaceAll("\\s+", "")+ " AND A CATEGORY = " + this.formData.getFirst("category")
                + " AND A DESCRIPTION = " + this.formData.getFirst("description") + " IS ALREADY PRESENT IN THE DATABASE ";

        // 3  Test  With  Trying  to  add The Same Meal again
        var result2  =  this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result2.andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(errorMessage)));

        clearDataBase(); //  clear  the  database  after  all
    }





    @Test
    @DisplayName("add Meal with  the  same  label+same spaces    and  the  same  category  and  the  same  description  of  an  existing  meal  in  the  database")
    void addMealTestWithExistingMealWithAddSpacesToLabel() throws Exception {
        initDataBase(); //  make  one  Meal in  the  database

        this.formData.remove("label");
        this.formData.add("label", " M e a  l T e s t ");


        var  errorMessage = "THE MEAL WITH AN LABEL = " + this.formData.getFirst("label").replaceAll("\\s+", "")+ " AND A CATEGORY = " + this.formData.getFirst("category")
                + " AND A DESCRIPTION = " + this.formData.getFirst("description") + " IS ALREADY PRESENT IN THE DATABASE ";

        // 3  Test  With  Trying  to  add The Same Meal again
        var result2  =  this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result2.andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(errorMessage)));

        clearDataBase(); //  clear  the  database  after  all
    }
















    @Test
    @DisplayName("add Meal with  the  same  label  and  the  same  category  and  the  same  description  of  an  existing  meal  in  the  database")
    void addMealTestWithExistingMeal() throws Exception {

        initDataBase(); //  make  one  Meal in  the  database

        var  errorMessage = "THE MEAL WITH AN LABEL = " + this.formData.getFirst("label")+ " AND A CATEGORY = " + this.formData.getFirst("category")
                + " AND A DESCRIPTION = " + this.formData.getFirst("description") + " IS ALREADY PRESENT IN THE DATABASE ";

        // 3  Test  With  Trying  to  add The Same Meal again
        var result2  =  this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result2.andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(errorMessage)));

        clearDataBase(); //  clear  the  database  after  all
    }



































    /**********************************************  Tests  Fot Images  *********************************************/
    @Test
    void addMealWithWrongImageFormat() throws Exception {
        this.imageData = new MockMultipartFile(
                "image",                         // nom du champ de fichier
                "ImageMealForTest.jpg",          // nom du fichier
                "image/gif",                    // type MIME
                new FileInputStream("src/test/java/fr/sqli/Cantine/service/images/filesTests/Babidi_TestGifFormat.gif"));

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidImageFormat"))));

    }
    @Test
    void addMealTestWithWrongImageName () throws Exception {
        this.imageData = new MockMultipartFile(
                "WrongImageName",                         // nom du champ de fichier
                "ImageMealForTest.jpg",          // nom du fichier
                "image/jpg",                    // type MIME
                new FileInputStream("images/meals/ImageMealForTest.jpg"));

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Image"))));

    }
    @Test
    void addMealTestWithOutImage() throws Exception {
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Image"))));


    }


    /*******************************  Tests  For Price  **********************************/

    @Test
    void addMealTestWithTooLongPrice() throws Exception {
        this.formData.remove("price");
        this.formData.add("price", "1000.1");
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("HighPrice"))));
    }

    @Test
    void addMealTestWithNegativePrice() throws Exception {
        this.formData.remove("price");
        this.formData.add("price", "-1.5");
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("NegativePrice"))));
    }

    @Test
    void addMealTestWithInvalidPrice4() throws Exception {
        this.formData.remove("price");
        this.formData.add("price", ".5-");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidArgument"))));
    }

    @Test
    void addMealTestWithInvalidPrice3() throws Exception {
        this.formData.remove("price");
        this.formData.add("price", "-1c");
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidArgument"))));
    }

    @Test
    void addMealTestWithInvalidPrice2() throws Exception {
        this.formData.remove("price");
        this.formData.add("price", "1.d");
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidArgument"))));
    }

    @Test
    void addMealTestWithInvalidPrice() throws Exception {
        this.formData.remove("price");
        this.formData.add("price", "0edez");
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidArgument"))));
    }

    @Test
    void addMealTestWitEmptyPrice() throws Exception {
        this.formData.remove("price");
        this.formData.add("price", "");
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Price"))));
    }

    @Test
    void addMealTestWithNullPrice() throws Exception {
        this.formData.remove("price");
        this.formData.add("price", null);
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Price"))));
    }

    @Test
    void addMealTestWithOutPrice() throws Exception {
        this.formData.remove("price");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Price"))));
    }


    /************************************* Tests for  Quantity  *************************************/
    @Test
    void addMealTestWithTooLongQuantity() throws Exception {
        this.formData.remove("quantity");
        this.formData.add("quantity", Integer.toString(Integer.MAX_VALUE - 99));
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("HighQuantity"))));
    }

    @Test
    void addMealTestWithNegativeQuantity() throws Exception {
        this.formData.remove("quantity");
        this.formData.add("quantity", "-1");
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("NegativeQuantity"))));
    }

    @Test
    void addMealTestWithInvalidQuantity3() throws Exception {
        this.formData.remove("quantity");
        this.formData.add("quantity", "-1.2");
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidArgument"))));
    }

    @Test
    void addMealTestWithInvalidQuantity2() throws Exception {
        this.formData.remove("quantity");
        this.formData.add("quantity", "1.2");
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidArgument"))));
    }

    @Test
    void addMealTestWithInvalidQuantity() throws Exception {
        this.formData.remove("quantity");
        this.formData.add("quantity", "null");
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidArgument"))));
    }

    @Test
    void addMealTestWithEmptyQuantity() throws Exception {
        this.formData.remove("quantity");
        this.formData.add("quantity", "");
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Quantity"))));
    }

    @Test
    void addMealTestWithNullQuantity() throws Exception {
        this.formData.remove("quantity");
        this.formData.add("quantity", null);
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Quantity"))));
    }

    @Test
    void addMealTestWithOutQuantity() throws Exception {
        this.formData.remove("quantity");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Quantity"))));
    }


    /********************************* Tests for Status *********************************/
    @Test
    void AddMealTestWithOutSideStatusValue3() throws Exception {
        this.formData.remove("status");
        this.formData.add("status", "3 ");


        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("OutSideStatusValue"))));
    }

    @Test
    void AddMealTestWithOutSideStatusValue2() throws Exception {
        this.formData.remove("status");
        this.formData.add("status", "-5");


        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("OutSideStatusValue"))));
    }

    @Test
    void AddMealTestWithOutSideStatusValue() throws Exception {
        this.formData.remove("status");
        this.formData.add("status", "5");


        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("OutSideStatusValue"))));
    }

    @Test
    void AddMealTestWithEmptyStatusValue() throws Exception {
        this.formData.remove("status");
        this.formData.add("status", " ");

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Status"))));
    }

    @Test
    void AddMealTestWithInvalidStatusValue2() throws Exception {
        this.formData.remove("status");
        this.formData.add("status", "-5rffr");

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidArgument"))));
    }

    @Test
    void AddMealTestWithInvalidStatusValue() throws Exception {
        this.formData.remove("status");
        this.formData.add("status", "564rffr");

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidArgument"))));
    }

    @Test
    void AddMealTestWithNegativeStatusValue3() throws Exception {
        this.formData.remove("status");
        this.formData.add("status", "-1");

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)

                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("OutSideStatusValue"))));
    }

    @Test
    void AddMealTestWithNullStatusValue() throws Exception {
        this.formData.remove("status");
        this.formData.add("status", null);

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Status"))));
    }

    @Test
    void AddMealWithoutStatus() throws Exception {
        this.formData.remove("status");

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Status"))));
    }


    /*****************************************  Tests For   Description  *****************************************/


    @Test
    void AddMealTestWithTooLongDescription() throws Exception {
        // given :  remove label from formData
        this.formData.remove("description");
        // word  with  101  characters
        String tooLongLabel = "a".repeat(601);
        this.formData.add("description", tooLongLabel); // length  must be  < 3 without spaces

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LongDescriptionLength"))));
    }

    @Test
    void AddMealTestWithNullDescriptionValue() throws Exception {
        this.formData.remove("description");
        this.formData.add("description", null);

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Description"))));
    }

    @Test
    void AddMealWithoutDescription() throws Exception {

        // given :  remove label from formData
        this.formData.remove("description");

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Description"))));
    }

    @Test
    void AddMealTestWithTooShortDescription() throws Exception {
        // given :  remove label from formData
        this.formData.remove("description");
        this.formData.add("description", "    ad     "); // length  must be  < 3 without spaces

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("ShortDescriptionLength"))));
    }


    /******************************************** Tests for Category ********************************************/
    @Test
    void AddMealTestWithTooLongCategory() throws Exception {
        // given :  remove label from formData
        this.formData.remove("category");
        // word  with  101  characters
        String tooLongLabel = "t".repeat(45);
        this.formData.add("category", tooLongLabel); // length  must be  < 3 without spaces

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LongCategoryLength"))));
    }

    @Test
    void AddMealTestWithNullCategoryValue() throws Exception {
        this.formData.remove("category");
        this.formData.add("category", null);

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Category"))));
    }

    @Test
    void AddMealTestWithTooShortCategory() throws Exception {
        // given :  remove label from formData
        this.formData.remove("category");
        this.formData.add("category", "    ad     "); // length  must be  < 3 without spaces

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("ShortCategoryLength"))));
    }

    @Test
    void AddMealWithoutCategory() throws Exception {

        // given :  remove label from formData
        this.formData.remove("category");

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Category"))));
    }


    /******************************************** Tests for Label ********************************************/
    @Test
    void AddMealWithoutLabel() throws Exception {
        // given :  remove label from formData
        this.formData.remove("label");

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Label"))));

    }

    @Test
    void AddMealTestWithNullLabelValue() throws Exception {
        // given :  remove label from formData
        this.formData.remove("label");
        this.formData.add("label", null);

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Label"))));

    }

    @Test
    void AddMealTestWithTooShortLabel() throws Exception {
        // given :  remove label from formData
        this.formData.remove("label");
        this.formData.add("label", "    a        d     "); // length  must be  < 3 without spaces  ( all spaces are removed )

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("ShortLabelLength"))));

    }

    @Test
    void AddMealTestWithTooLongLabel() throws Exception {
        // given :  remove label from formData
        this.formData.remove("label");
        // word  with  101  characters
        String tooLongLabel = "test".repeat(26);
        this.formData.add("label", tooLongLabel); // length  must be  < 3 without spaces

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LongLabelLength"))));


    }


}


























