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
public class UpdateMealTest  extends   AbstractMealTest {
   //THE ID  CAN NOT BE NULL OR LESS THAN 0
   private final Map<String, String> exceptionsMap = Map.ofEntries(
           Map.entry("InvalidID", "THE ID  CAN NOT BE NULL OR LESS THAN 0"),
           Map.entry("InvalidArgument", "ARGUMENT NOT VALID")
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
        this.formData.add("id" , "1");
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
                       new MealEntity("Entrée", "Salade de tomates", "Salade", new BigDecimal("2.3"), 1 ,  1 , image),
                       new MealEntity("Plat", "Poulet", "Poulet", new BigDecimal("2.3"), 1 ,  1 , image1)
               );
       this.mealDao.saveAll(meals);

      }
   @AfterEach
    void cleanDatabase() {
        this.mealDao.deleteAll();
    }






    /********************************************* ID MEAL ************************************************/

    @Test
    void updateMealWithInvalidArgumentID2() throws Exception {
        this.formData.set("id", "0.2");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart( HttpMethod.PUT,super.UPDATE_MEAL_URL)
                .file(this.imageData)
                .params(this.formData));

        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(this.exceptionsMap.get("InvalidArgument"))));
    }

    @Test
    void updateMealWithInvalidArgumentID() throws Exception {
        this.formData.set("id", "1.0");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart( HttpMethod.PUT,super.UPDATE_MEAL_URL)
                .file(this.imageData)
                .params(this.formData));

        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(this.exceptionsMap.get("InvalidArgument"))));
    }

    @Test
    void updateMealWithNegativeID() throws Exception {
        this.formData.set("id", "-5");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart( HttpMethod.PUT,super.UPDATE_MEAL_URL)
                .file(this.imageData)
                .params(this.formData));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(this.exceptionsMap.get("InvalidID"))));
    }


    @Test
    void updateMealWithEmptyID() throws Exception {
        this.formData.set("id", "");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart( HttpMethod.PUT,super.UPDATE_MEAL_URL)
                .file(this.imageData)
                .params(this.formData));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(this.exceptionsMap.get("InvalidID"))));
    }
    @Test
    void updateMealWithInvalidID() throws Exception {
        this.formData.set("id", "");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart( HttpMethod.PUT,super.UPDATE_MEAL_URL)
                .file(this.imageData)
                .params(this.formData));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(this.exceptionsMap.get("InvalidID"))));
    }
    @Test
    void updateMealWithIDNull() throws Exception {

        this.formData.set("id", null);

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart( HttpMethod.PUT,super.UPDATE_MEAL_URL)
                .file(this.imageData)
                .params(this.formData));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(this.exceptionsMap.get("InvalidID"))));
    }

    @Test
    void  updateMealWithOutID() throws Exception {

        this.formData.remove("id");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart( HttpMethod.PUT,super.UPDATE_MEAL_URL)
                .file(this.imageData)
                .params(this.formData));


        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(this.exceptionsMap.get("InvalidID"))));
    }




    @Test
    void updateMealWithInvalidIDArgument3() throws Exception {
        this.formData.set("id", "1.eoje");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart( HttpMethod.PUT,super.UPDATE_MEAL_URL)
                .file(this.imageData)
                .params(this.formData));

        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(this.exceptionsMap.get("InvalidArgument"))));
    }

    @Test
    void updateMealWithInvalidIDArgument2() throws Exception {
        this.formData.set("id", "1.5");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart( HttpMethod.PUT,super.UPDATE_MEAL_URL)
                .file(this.imageData)
                .params(this.formData));

        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(this.exceptionsMap.get("InvalidArgument"))));
    }

    @Test
    void updateMealWithInvalidIDArgument() throws Exception {
        this.formData.set("id", "erfzr");

        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart( HttpMethod.PUT,super.UPDATE_MEAL_URL)
                .file(this.imageData)
                .params(this.formData));

        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(this.exceptionsMap.get("InvalidArgument"))));
    }




}
