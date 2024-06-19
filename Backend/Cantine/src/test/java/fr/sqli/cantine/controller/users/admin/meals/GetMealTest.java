package fr.sqli.cantine.controller.users.admin.meals;


import fr.sqli.cantine.controller.AbstractContainerConfig;
import fr.sqli.cantine.dao.IMealDao;
import fr.sqli.cantine.entity.ImageEntity;
import fr.sqli.cantine.entity.MealEntity;
import fr.sqli.cantine.entity.MealTypeEnum;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class GetMealTest extends AbstractContainerConfig implements   IMealTest {

    final  String paramReq = "?" + "idMeal" + "=";
    @Autowired
    private IMealDao mealDao;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach //  clean database before each test
    public void setUp() {
        this.mealDao.deleteAll();
    }

    /*@AfterEach //  clean database after each test
    public void tearDown() {
        this.mealDao.deleteAll();
    }*/

    @Test
    @Rollback(true)
    @DisplayName("Get all meals tests  : 2 meals in database")
    public void testGetAllMealsTest() throws Exception {
////        given  : 2 meals in database
        ImageEntity image = new ImageEntity();
        image.setImagename(IMAGE_MEAL_FOR_TEST_NAME);
        ImageEntity image1 = new ImageEntity();
        image1.setImagename(SECOND_IMAGE_MEAL_FOR_TEST_NAME);
        MealTypeEnum mealTypeEnum = MealTypeEnum.getMealTypeEnum("ENTREE");
        List<MealEntity> meals =
                List.of(
                        new MealEntity("Entrée", "Salade de tomates", "Salade", new BigDecimal("2.3"), 1, 1, mealTypeEnum, image),
                        new MealEntity("Plat", "Poulet", "Poulet", new BigDecimal("2.3"), 1, 1,mealTypeEnum ,image1)
                );
        this.mealDao.saveAll(meals);

        // when : get all meals
        var result = this.mockMvc.perform(MockMvcRequestBuilders.get(GET_ALL_MEALS_URL));


        // then : 2 meals are returned  (verify status is 200)
        result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(CoreMatchers.is(meals.size())));
        result.andExpect(MockMvcResultMatchers.jsonPath("$[0].label").value(CoreMatchers.is(meals.get(0).getLabel())));
        result.andExpect(MockMvcResultMatchers.jsonPath("$[0].category").value(CoreMatchers.is(meals.get(0).getCategory())));

        result.andExpect(MockMvcResultMatchers.jsonPath("$[1].label").value(CoreMatchers.is(meals.get(1).getLabel())));
        result.andExpect(MockMvcResultMatchers.jsonPath("$[1].category").value(CoreMatchers.is(meals.get(1).getCategory())));

        result.andExpect(MockMvcResultMatchers.jsonPath("$[1].image").value(Matchers.not(Matchers.isEmptyOrNullString())));
        result.andExpect(MockMvcResultMatchers.jsonPath("$[1].image").value(Matchers.not(Matchers.isEmptyOrNullString())));

    }

    @Test
    @DisplayName("Get all meals tests  : empty database")
    public void testGetAllMealsWillEmptyDbTest() throws Exception {
        var result = this.mockMvc.perform(MockMvcRequestBuilders.get(GET_ALL_MEALS_URL));
        result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(CoreMatchers.is(0)));
    }


    /**************************************** Get meal by id tests ****************************************/

    @Test
    void getMenuByIdTest () throws Exception {
        ImageEntity image = new ImageEntity();
        image.setImagename(IMAGE_MEAL_FOR_TEST_NAME);
        MealTypeEnum mealTypeEnum = MealTypeEnum.getMealTypeEnum("ENTREE");
        MealEntity meal  = new MealEntity("Entrée", "Salade de tomates", "Salade", new BigDecimal("2.3"), 1, 1,mealTypeEnum, image);
        var idMeal =  this.mealDao.save(meal).getId();
        var  result  =   this.mockMvc.perform(MockMvcRequestBuilders.get(GET_ONE_MEAL_URL+ this.paramReq +idMeal ));

        result.andExpect( status().isOk());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(idMeal)));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.label", CoreMatchers.is(meal.getLabel())));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.description", CoreMatchers.is(meal.getDescription())));
    }


    @Test
    void getMenuByIdWithOutMenu () throws Exception {
        var  idMeal=  + 3; // id must be not exist in database
        var  result  =   this.mockMvc.perform(MockMvcRequestBuilders.get(GET_ONE_MEAL_URL+ this.paramReq +idMeal ));
        result.andExpect( status().isNotFound())
                .andExpect(content().string(super.exceptionMessage(exceptionsMap.get("mealNotFound"))));
    }



    @Test
    void getMenuByIdWithInvalidId2 () throws Exception {
        var  result  =   this.mockMvc.perform(MockMvcRequestBuilders.get(GET_ONE_MEAL_URL+ this.paramReq + "54fd" ));
        result.andExpect( status().isNotAcceptable())
                .andExpect(content().string(super.exceptionMessage(exceptionsMap.get("InvalidArgument"))));
    }
    @Test
    void getMenuByIdWithInvalidId () throws Exception {
        var  result  =   this.mockMvc.perform(MockMvcRequestBuilders.get(GET_ONE_MEAL_URL+ this.paramReq + "1.2" ));
        result.andExpect( status().isNotAcceptable())
                .andExpect(content().string(super.exceptionMessage(exceptionsMap.get("InvalidArgument"))));
    }
    @Test
    void getMenuByIdWithNegativeId () throws Exception {
        var  result  =   this.mockMvc.perform(MockMvcRequestBuilders.get(GET_ONE_MEAL_URL+ this.paramReq + "-1" ));
        result.andExpect( status().isBadRequest())
                .andExpect(content().string(super.exceptionMessage(exceptionsMap.get("InvalidParameter"))));
    }
    @Test
    void getMenuByIdWithNullId () throws Exception {
        var  result  =   this.mockMvc.perform(MockMvcRequestBuilders.get(GET_ONE_MEAL_URL+ this.paramReq + null ));
        result.andExpect( status().isNotAcceptable())
                .andExpect(content().string(super.exceptionMessage(exceptionsMap.get("InvalidArgument"))));
    }

    @Test
    void getMenuByIdWithOutID () throws Exception {
        var  result  =   this.mockMvc.perform(MockMvcRequestBuilders.get(GET_ONE_MEAL_URL+ this.paramReq));
        result.andExpect( status().isNotAcceptable())
                .andExpect(content().string(super.exceptionMessage(exceptionsMap.get("missingParam"))));
    }

}