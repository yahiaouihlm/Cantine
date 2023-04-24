package fr.sqli.Cantine.controller.admin;

import fr.sqli.Cantine.dao.IMealDao;
import fr.sqli.Cantine.entity.ImageEntity;
import fr.sqli.Cantine.entity.MealEntity;
import fr.sqli.Cantine.entity.MenuEntity;
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
            Map.entry("missingParam", "MISSING PARAMETER"),
            Map.entry("mealNotFound", "NO MEAL WAS FOUND WITH THIS ID")
            );
    @Autowired
    private IMealDao mealDao;

    @Autowired
    private IMealService  mealService;

    private     List<MealEntity> meals;
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

     this.meals =
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


 /*  TODO whe We Make Menu */
  /* @Test
    void removeMealInAssociationWithMenu () throws Exception {
        var  expectedExceptionMessage=  "THE MEAL WITH AN label  = " + this.meals.get(0).getLabel() + " IS PRESENT IN A OTHER  MENU(S) AND CAN NOT BE DELETED" ;
        this.meals.get(0).setMenus(List.of(new MenuEntity()));

        var idMealToRemov = this.mealDao.save(this.meals.get(0)).getId();
//        MealEntity  mealToFind = this.meals.get(0);
//
//        var mealsInDbL =   this.mealDao.findAll();
//        Integer idMealToRemov  = null ;
//       for ( MealEntity mealEntity : mealsInDbL ) {
//             if (mealEntity.getLabel().equals("Entrée")) {
//               idMealToRemov = mealEntity.getId();
//               break;
//           }
//       }
//


        var result =  this.mockMvc.perform(delete(super.DELETE_MEAL_URL+"?idMeal="+idMealToRemov));

        result.andExpect(status().isConflict())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get(expectedExceptionMessage) )));



    }*/


    @Test
    void  removeMealTestWithMealNotFound() throws Exception {

        var result = this.mockMvc.perform(delete(DELETE_MEAL_URL + "?idMeal=" + (Integer.MAX_VALUE - 10)));

        result.andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("mealNotFound"))));

    }

    @Test
    void  removeMealTestWithNegativeID() throws Exception {
        var result =  this.mockMvc.perform(delete(super.DELETE_MEAL_URL+"?idMeal=-5"));

        result.andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidMealID"))));

    }



    @Test
    void  removeMealTestWithInValidID2() throws Exception {
        var result =  this.mockMvc.perform(delete(super.DELETE_MEAL_URL+"?idMeal=1000000000000000000000000000000000000000000"));

        result.andExpect(status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.content().json(super.exceptionMessage(exceptionsMap.get("InvalidArgument"))));

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
