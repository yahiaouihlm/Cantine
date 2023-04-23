package fr.sqli.Cantine.controller.admin;

import fr.sqli.Cantine.dao.IMealDao;
import fr.sqli.Cantine.entity.ImageEntity;
import fr.sqli.Cantine.entity.MealEntity;
import fr.sqli.Cantine.service.admin.meals.IMealService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RemoveMealTest  extends   AbstractMealTest{


    //"THE ID CAN NOT BE NULL OR LESS THAN 0"
    private final Map<String, String> exceptionsMap = Map.ofEntries(
            Map.entry("InvalidMealID", "THE ID CAN NOT BE NULL OR LESS THAN 0"),
            Map.entry("InvalidArgument", "ARGUMENT NOT VALID"),
            Map.entry("missingParam", "MISSING PARAMETER")
            );
    @Autowired
    private IMealDao mealDao;

    @Autowired
    private IMealService  mealService;

    @Autowired
    private MockMvc mockMvc;
    /*TODO
          to  reloas image  after    each   tests
     */
    @BeforeEach
    public void init() {
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
    void  cleanUp(){
        this.mealDao.deleteAll();
    }

    @Test
    void removeMealTestWithInvalidID() throws Exception {
        var result =  this.mockMvc.perform(delete(super.DELETE_MEAL_URL+"?idMeal=ozzedoz"));

        result.andExpect(status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidArgument"))));

    }





    @Test
    void removeMealTestWithNullID() throws Exception {
        var result =  this.mockMvc.perform(delete(super.DELETE_MEAL_URL+"?idMeal="));


        result.andExpect(status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("missingParam"))));

    }



}
