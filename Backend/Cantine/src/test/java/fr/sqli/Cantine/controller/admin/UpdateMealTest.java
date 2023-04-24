package fr.sqli.Cantine.controller.admin;


import fr.sqli.Cantine.dao.IMealDao;
import fr.sqli.Cantine.entity.ImageEntity;
import fr.sqli.Cantine.entity.MealEntity;
import fr.sqli.Cantine.service.admin.meals.MealService;
import fr.sqli.Cantine.service.images.ImageService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
public class UpdateMealTest  extends   AbstractMealTest {
   //THE ID  CAN NOT BE NULL OR LESS THAN 0
   private final Map<String, String> exceptionsMap = Map.ofEntries(
           Map.entry("InvalidID", "The id can not be null or less than 0"),
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






    /******************************************* ID MEAL ********************************************/

   @Test
    void  updateMealWithOutLabel() throws Exception {

        this.formData.remove("id");

       var requestBuilder = MockMvcRequestBuilders.multipart(super.UPDATE_MEAL_URL)
               .file(this.imageData)
               .params(this.formData);

       var result = this.mockMvc.perform(requestBuilder.method(HttpMethod.PUT));


       result.andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                        .andExpect(MockMvcResultMatchers.content().json((this.exceptionsMap.get("InvalidArgument"))));
    }


}
