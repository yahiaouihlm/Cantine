package fr.sqli.Cantine.controller.admin.meals;


import fr.sqli.Cantine.controller.admin.AbstractContainerConfig;
import fr.sqli.Cantine.dao.IMealDao;
import fr.sqli.Cantine.entity.ImageEntity;
import fr.sqli.Cantine.entity.MealEntity;
import fr.sqli.Cantine.service.admin.meals.MealService;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
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

@SpringBootTest
@AutoConfigureMockMvc
class GetMealTest extends AbstractContainerConfig implements   IMealTest {

    @Autowired
    private MealService mealService;

    @Autowired
    private IMealDao mealDao;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach //  clean database before each test
    public void setUp() {
        this.mealDao.deleteAll();
    }

    @AfterEach //  clean database after each test
    public void tearDown() {
        this.mealDao.deleteAll();
    }

    @Test
    @Rollback(true)
    @DisplayName("Get all meals tests  : 2 meals in database")
    public void testGetAllMealsTest() throws Exception {
////        given  : 2 meals in database
        ImageEntity image = new ImageEntity();
        image.setImagename("ImageMenuForTest.jpg");
        ImageEntity image1 = new ImageEntity();
        image1.setImagename("ImageMealForTest1.jpg");

        List<MealEntity> meals =
                List.of(
                        new MealEntity("Entrée", "Salade de tomates", "Salade", new BigDecimal("2.3"), 1, 1, image),
                        new MealEntity("Plat", "Poulet", "Poulet", new BigDecimal("2.3"), 1, 1, image1)
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
}