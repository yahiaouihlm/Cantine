package fr.sqli.cantine.service.food.meals;


import fr.sqli.cantine.dao.IMealDao;
import fr.sqli.cantine.dao.IMenuDao;
import fr.sqli.cantine.dto.in.food.MealDtoIn;
import fr.sqli.cantine.entity.*;
import fr.sqli.cantine.service.food.exceptions.FoodNotFoundException;
import fr.sqli.cantine.service.food.exceptions.InvalidFoodInformationException;
import fr.sqli.cantine.service.food.exceptions.RemoveFoodException;
import fr.sqli.cantine.service.food.impl.MealService;
import fr.sqli.cantine.service.images.IImageService;
import fr.sqli.cantine.service.images.exception.ImagePathException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


@ExtendWith(MockitoExtension.class)
class RemoveMealTest {

    MealEntity mealEntity;
    MealDtoIn mealDtoIn;
    @Mock
    private IMealDao mealDao;
    @Mock
    private IImageService imageService;
    @InjectMocks
    private MealService mealService;

    @Mock
    private IMenuDao menuDao;

    @Mock
    private MockEnvironment env;

    @BeforeEach
    public void setUp() {
        env = new MockEnvironment();
        env.setProperty("sqli.cantine.images.url.meals", "http://localhost:8080/cantine/download/images/meals/");
        env.setProperty("sqli.cantine.images.meals.path", "images/meals");
        mealService = new MealService(env, mealDao, imageService , menuDao);

        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setName("image-test");
        MealTypeEnum mealTypeEnum = MealTypeEnum.getMealTypeEnum("ENTREE");
        this.mealEntity = new MealEntity("Meal 1", "Frites", "first Meal To  Test", BigDecimal.valueOf(1.3), 1, 1,mealTypeEnum, imageEntity);
        this.mealEntity.setId(java.util.UUID.randomUUID().toString());


        this.mealDtoIn = new MealDtoIn();
        this.mealDtoIn.setLabel("Meal 1");
        this.mealDtoIn.setCategory("Frites");
        this.mealDtoIn.setDescription("first Meal To  Test");
        this.mealDtoIn.setPrice(new BigDecimal("1.3"));
        this.mealDtoIn.setQuantity(1);
        this.mealDtoIn.setStatus(1);
        this.mealDtoIn.setImage(Mockito.mock(MultipartFile.class));

    }

    @AfterEach
    public void tearDown() {
        mealEntity = null;
        mealDtoIn = null;

    }

    /**************************** Remove Meal Test ****************************/

    @Test
    @DisplayName("Test  removeMeal method with  valid iformation")
    void removeMealWithValidateInformationTest() throws ImagePathException, RemoveFoodException, InvalidFoodInformationException, FoodNotFoundException {

        this.mealEntity.setMenus(List.of()); //  empty list of  menu

        Mockito.when(mealDao.findMealById(this.mealEntity.getId())).thenReturn(Optional.of(mealEntity));

        Mockito.doNothing().when(this.imageService).deleteImage(this.mealEntity.getImage().getName(), this.env.getProperty("sqli.cantine.images.meals.path"));
        var result = mealService.deleteMeal(this.mealEntity.getId());

        // tests
        Assertions.assertNotNull(result);
        Assertions.assertEquals(this.mealEntity.getLabel(), result.getLabel());

        Mockito.verify(mealDao, Mockito.times(1)).findMealById(this.mealEntity.getId());
        Mockito.verify(mealDao, Mockito.times(1)).delete(this.mealEntity);
        Mockito.verify(imageService, Mockito.times(1)).deleteImage(this.mealEntity.getImage().getName(), "images/meals");

    }

    @Test
    @DisplayName("Test  removeMeal method with positive id and meal not in  association with Order ")
    void removeMealTestWithMealInAssociationWithOneOrderTest() throws ImagePathException {

        this.mealEntity.setOrders(List.of(new OrderEntity())); //  add a order to  meal

        Mockito.when(mealDao.findMealById(this.mealEntity.getId())).thenReturn(Optional.of(this.mealEntity)); // the meal is found

        Assertions.assertThrows(RemoveFoodException.class,
                () -> mealService.deleteMeal(this.mealEntity.getId())); // the meal is in association with menu

        Mockito.verify(mealDao, Mockito.times(1)).findMealById(this.mealEntity.getId());
        Mockito.verify(mealDao, Mockito.times(0)).delete(this.mealEntity);
        Mockito.verify(imageService, Mockito.times(0)).deleteImage(Mockito.any(String.class), Mockito.any(String.class));
    }

    @Test
    @DisplayName("Test  removeMeal method with positive id and meal not in  association with menu ")
    void removeMealTestWithMealInAssociationWithOneMenuTest() throws ImagePathException {
        this.mealEntity.setMenus(List.of(new MenuEntity())); //  add a menu to  meal

        Mockito.when(mealDao.findMealById(this.mealEntity.getId())).thenReturn(Optional.of(this.mealEntity)); // the meal is found

        Assertions.assertThrows(RemoveFoodException.class,
                () -> mealService.deleteMeal(this.mealEntity.getId())); // the meal is in association with menu

        Mockito.verify(mealDao, Mockito.times(1)).findMealById(this.mealEntity.getId());
        Mockito.verify(mealDao, Mockito.times(0)).delete(this.mealEntity);
        Mockito.verify(imageService, Mockito.times(0)).deleteImage(Mockito.any(String.class), Mockito.any(String.class));
    }


    @Test
    @DisplayName("Test  getMealWithUuid with Short ID ")
    void getMealByUuidWithShortUuid() {
        String uuidMeal = "a".repeat(19);
        Assertions.assertThrows(InvalidFoodInformationException.class, () -> {
            this.mealService.getMealByUUID(uuidMeal);
        });
        Mockito.verify(mealDao, Mockito.times(0)).findMealById(java.util.UUID.randomUUID().toString());
        Mockito.verify(mealDao, Mockito.times(0)).delete(Mockito.any(MealEntity.class));
    }

    @Test
    @DisplayName("Test  getMealWithUuid with Empty ID ")
    void getMealByUuidWithEmptyUuid() {
        String uuidMeal = "    ";
        Assertions.assertThrows(InvalidFoodInformationException.class, () -> {
            this.mealService.getMealByUUID(uuidMeal);
        });
        Mockito.verify(mealDao, Mockito.times(0)).findMealById(java.util.UUID.randomUUID().toString());
        Mockito.verify(mealDao, Mockito.times(0)).delete(Mockito.any(MealEntity.class));
    }


    @Test
    @DisplayName("Test the removeMeal method with NULL uuid")
    void removeMealTestWithNullUuidTest() throws ImagePathException {
        Assertions.assertThrows(InvalidFoodInformationException.class,
                () -> mealService.deleteMeal(null));
        Mockito.verify(mealDao, Mockito.times(0)).findMealById(java.util.UUID.randomUUID().toString());
        Mockito.verify(mealDao, Mockito.times(0)).delete(Mockito.any(MealEntity.class));
    }


}


