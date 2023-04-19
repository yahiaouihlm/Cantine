package fr.sqli.Cantine.controller.admin;


import fr.sqli.Cantine.dao.IMealDao;
import fr.sqli.Cantine.entity.ImageEntity;
import fr.sqli.Cantine.entity.MealEntity;
import fr.sqli.Cantine.service.admin.meals.MealService;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class GetMealTest {

     @Container
     private PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:latest")
             .withDatabaseName("cantine_tests")
             .withUsername("postgres")
             .withPassword("halim");
    @Autowired
    private MealService mealService;

    @Autowired
    private IMealDao mealDao;

    @Autowired
    private MockMvc mockMvc;

   @Test
   @Rollback(true)
    public void testGetAllMealsTest() throws Exception {
        System.out.println("it should return all meals");
        // given  : 2 meals in database
        ImageEntity image = new ImageEntity();
        image.setImagename("ImageMealForTest.jpg");

       ImageEntity image1 = new ImageEntity();
       image1.setImagename("ImageMealForTest1.jpg");
        List<MealEntity> meals =
                List.of(
                        new MealEntity("Entrée", "Salade de tomates", "Salade", new BigDecimal("2.3"), 1 ,  1 , image),
                        new MealEntity("Plat", "Poulet", "Poulet", new BigDecimal("2.3"), 1 ,  1 , image1)
                );
        mealDao.saveAll(meals);

        // when : get all meals
       var   result  =   this.mockMvc.perform(MockMvcRequestBuilders.get("/admin/meal/getAllMeals"));


       // then : 2 meals are returned  (verify status is 200)
       result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(CoreMatchers.is(meals.size())));
       this.mealDao.deleteAll();

    }

}