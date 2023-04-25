package fr.sqli.Cantine.controller.admin;


import fr.sqli.Cantine.dao.IMealDao;
import fr.sqli.Cantine.entity.ImageEntity;
import fr.sqli.Cantine.entity.MealEntity;
import fr.sqli.Cantine.service.admin.meals.MealService;
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

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@SpringBootTest
@AutoConfigureMockMvc
public class UpdateMealTest extends AbstractMealTest {
    //THE ID  CAN NOT BE NULL OR LESS THAN 0
    private final Map<String, String> exceptionsMap = Map.ofEntries(
            Map.entry("InvalidID", "THE ID  CAN NOT BE NULL OR LESS THAN 0"),
            Map.entry("InvalidArgument", "ARGUMENT NOT VALID"),
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
            Map.entry("OutSideStatusValue", "STATUS MUST BE 0 OR 1 FOR ACTIVE OR INACTIVE"),
            Map.entry("HighPrice", "PRICE MUST BE LESS THAN 1000"),
            Map.entry("HighQuantity", "QUANTITY_IS_TOO_HIGH"),
            Map.entry("NegativePrice", "PRICE MUST BE GREATER THAN 0"),
            Map.entry("NegativeQuantity", "QUANTITY MUST BE GREATER THAN 0"),
            Map.entry("InvalidImageFormat", "INVALID IMAGE TYPE ONLY PNG , JPG , JPEG OR SVG  ARE ACCEPTED"),
            Map.entry("MealAddedSuccessfully", "MEAL ADDED SUCCESSFULLY")
    );
    @Autowired
    private IMealDao mealDao;

    @Autowired
    private MealService mealService;

    @Autowired
    private MockMvc mockMvc;

    private LinkedMultiValueMap<String, String> formData;
    private MockMultipartFile imageData;


