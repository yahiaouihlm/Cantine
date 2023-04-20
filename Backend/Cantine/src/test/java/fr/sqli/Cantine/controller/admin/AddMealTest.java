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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
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

    private MealDtoIn mealDtoIn;

    @BeforeEach
    public  void  initMeaDtoIn () throws IOException {


       // this.mealDtoIn.setImage(createMultipartFileFromFile(file));

    }


    @Test
    void  AddMealWithNullLabel () throws Exception {
        // given : mealDtoIn with null label
        this.mealDtoIn.setLabel(null);

        /// prepare request
        var  requestBody = new ObjectMapper().writeValueAsString(this.mealDtoIn);

       // when : call addMeal
        var  result  =  this.mockMvc.perform(MockMvcRequestBuilders.post(ADD_MEAL_URL)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .content(requestBody));


       result.andExpect(MockMvcResultMatchers.status().isBadRequest())
               .andExpect(MockMvcResultMatchers.content().string(INVALID_LABEL));

    }




    public MultipartFile createMultipartFileFromFile(File file) throws IOException {
        String contentType = "image/jpeg"; // ou récupérez le type de contenu à partir du fichier si vous le connaissez
        String filename = file.getName();
        byte[] content = readFileToByteArray(file);
        return new MockMultipartFile(filename, filename, contentType, content);
    }

    private byte[] readFileToByteArray(File file) throws IOException {
        try (InputStream inputStream = new FileInputStream(file)) {
            return inputStream.readAllBytes();
        }


    }






}


























