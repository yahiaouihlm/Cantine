package fr.sqli.cantine.service.food.meals;

import fr.sqli.cantine.dao.IMealDao;
import fr.sqli.cantine.dto.out.food.MealDtOut;
import fr.sqli.cantine.entity.ImageEntity;
import fr.sqli.cantine.entity.MealEntity;
import fr.sqli.cantine.service.food.exceptions.FoodNotFoundException;
import fr.sqli.cantine.service.food.exceptions.InvalidFoodInformationException;
import fr.sqli.cantine.service.food.impl.MealService;
import fr.sqli.cantine.service.images.IImageService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.env.MockEnvironment;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class GetMealsTest {


    @Mock
    IMealDao iMealDao;
    @InjectMocks
    MealService iMealService;

    MealEntity mealEntity;

    @Mock
    IImageService imageService;
    @Mock
    MockEnvironment environment;


    @BeforeEach
    void setUp() {
        this.environment = new MockEnvironment();
        this.environment.setProperty("sqli.cantine.images.url.meals", "http://localhost:8080/images/meals/");
        this.iMealService = new MealService(this.environment, this.iMealDao, this.imageService);
        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setImagename("image-test");
        this.mealEntity = new MealEntity("Meal 1 ", "Frites", "first Meal To  Test", BigDecimal.valueOf(1.3), 1, 1, imageEntity);
        this.mealEntity.setId(1);


    }

    @Test
    @DisplayName("Test getAllMeals() with list of 2 elements")
    void getAllMealsWithListOf2Elements() {
        var meal2 = new MealEntity("Meal 2 ", "Frites", "firt Meal To  Test 2   ", BigDecimal.valueOf(1.3), 1, 1, new ImageEntity());
        ;
        meal2.setId(2);


        final var ListToFindAsEntity = List.of(this.mealEntity, meal2);

        final var ListToGetAsDtout = List.of(this.mealEntity, meal2).stream().map(meal -> new MealDtOut(meal, this.environment.getProperty("sqli.cantine.images.url.meals"))).toList();

        Mockito.when(this.iMealDao.findAll()).thenReturn(ListToFindAsEntity);
        var result = this.iMealService.getAllMeals();

        Assertions.assertEquals(2, result.size());

        /** first element of List  */
        Assertions.assertEquals(ListToGetAsDtout.get(0).getUuid(), result.get(0).getUuid());
        Assertions.assertEquals(ListToGetAsDtout.get(0).getDescription(), result.get(0).getDescription());


        /** second element of List  */
        Assertions.assertEquals(ListToGetAsDtout.get(1).getUuid(), result.get(1).getUuid());
        Assertions.assertEquals(ListToGetAsDtout.get(1).getDescription(), result.get(1).getDescription());

        Mockito.verify(this.iMealDao, times(1)).findAll();

    }

    @Test
    @DisplayName("Test  getAllMeals with empty List  ")
    void getAllMealsWithNoMealInDB() {

        Mockito.when(this.iMealDao.findAll()).thenReturn(List.of());

        var result = this.iMealService.getAllMeals();
        Assertions.assertEquals(0, result.size());
        Assertions.assertEquals(Collections.emptyList(), result);

        Mockito.verify(this.iMealDao, times(1)).findAll();
    }


    /******************************************* getMealByid methode *****************************************************/
    @Test
    @DisplayName("Test  getMealWithUuid with valid ID  return a Meal Instanced By Mockito ")
    void geMealWithValidUUID() throws InvalidFoodInformationException, FoodNotFoundException {
        String uuidMealToFind = this.mealEntity.getUuid();
        final String urlMealImage = this.environment.getProperty("sqli.cantine.images.url.meals");

        Mockito.when(this.iMealDao.findByUuid(uuidMealToFind)).thenReturn(Optional.of(this.mealEntity));

        MealDtOut resultTest = this.iMealService.getMealByUUID(uuidMealToFind);

        MealDtOut shouldResult = new MealDtOut(this.mealEntity, urlMealImage);

        Assertions.assertEquals(shouldResult.getUuid(), resultTest.getUuid());
        Assertions.assertEquals(shouldResult.getCategory(), resultTest.getCategory());
        Assertions.assertEquals(shouldResult.getPrice(), resultTest.getPrice());

        Mockito.verify(this.iMealDao, times(1)).findByUuid(uuidMealToFind);
    }

    @Test
    @DisplayName("Test  getMealWithUuid with invalid ID  with MAX_VALUE Because this ID Can't be recorded in the database ")
    void getMealByUuidWithNotFoundUUID() {
        String uuidMeal = java.util.UUID.randomUUID().toString();

        Mockito.when(this.iMealDao.findByUuid(uuidMeal)).thenReturn(Optional.empty());


        Assertions.assertThrows(FoodNotFoundException.class, () -> {
            this.iMealService.getMealByUUID(uuidMeal);
        });

        Mockito.verify(this.iMealDao, times(1)).findByUuid(uuidMeal);
    }


    @Test
    @DisplayName("Test  getMealWithUuid with Short ID ")
    void getMealByUuidWithShortUuid() {
        String uuidMeal = "a".repeat(19);
        Assertions.assertThrows(InvalidFoodInformationException.class, () -> {
            this.iMealService.getMealByUUID(uuidMeal);
        });
        Mockito.verify(this.iMealDao, times(0)).findByUuid(Mockito.anyString());
    }

    @Test
    @DisplayName("Test  getMealWithUuid with Empty ID ")
    void getMealByUuidWithEmptyUuid() {
        String uuidMeal = "    ";
        Assertions.assertThrows(InvalidFoodInformationException.class, () -> {
            this.iMealService.getMealByUUID(uuidMeal);
        });
        Mockito.verify(this.iMealDao, times(0)).findByUuid(Mockito.anyString());
    }


    @Test
    @DisplayName("Test  getMealWithUuid with null UUID  ")
    void getMealByUuidWithNullID() {
        Assertions.assertThrows(InvalidFoodInformationException.class, () -> {
            this.iMealService.getMealByUUID(null);
        });
        Mockito.verify(this.iMealDao, times(0)).findByUuid(Mockito.anyString());
    }


}