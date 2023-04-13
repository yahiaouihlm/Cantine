package fr.sqli.Cantine.controller.admin;


import fr.sqli.Cantine.dao.IMealDao;
import fr.sqli.Cantine.entity.ImageEntity;
import fr.sqli.Cantine.entity.MealEntity;
import fr.sqli.Cantine.service.admin.MealService;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class GetMealTest {

    /* @Container
     private PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:latest")
             .withDatabaseName("cantine")
             .withUsername("test")
             .withPassword("test");*/
    @Autowired
    private MealService mealService;

    @Autowired
    private IMealDao mealDao;

    @Autowired
    private MockMvc mockMvc;

   /* @Test
    public void testGetAllMealsTest() throws Exception {
        System.out.println("it should return all meals");
        // given  : 2 meals in database
        ImageEntity image = new ImageEntity();

        List<MealEntity> meals =
                List.of(
                        MealEntity.builder().categorie("Entrée").description("Salade de tomates").label("Salade de tomates").image(image).quantite(1)
                                .prixht(new BigDecimal("2.3")).status(1).build(),
                        MealEntity.builder().categorie("Entrée").description("Salade de tomates").label("tacos").image(image).quantite(2)
                                .prixht(new BigDecimal("8.6")).status(1).build()
                );
        mealDao.saveAll(meals);

        // when : get all meals
       var   result  =   this.mockMvc.perform(MockMvcRequestBuilders.get("/admin/meal/getAllMeals"));


       // then : 2 meals are returned  (verify status is 200)
       result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(CoreMatchers.is(meals.size())));


    }
}*/

/*
*       public  MealEntity( Integer idplat,  String label, String description, String categorie, BigDecimal prixht, Integer quantite, Integer status, ImageEntity image){
        this.idplat = idplat;
        this.label = label;
        this.description = description;
        this.categorie = categorie;
        this.prixht = prixht;
        this.quantite = quantite;
        this.status = status;
        this.image = image;
    }

*
* */

}