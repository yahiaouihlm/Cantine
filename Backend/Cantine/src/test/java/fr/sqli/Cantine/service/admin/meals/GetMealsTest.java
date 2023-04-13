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
import org.springframework.mock.env.MockEnvironment;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


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
    @Mock
    MockEnvironment environment;


    @BeforeEach
    void setUp() throws MealNotFoundAdminException, InvalidMealInformationAdminException {
        this.environment = new MockEnvironment();
        this.environment.setProperty("sqli.cantine.images.url.meals", "http://localhost:8080/images/meals/");
        this.iMealService = new MealService(environment, iMealDao, null);
        this.mealEntity = new MealEntity();
        this.mealEntity.setId(1);
        this.mealEntity.setStatus(1);
        this.mealEntity.setPrice(BigDecimal.valueOf(1.3));
        this.mealEntity.setQuantity(1);
        this.mealEntity.setCategory("Frites");
        this.mealEntity.setDescription("first Meal To  Test");
        this.mealEntity.setLabel("Meal 1");
        this.mealEntity.setImage(new ImageEntity());

    }

    @Test
    @DisplayName("Test getAllMeals() with list of 2 elements")
    void getAllMealsWithListOf2Elements() {
        var meal2 = new MealEntity();
        meal2.setId(2);
        meal2.setStatus(1);
        meal2.setPrice(BigDecimal.valueOf(1.3));
        meal2.setQuantity(1);
        meal2.setCategory("Frites");
        meal2.setDescription("firt Meal To  Test 2  ");
        meal2.setLabel("Meal 2");
        meal2.setImage(new ImageEntity());

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
        Assertions.assertEquals(shouldBeresult.getCategory(), resultTest.getCategory());
        Assertions.assertEquals(shouldBeresult.getPrice(), resultTest.getPrice());

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