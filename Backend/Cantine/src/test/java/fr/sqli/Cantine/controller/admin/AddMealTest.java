package fr.sqli.Cantine.controller.admin;

import ch.qos.logback.core.net.ObjectWriter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.sqli.Cantine.dao.IMealDao;
import fr.sqli.Cantine.dto.in.MealDtoIn;
import fr.sqli.Cantine.entity.MealEntity;
import fr.sqli.Cantine.service.admin.meals.MealService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;

@SpringBootTest
@AutoConfigureMockMvc
public class AddMealTest   extends  AbstractMealTest {
     private   final String  INVALID_LABEL = "LABEL_IS_MANDATORY";
    @Autowired
    private MealService mealService;

    @Autowired
    private IMealDao mealDao;

    @Autowired
    private MockMvc mockMvc;

    private MultiValueMap<String, String> formData;

    private MockMultipartFile  imageData;


    @BeforeEach
    public  void  initMeaDtoIn () throws IOException {
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


    @Test
    void  AddMealWithNullLabel () throws Exception {
        // given :  remove label from formData
        this.formData.remove("label" );

       // when : call addMeal
          var result  =  this.mockMvc.perform( MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                          .file(this.imageData)
                    .params(this.formData)
                  .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));



        // then :
       result.andExpect(MockMvcResultMatchers.status().isBadRequest())
               .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(INVALID_LABEL)) )    ;

    }

   @Test
   void  AddMealTestWithNullLableValue () throws Exception {
       // given :  remove label from formData
       this.formData.remove("label" );
       this.formData.add("label", null );

       // when : call addMeal
       var result  =  this.mockMvc.perform( MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
               .file(this.imageData)
               .params(this.formData)
               .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));



       // then :
       result.andExpect(MockMvcResultMatchers.status().isBadRequest())
               .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(INVALID_LABEL)) )    ;

   }







}


























