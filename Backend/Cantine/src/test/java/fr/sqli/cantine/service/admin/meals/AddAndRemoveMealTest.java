package fr.sqli.Cantine.service.admin.meals;


import fr.sqli.Cantine.dao.IMealDao;
import fr.sqli.Cantine.dto.in.food.MealDtoIn;
import fr.sqli.Cantine.entity.ImageEntity;
import fr.sqli.Cantine.entity.MealEntity;
import fr.sqli.Cantine.entity.MenuEntity;
import fr.sqli.Cantine.entity.OrderEntity;
import fr.sqli.Cantine.service.admin.meals.exceptions.ExistingMealException;
import fr.sqli.Cantine.service.admin.meals.exceptions.InvalidMealInformationException;
import fr.sqli.Cantine.service.admin.meals.exceptions.MealNotFoundException;
import fr.sqli.Cantine.service.admin.meals.exceptions.RemoveMealAdminException;
import fr.sqli.Cantine.service.admin.menus.exceptions.InvalidMenuInformationException;
import fr.sqli.Cantine.service.images.IImageService;
import fr.sqli.Cantine.service.images.exception.ImagePathException;
import fr.sqli.Cantine.service.images.exception.InvalidImageException;
import fr.sqli.Cantine.service.images.exception.InvalidFormatImageException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.matchers.Null;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


@ExtendWith(MockitoExtension.class)
class AddAndRemoveMealTest {

    MealEntity mealEntity;
    MealDtoIn mealDtoIn;
    @Mock
    private IMealDao mealDao;
    @Mock
    private IImageService imageService;
    @InjectMocks
    private MealService mealService;

    @Mock
    private MockEnvironment env;

