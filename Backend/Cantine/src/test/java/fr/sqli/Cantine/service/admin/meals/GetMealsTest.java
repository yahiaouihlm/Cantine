package fr.sqli.Cantine.service.admin.meals;

import fr.sqli.Cantine.dao.IMealDao;
import fr.sqli.Cantine.dto.out.MealDtout;
import fr.sqli.Cantine.entity.ImageEntity;
import fr.sqli.Cantine.entity.MealEntity;
import fr.sqli.Cantine.service.admin.MealService;
import fr.sqli.Cantine.service.admin.exceptions.InvalidMealInformationAdminException;
import fr.sqli.Cantine.service.admin.exceptions.MealNotFoundAdminException;
import fr.sqli.Cantine.service.images.IImageService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class GetMealsTest {

    @Mock
    MealService iMealService;

    @Mock
    IMealDao iMealDao;

    MealEntity mealEntity;
    @Mock
    MultipartFile multipartFile;
    @Mock
    IImageService imageService;
    @Autowired
    Environment environment;


    @BeforeEach
    void setUp() throws MealNotFoundAdminException, InvalidMealInformationAdminException {
        this.iMealService = new MealService(environment, iMealDao, null);
        this.mealEntity = new MealEntity(1, "Meal 1", "first Meal To  Test ", "Frites", new BigDecimal("1.3"), 1, 1, new ImageEntity());

    }

    @Test
    @DisplayName("Test getAllMeals() with list of 2 elements")
    void getAllMealsWithListOf2Elements() {
        var meal2 = new MealEntity(2, "Meal 2", "firt Meal To  Test 2  ", "Frites", new BigDecimal("1.3"), 1, 1, new ImageEntity());
        final var ListToFindAsEntity = List.of(this.mealEntity, meal2);

        final var ListToGetAsDtout = List.of(this.mealEntity, meal2).stream().map(meal -> new MealDtout(meal, this.environment.getProperty("sqli.cantine.images.url.meals"))).toList();

        Mockito.when(this.iMealDao.findAll()).thenReturn(ListToFindAsEntity);
        var result = this.iMealService.getAllMeals();

        Assertions.assertEquals(2, result.size());

        /** first element of List  */
        Assertions.assertEquals(ListToGetAsDtout.get(0).getId(), result.get(0).getId());
        Assertions.assertEquals(ListToGetAsDtout.get(0).getDescription(), result.get(0).getDescription());


        /** second element of List  */
        Assertions.assertEquals(ListToGetAsDtout.get(1).getId(), result.get(1).getId());
        Assertions.assertEquals(ListToGetAsDtout.get(1).getDescription(), result.get(1).getDescription());

        Mockito.verify(this.iMealDao).findAll();

    }

    @Test
    @DisplayName("Test  getAllMeals with empty List  ")
    void getAllMealsWithNoMealInDB() {
        Mockito.when(this.iMealDao.findAll()).thenReturn(List.of());
        var result = this.iMealService.getAllMeals();
        Assertions.assertEquals(0, result.size());
        Assertions.assertEquals(Collections.emptyList(), result);

        Mockito.verify(this.iMealDao).findAll();
    }


    /******************************************* getMealByid methode *****************************************************/
    @Test
    @DisplayName("Test  getMealByID with valid ID  return a Meal Instanced By Mokito ")
    void geMealWithValidId() throws MealNotFoundAdminException, InvalidMealInformationAdminException {
        final Integer idMeal = 1;
        final String urlMealImage = this.environment.getProperty("sqli.cantine.images.url.meals");

        Mockito.when(this.iMealDao.findById(1)).thenReturn(Optional.of(this.mealEntity));
        MealDtout resultTest = this.iMealService.getMealByID(1);
        MealDtout shouldBeresult = new MealDtout(this.mealEntity, urlMealImage);

        Assertions.assertEquals(shouldBeresult.getId(), resultTest.getId());
        Assertions.assertEquals(shouldBeresult.getCategorie(), resultTest.getCategorie());
        Assertions.assertEquals(shouldBeresult.getPrixht(), resultTest.getPrixht());

        Mockito.verify(this.iMealDao).findById(idMeal);
    }

    @Test
    @DisplayName("Test  getMealByID with invalid ID  with MAX_VALUE Because this ID Can't be recorded in the database ")
    void getMealByIdWithNoutFoundID() {
        final Integer idMeal = Integer.MAX_VALUE;

        Mockito.when(this.iMealDao.findById(idMeal)).thenReturn(Optional.empty());


        Assertions.assertThrows(MealNotFoundAdminException.class, () -> {
            this.iMealService.getMealByID(idMeal);
        });

        Mockito.verify(this.iMealDao).findById(idMeal);
    }


    @Test
    @DisplayName("Test  getMealByID with invalid ID  with negative value (-1 ) ")
    void getMealIdWithInvalidID() {
        Integer IdTest = -1;
        Assertions.assertThrows(InvalidMealInformationAdminException.class, () -> {
            this.iMealService.getMealByID(IdTest);
        });

    }

    @Test
    @DisplayName("Test  getMealByID with null ID  ")
    void getMealByIdWithNullID() {
        Integer IdTest = null;
        Assertions.assertThrows(InvalidMealInformationAdminException.class, () -> {
            this.iMealService.getMealByID(IdTest);
        });
    }


}