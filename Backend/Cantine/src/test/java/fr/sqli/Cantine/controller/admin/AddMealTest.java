package fr.sqli.Cantine.controller.admin;

import fr.sqli.Cantine.dao.IMealDao;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@AutoConfigureMockMvc
public class AddMealTest extends AbstractMealTest {
    private  final HashMap<String,  String> exceptions = new HashMap<>(
            Map.of(
                    "Label", "LABEL_IS_MANDATORY",
                    "Category", "CATEGORY_IS_MANDATORY",
                    "Description", "DESCRIPTION_IS_MANDATORY",
                    "Price", "PRICE_IS_MANDATORY",
                    "Quantity", "QUANTITY_IS_MANDATORY",
                    "Status", "STATUS_IS_MANDATORY",
                    "ShortLabelLength", "LABEL_IS_TOO_SHORT",
                    "LongLabelLength", "LABEL_IS_TOO_LONG"
            )
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
    public void initMeaDtoIn() throws IOException {
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

    /*
    add containerTest  for  Label Too short
   */

    @Test
    void AddMealWithCategory () throws Exception {

        // given :  remove label from formData
        this.formData.remove("category");

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptions.get("Category"))));
    }



    /******************************************** Tests for Label ********************************************/
    @Test
    void AddMealWithNullLabel() throws Exception {
        // given :  remove label from formData
        this.formData.remove("label");

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptions.get("Label"))));

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
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptions.get("Label"))));

    }

    @Test
    void AddMealTestWithTooShortLabel() throws Exception {
        // given :  remove label from formData
        this.formData.remove("label");
        this.formData.add("label", "    ad     "); // length  must be  < 3 without spaces

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptions.get("ShortLabelLength"))));

    }

    @Test
    void AddMealTestWithTooLongLabel() throws Exception {
        // given :  remove label from formData
        this.formData.remove("label");
        // word  with  101  characters
        String tooLongLabel = """
                                 Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor.
                                 Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. 
                                 Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo,
                                 fringilla vel, aliquet nec, vulputate eget, arcu. In  justo, rhoncus ut, imperdiet a, venenatis vitae, justo. Nullam
                                 dictum felis eu pede mollis pretium. Integer tincidunt. Cras dapielementum semper nisi. Aenean vulputate
                                   eleifend tellus. Aenean leo ligula, porttitor eu, consequat vitae, eleifend ac, enim. Aliquam lorem ante, dapibus in, 
                                    viverra quis, feugiat a,e
                             """;
        this.formData.add("label", tooLongLabel); // length  must be  < 3 without spaces

        // when : call addMeal
        var result = this.mockMvc.perform(MockMvcRequestBuilders.multipart(ADD_MEAL_URL)
                .file(this.imageData)
                .params(this.formData)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));


        // then :
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptions.get("LongLabelLength"))));


    }





}


