    @BeforeEach
    public void setUp() {
        env = new MockEnvironment();
        env.setProperty("sqli.cantine.images.url.meals", "http://localhost:8080/cantine/download/images/meals/");
        env.setProperty("sqli.cantine.images.meals.path", "images/meals");
        mealService = new MealService(env, mealDao, imageService);
        this.mealEntity = new MealEntity();
        this.mealEntity.setId(1);
        this.mealEntity.setStatus(1);
        this.mealEntity.setPrice(BigDecimal.valueOf(1.3));
        this.mealEntity.setQuantity(1);
        this.mealEntity.setCategory("Frites");
        this.mealEntity.setDescription("first Meal To  Test");
        this.mealEntity.setLabel("Meal 1");
        this.mealEntity.setImage(new ImageEntity());


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
    void removeMealWithValidateInformationTest() throws ImagePathException, InvalidMealInformationException, RemoveMealAdminException, MealNotFoundException {

        this.mealEntity.setMenus(List.of()); //  empty list of  menu

        Mockito.when(mealDao.findById(1)).thenReturn(Optional.of(mealEntity));

        Mockito.doNothing().when(this.imageService).deleteImage(null, this.env.getProperty("sqli.cantine.images.meals.path"));
        var result = mealService.removeMeal(1);

        // tests
        Assertions.assertNotNull(result);
        Assertions.assertEquals(this.mealEntity.getLabel(), result.getLabel());

        Mockito.verify(mealDao, Mockito.times(1)).findById(1);
        Mockito.verify(mealDao, Mockito.times(1)).delete(this.mealEntity);
        Mockito.verify(imageService, Mockito.times(1)).deleteImage(null, "images/meals");

    }
    @Test
    @DisplayName("Test  removeMeal method with positive id and meal not in  association with Order ")
    void removeMealTestWithMealInAssociationWithOneOrderTest() throws  ImagePathException {

           this.mealEntity.setOrders(List.of(new OrderEntity())); //  add a order to  meal

        Mockito.when(mealDao.findById(1)).thenReturn(Optional.of(this.mealEntity)); // the meal is found

        Assertions.assertThrows(RemoveMealAdminException.class,
                () -> mealService.removeMeal(1)); // the meal is in association with menu

        Mockito.verify(mealDao, Mockito.times(1)).findById(1);
        Mockito.verify(mealDao, Mockito.times(0)).delete(this.mealEntity);
        Mockito.verify(imageService, Mockito.times(0)).deleteImage(Mockito.any(String.class), Mockito.any(String.class));
    }

    @Test
    @DisplayName("Test  removeMeal method with positive id and meal not in  association with menu ")
    void removeMealTestWithMealInAssociationWithOneMenuTest() throws InvalidMealInformationException, ImagePathException {
        this.mealEntity.setMenus(List.of(new MenuEntity())); //  add a menu to  meal

        Mockito.when(mealDao.findById(1)).thenReturn(Optional.of(this.mealEntity)); // the meal is found

        Assertions.assertThrows(RemoveMealAdminException.class,
                () -> mealService.removeMeal(1)); // the meal is in association with menu

        Mockito.verify(mealDao, Mockito.times(1)).findById(1);
        Mockito.verify(mealDao, Mockito.times(0)).delete(this.mealEntity);
        Mockito.verify(imageService, Mockito.times(0)).deleteImage(Mockito.any(String.class), Mockito.any(String.class));
    }

    @Test
    @DisplayName("Test the removeMeal method with positive id")
    void removeMealTestWithNegativeIDTest() throws InvalidMealInformationException, ImagePathException {
        Assertions.assertThrows(InvalidMealInformationException.class,
                () -> mealService.removeMeal(-1));
        Mockito.verify(mealDao, Mockito.times(0)).findById(-1);
        Mockito.verify(mealDao, Mockito.times(0)).delete(this.mealEntity);
        Mockito.verify(imageService, Mockito.times(0)).deleteImage(Mockito.any(String.class), Mockito.any(String.class));
    }

    @Test
    @DisplayName("Test the removeMeal method with NULL id")
    void removeMealTestWithNullIdTest() throws ImagePathException {
        Assertions.assertThrows(InvalidMealInformationException.class,
                () -> mealService.removeMeal(null));
        Mockito.verify(mealDao, Mockito.times(0)).findById(-1);
        Mockito.verify(mealDao, Mockito.times(0)).delete(this.mealEntity);
        Mockito.verify(imageService, Mockito.times(0)).deleteImage(Mockito.any(String.class), Mockito.any(String.class));
    }


    /**************************** Add Meal Test ****************************/


    @Test
    @DisplayName("Test the addMeal method with too  long category")
    public void AddMealWithTooLongCategoryTest() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, InvalidMealInformationException, ExistingMealException, InvalidMenuInformationException {
        this.mealDtoIn.setCategory("test".repeat(45));
        Assertions.assertThrows(InvalidMealInformationException.class,
                () -> mealService.addMeal(mealDtoIn));

        Mockito.verify(mealDao, Mockito.times(0)).save(this.mealEntity);
        Mockito.verify(imageService, Mockito.times(0)).uploadImage(mealDtoIn.getImage(), "images/meals");
        Mockito.verify(mealDao, Mockito.times(0)).findByLabelAndAndCategoryAndDescriptionIgnoreCase("Meal1", mealDtoIn.getCategory(), mealDtoIn.getDescription());
    }

    @Test
    @DisplayName("Test the addMeal method with valid meal information and valid image")
    public void AddMealWihValidInformationTest() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, InvalidMealInformationException, ExistingMealException, InvalidMenuInformationException {

        //  spaces  from  the  label  is   removed
        Mockito.when(this.mealDao.findByLabelAndAndCategoryAndDescriptionIgnoreCase("Meal 1", mealDtoIn.getCategory(), mealDtoIn.getDescription())).thenReturn(Optional.empty());

        String imageName = "test-image.jpg";
        Mockito.when(imageService.uploadImage(mealDtoIn.getImage(), "images/meals")).thenReturn(imageName);

        // Mock database save

        Mockito.when(this.mealDao.save(Mockito.any(MealEntity.class))).thenReturn(mealEntity);


        // Call the method
        MealEntity result = this.mealService.addMeal(this.mealDtoIn);

        // Verify the result
        Assertions.assertNotNull(result);
        Assertions.assertEquals(mealDtoIn.getLabel(), result.getLabel());
        Assertions.assertEquals(mealDtoIn.getDescription(), result.getDescription());
        Assertions.assertEquals(mealDtoIn.getPrice(), result.getPrice());
        Assertions.assertNotNull(result.getImage());

        Mockito.verify(mealDao, Mockito.times(1)).findByLabelAndAndCategoryAndDescriptionIgnoreCase("Meal 1", mealDtoIn.getCategory(), mealDtoIn.getDescription());
        Mockito.verify(mealDao, Mockito.times(1)).save(Mockito.any(MealEntity.class));
        Mockito.verify(imageService, Mockito.times(1)).uploadImage(mealDtoIn.getImage(), "images/meals");
    }

    @Test
    @DisplayName("Test the addMeal method with  too short label")
    void AddMealWithTooShortLabelTest() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, InvalidMealInformationException, ExistingMealException, InvalidMenuInformationException {
        this.mealDtoIn.setLabel("   M              e        "); //  spaces  from  the  label  is   removed
        Assertions.assertThrows(InvalidMealInformationException.class,
                () -> mealService.addMeal(mealDtoIn));
        Mockito.verify(this.mealDao, Mockito.times(0)).findByLabelAndAndCategoryAndDescriptionIgnoreCase("Me", this.mealDtoIn.getCategory(), this.mealDtoIn.getDescription());
        Mockito.verify(this.mealDao, Mockito.times(0)).save(Mockito.any(MealEntity.class));
        Mockito.verify(this.imageService, Mockito.times(0)).uploadImage(Mockito.any(), Mockito.any());
    }

    @Test
    @DisplayName("Test the addMeal method with status out of bound status = -1")
    public void AddMealWithStatusOutOfBoundTest1() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, InvalidMealInformationException, ExistingMealException, InvalidMenuInformationException {
        this.mealDtoIn.setStatus(-1);

        Assertions.assertThrows(InvalidMealInformationException.class,
                () -> mealService.addMeal(mealDtoIn));

        Mockito.verify(this.mealDao, Mockito.times(0)).findByLabelAndAndCategoryAndDescriptionIgnoreCase("Meal1", this.mealDtoIn.getCategory(), this.mealDtoIn.getDescription());
        Mockito.verify(this.mealDao, Mockito.times(0)).save(Mockito.any(MealEntity.class));
        Mockito.verify(this.imageService, Mockito.times(0)).uploadImage(Mockito.any(), Mockito.any());
    }

    @Test
    @DisplayName("Test the addMeal method with status out of bound status = 3")
    public void AddMealWithStatusOutOfBoundTest() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, InvalidMealInformationException, ExistingMealException, InvalidMenuInformationException {
        this.mealDtoIn.setStatus(3);

        Assertions.assertThrows(InvalidMealInformationException.class,
                () -> mealService.addMeal(mealDtoIn));

        Mockito.verify(this.mealDao, Mockito.times(0)).findByLabelAndAndCategoryAndDescriptionIgnoreCase("Meal1", this.mealDtoIn.getCategory(), this.mealDtoIn.getDescription());
        Mockito.verify(this.mealDao, Mockito.times(0)).save(Mockito.any(MealEntity.class));
        Mockito.verify(this.imageService, Mockito.times(0)).uploadImage(Mockito.any(), Mockito.any());
    }

    @Test
    @DisplayName("Test the addMeal method with Existing meal")
    void AddMealWithExistingMealTest() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {

        //  the spaces are  removed  from  the  label
        Mockito.when(this.mealDao.findByLabelAndAndCategoryAndDescriptionIgnoreCase("Meal 1", this.mealDtoIn.getCategory(), this.mealDtoIn.getDescription())).thenReturn(Optional.of(this.mealEntity));
        Assertions.assertThrows(ExistingMealException.class,
                () -> mealService.addMeal(mealDtoIn));

        Mockito.verify(this.imageService, Mockito.times(0)).updateImage(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.verify(this.mealDao, Mockito.times(1)).findByLabelAndAndCategoryAndDescriptionIgnoreCase("Meal 1", this.mealDtoIn.getCategory(), this.mealDtoIn.getDescription());
        Mockito.verify(this.mealDao, Mockito.times(0)).save(this.mealEntity);
    }

    @Test
    @DisplayName("Test the addMeal method with negative price")
    void AddMealWithNegativePriceTest() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {

        this.mealDtoIn.setPrice(BigDecimal.valueOf(-1.3));

        Assertions.assertThrows(InvalidMealInformationException.class,
                () -> mealService.addMeal(mealDtoIn));

        Mockito.verify(this.imageService, Mockito.times(0)).updateImage(Mockito.any(), Mockito.any(), Mockito.any());

        Mockito.verify(this.mealDao, Mockito.times(0)).save(this.mealEntity);
    }

    @Test
    @DisplayName("Test the addMeal method with too lang label")
    void AddMealWithTooLangLabelTest() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {
        String tooLangString = "test".repeat(101);


        this.mealDtoIn.setLabel(tooLangString);

        Assertions.assertThrows(InvalidMealInformationException.class,
                () -> mealService.addMeal(mealDtoIn));

        Mockito.verify(mealDao, Mockito.times(0)).save(Mockito.any(MealEntity.class));
        Mockito.verify(this.imageService, Mockito.times(0)).updateImage(Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    @DisplayName("Test the addMeal method with null image")
    void AddMealWithNullImageNet() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, InvalidMealInformationException {
        this.mealDtoIn.setImage(null);
        Assertions.assertThrows(InvalidMealInformationException.class,
                () -> mealService.addMeal(mealDtoIn));

        Mockito.verify(mealDao, Mockito.times(0)).save(Mockito.any(MealEntity.class));
        Mockito.verify(imageService, Mockito.times(0)).uploadImage(mealDtoIn.getImage(), "images/meals");
    }


    @Test
    @DisplayName("Test the addMeal method with null meal information")
    void AddMealWithNullMealInformation() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, InvalidMealInformationException {
        this.mealDtoIn = new MealDtoIn();
        Assertions.assertThrows(InvalidMealInformationException.class,
                () -> mealService.addMeal(mealDtoIn));

        Mockito.verify(mealDao, Mockito.times(0)).save(Mockito.any(MealEntity.class));
        Mockito.verify(imageService, Mockito.times(0)).uploadImage(mealDtoIn.getImage(), "images/meals");
    }


    @Test
    @DisplayName("Test the addMeal method with null  MealDtoIn")
    void AddMealWithNullRequestData() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, InvalidMealInformationException {

        Assertions.assertThrows(InvalidMealInformationException.class,
                () -> mealService.addMeal(null));

        Mockito.verify(mealDao, Mockito.times(0)).save(Mockito.any(MealEntity.class));
        Mockito.verify(imageService, Mockito.times(0)).uploadImage(mealDtoIn.getImage(), "images/meals");
    }


}