    @BeforeEach
    public void initFormData() throws IOException {
        this.formData = new LinkedMultiValueMap<>();
        this.formData.add("id", "1");
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

    @BeforeEach
    void initDatabase() {
        ImageEntity image = new ImageEntity();
        image.setImagename("ImageMealForTest.jpg");
        ImageEntity image1 = new ImageEntity();
        image1.setImagename("ImageMealForTest1.jpg");

        List<MealEntity> meals =
                List.of(
                        new MealEntity("Entrée", "Salade de tomates", "Salade", new BigDecimal("2.3"), 1, 1, image),
                        new MealEntity("Plat", "Poulet", "Poulet", new BigDecimal("2.3"), 1, 1, image1)
                );
        this.mealDao.saveAll(meals);

    }

    @AfterEach
    void cleanDatabase() {
        this.mealDao.deleteAll();
    }


    /*************************************** DESCRIPTION TESTS ********************************************/


    @Test
    void AddMealTestWithTooLongDescription() throws Exception {
        // given :  remove label from formData
        // word  with  101  characters
        String tooLongLabel = "a".repeat(601);
        this.formData.set("description", tooLongLabel); // length  must be  < 3 without spaces

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, super.UPDATE_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LongDescriptionLength"))));
    }

    @Test
    void AddMealTestWithNullDescriptionValue() throws Exception {
        this.formData.set("description", null);

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, super.UPDATE_MEAL_URL)
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
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, super.UPDATE_MEAL_URL)
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
        this.formData.set("description", "    ad     "); // length  must be  < 3 without spaces

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, super.UPDATE_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("ShortDescriptionLength"))));
    }




    /********************************************* Category   ************************************************/

    @Test
    void updateMealTestWithTooLongCategory() throws Exception {
        // given :  remove label from formData
        // word  with  101  characters
        String tooLongLabel = "t".repeat(45);
        this.formData.set("category", tooLongLabel); // length  must be  < 3 without spaces

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, super.UPDATE_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LongCategoryLength"))));
    }

    @Test
    void updateMealTestWithNullCategoryValue() throws Exception {
        this.formData.set("category", null);

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, super.UPDATE_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Category"))));
    }

    @Test
    void updateMealTestWithTooShortCategory() throws Exception {
        // given :  remove label from formData
        this.formData.set("category", "    ad     "); // length  must be  < 3 without spaces

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, super.UPDATE_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("ShortCategoryLength"))));
    }

    @Test
    void updateMealWithoutCategory() throws Exception {

        // given :  remove label from formData
        this.formData.remove("category");

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, super.UPDATE_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Category"))));
    }




    /********************************************* Label  ************************************************/

    @Test
    void updateMealWithoutLabel() throws Exception {
        // given :  remove label from formData
        this.formData.remove("label");

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, super.UPDATE_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Label"))));

    }

    @Test
    void updateMealTestWithNullLabelValue() throws Exception {
        // given :  remove label from formData
        this.formData.set("label", null);

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, super.UPDATE_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("Label"))));

    }

    @Test
    void updateMealTestWithTooShortLabel() throws Exception {
        // given :  remove label from formData
        this.formData.set("label", "    a        d     "); // length  must be  < 3 without spaces  ( all spaces are removed )

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, super.UPDATE_MEAL_URL)
                        .file(this.imageData)
                        .params(this.formData)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));



                // then :
                result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                        .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("ShortLabelLength"))));

    }

    @Test
    void updateMealTestWithTooLongLabel() throws Exception {
        // given :  remove label from formData
        // word  with  101  characters
        String tooLongLabel = "test".repeat(26);
        this.formData.set("label", tooLongLabel); // length  must be  < 3 without spaces

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, super.UPDATE_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
        );
        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("LongLabelLength"))));


    }














    /********************************************* ID MEAL ************************************************/

    @Test
    void updateMealWithInvalidArgumentID2() throws Exception {
        this.formData.set("id", "0.2");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, super.UPDATE_MEAL_URL)
                .file(this.imageData)
                .params(this.formData) .contentType(MediaType.MULTIPART_FORM_DATA_VALUE ) );

        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(this.exceptionsMap.get("InvalidArgument"))));
    }

    @Test
    void updateMealWithInvalidArgumentID() throws Exception {
        this.formData.set("id", "1.0");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, super.UPDATE_MEAL_URL)
                .file(this.imageData)
                .params(this.formData) .contentType(MediaType.MULTIPART_FORM_DATA_VALUE ) );

        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(this.exceptionsMap.get("InvalidArgument"))));
    }

    @Test
    void updateMealWithNegativeID() throws Exception {
        this.formData.set("id", "-5");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, super.UPDATE_MEAL_URL)
                .file(this.imageData)
                .params(this.formData) .contentType(MediaType.MULTIPART_FORM_DATA_VALUE ) );

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(this.exceptionsMap.get("InvalidID"))));
    }


    @Test
    void updateMealWithEmptyID() throws Exception {
        this.formData.set("id", "");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, super.UPDATE_MEAL_URL)
                .file(this.imageData)
                .params(this.formData) .contentType(MediaType.MULTIPART_FORM_DATA_VALUE ) );

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(this.exceptionsMap.get("InvalidID"))));
    }

    @Test
    void updateMealWithInvalidID() throws Exception {
        this.formData.set("id", "");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, super.UPDATE_MEAL_URL)
                .file(this.imageData)
                .params(this.formData) .contentType(MediaType.MULTIPART_FORM_DATA_VALUE ) );

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(this.exceptionsMap.get("InvalidID"))));
    }

    @Test
    void updateMealWithIDNull() throws Exception {

        this.formData.set("id", null);

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, super.UPDATE_MEAL_URL)
                .file(this.imageData)
                .params(this.formData) .contentType(MediaType.MULTIPART_FORM_DATA_VALUE ) );


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(this.exceptionsMap.get("InvalidID"))));
    }

    @Test
    void updateMealWithOutID() throws Exception {

        this.formData.remove("id");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, super.UPDATE_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE )
        );


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(this.exceptionsMap.get("InvalidID"))));
    }


    @Test
    void updateMealWithInvalidIDArgument3() throws Exception {
        this.formData.set("id", "1.eoje");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, super.UPDATE_MEAL_URL)
                .file(this.imageData)
                .params(this.formData).contentType(MediaType.MULTIPART_FORM_DATA_VALUE  ));

        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(this.exceptionsMap.get("InvalidArgument"))));
    }

    @Test
    void updateMealWithInvalidIDArgument2() throws Exception {
        this.formData.set("id", "1.5");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, super.UPDATE_MEAL_URL)
                .file(this.imageData)
                .params(this.formData).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(this.exceptionsMap.get("InvalidArgument"))));
    }

    @Test
    void updateMealWithInvalidIDArgument() throws Exception {
        this.formData.set("id", "erfzr");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, super.UPDATE_MEAL_URL)
                .file(this.imageData)
                .params(this.formData).contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(this.exceptionsMap.get("InvalidArgument"))));
    }


}
