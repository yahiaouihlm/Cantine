package fr.sqli.Cantine.service.admin.meals;


import fr.sqli.Cantine.dao.IMealDao;
import fr.sqli.Cantine.dto.in.MealDtoIn;
import fr.sqli.Cantine.entity.ImageEntity;
import fr.sqli.Cantine.entity.MealEntity;

import fr.sqli.Cantine.entity.MenuEntity;
import fr.sqli.Cantine.service.admin.meals.exceptions.ExistingMeal;
import fr.sqli.Cantine.service.admin.meals.exceptions.InvalidMealInformationException;
import fr.sqli.Cantine.service.admin.meals.exceptions.MealNotFoundAdminException;
import fr.sqli.Cantine.service.admin.meals.exceptions.RemoveMealAdminException;
import fr.sqli.Cantine.service.admin.menus.exceptions.InvalidMenuInformationException;
import fr.sqli.Cantine.service.images.IImageService;
import fr.sqli.Cantine.service.images.exception.ImagePathException;
import fr.sqli.Cantine.service.images.exception.InvalidImageException;
import fr.sqli.Cantine.service.images.exception.InvalidTypeImageException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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

    }

    @AfterEach
    public void tearDown() {
        mealEntity = null;
        mealDtoIn = null;
    }

       /**************************** Remove Meal Test ****************************/

    @Test
    @DisplayName("Test  removeMeal method with  valid iformation")
    void removeMealWithValidateInformationtest () throws ImagePathException, MealNotFoundAdminException, InvalidMealInformationException, RemoveMealAdminException {

        this.mealEntity.setMenus(List.of()); //  empty list of  menu
        Mockito.when(mealDao.findById(1)).thenReturn(Optional.of(mealEntity));
        Mockito.doNothing().when(this.imageService).deleteImage(null, "images/meals");
        var result = mealService.removeMeal(1);
        // tests
        Assertions.assertNotNull(result);
        Assertions.assertEquals(this.mealEntity.getLabel(), result.getLabel());
    }
    @Test
    @DisplayName("Test  removeMeal method with positive id and meal not in  association with menu ")
    void removeMealTestWithMealInAssociationWithMenutest() {
        //add A menu ro  meal
        this.mealEntity.setMenus(List.of(new MenuEntity(), new MenuEntity()));
        Mockito.when(mealDao.findById(1)).thenReturn(java.util.Optional.ofNullable(mealEntity));
        Assertions.assertThrows(RemoveMealAdminException.class,
                () -> mealService.removeMeal(1));
        Mockito.verify(mealDao, Mockito.times(1)).findById(1);  // the meal is found the method findById is called once
        Mockito.verify(mealDao, Mockito.times(0)).delete(this.mealEntity); // the methode  deleted is  not  called  because the meal  is in association with menu
    }

    @Test
    @DisplayName("Test the removeMeal method with positive id")
    void removeMealTestWithNegativeIDtest() throws InvalidMealInformationException {
        Assertions.assertThrows(InvalidMealInformationException.class,
                () -> mealService.removeMeal(-1));
        Mockito.verify(mealDao, Mockito.times(0)).findById(-1);
        Mockito.verify(mealDao, Mockito.times(0)).delete(this.mealEntity);
    }

    @Test
    @DisplayName("Test the removeMeal method with NULL id")
    void removeMealTestWithNullIdtest() {
        Assertions.assertThrows(InvalidMealInformationException.class,
                () -> mealService.removeMeal(null));
        Mockito.verify(mealDao, Mockito.times(0)).findById(-1);
        Mockito.verify(mealDao, Mockito.times(0)).delete(this.mealEntity);
    }


    /**************************** Add Meal Test ****************************/

   @Test
    @DisplayName("Test the addMeal method with valid meal information and valid image")
    public void AddMealWihValidInformationtest() throws InvalidTypeImageException, InvalidImageException, ImagePathException, IOException, InvalidMealInformationException, ExistingMeal, InvalidMenuInformationException {

        this.mealDtoIn = new MealDtoIn();
        this.mealDtoIn.setLabel("Meal 1");
        this.mealDtoIn.setCategory("frites");
        this.mealDtoIn.setDescription("first Meal To  Test");
        this.mealDtoIn.setPrice(new BigDecimal("1.3"));
        this.mealDtoIn.setImage(Mockito.mock(MultipartFile.class));
        this.mealDtoIn.setQuantity(1);
        this.mealDtoIn.setStatus(1);

        //  spaces  from  the  label  is   removed
        Mockito.when(this.mealDao.findByLabelAndAndCategoryAndDescriptionIgnoreCase ("Meal1",  mealDtoIn.getCategory(), mealDtoIn.getDescription())).thenReturn( Optional.empty ( ) );

        String imageName = "test-image.jpg";
        Mockito.when(imageService.uploadImage(mealDtoIn.getImage(), "images/meals")).thenReturn(imageName);

        // Mock database save

        Mockito.when(this.mealDao.save(Mockito.any(MealEntity.class))).thenReturn(mealEntity);

        System.out.println("mealDtoIn in test  :  " + this.mealDtoIn.getLabel());
        // Call the method
        MealEntity result = this.mealService.addMeal(this.mealDtoIn);

        // Verify the result
        Assertions.assertNotNull(result);
        Assertions.assertEquals(mealDtoIn.getLabel(), result.getLabel());
        Assertions.assertEquals(mealDtoIn.getDescription(), result.getDescription());
        Assertions.assertEquals(mealDtoIn.getPrice(), result.getPrice());
        Assertions.assertNotNull(result.getImage());

        Mockito.verify(mealDao, Mockito.times(1)).findByLabelAndAndCategoryAndDescriptionIgnoreCase ("Meal1",  mealDtoIn.getCategory(), mealDtoIn.getDescription());
        Mockito.verify(mealDao, Mockito.times(1)).save(Mockito.any(MealEntity.class));
        Mockito.verify(imageService, Mockito.times(1)).uploadImage(mealDtoIn.getImage(), "images/meals");
    }

    @Test
    @DisplayName("Test the addMeal method with Existing meal")
    void AddMealWithExistingMealtest() throws InvalidTypeImageException, InvalidImageException, ImagePathException, IOException {
        this.mealDtoIn = new MealDtoIn();
        this.mealDtoIn.setLabel("Meal 1");
        this.mealDtoIn.setCategory("Frites");
        this.mealDtoIn.setDescription("first Meal To  Test");
        this.mealDtoIn.setPrice(BigDecimal.valueOf(1.3));
        this.mealDtoIn.setImage(Mockito.mock(MultipartFile.class));
        this.mealDtoIn.setQuantity(1);
        this.mealDtoIn.setStatus(1);

        //  the spaces are  removed  from  the  label
         Mockito.when(this.mealDao.findByLabelAndAndCategoryAndDescriptionIgnoreCase ("Meal1",  this.mealDtoIn.getCategory(), this.mealDtoIn.getDescription())).thenReturn(Optional.of(this.mealEntity));
        Assertions.assertThrows(ExistingMeal.class,
                () -> mealService.addMeal(mealDtoIn));
       Mockito.verify(this.imageService, Mockito.times(0)).updateImage(Mockito.any(), Mockito.any(), Mockito.any());
       Mockito.verify(this.mealDao, Mockito.times(1)).findByLabelAndAndCategoryAndDescriptionIgnoreCase ("Meal1",  this.mealDtoIn.getCategory(), this.mealDtoIn.getDescription());
       Mockito.verify(this.mealDao, Mockito.times(0)).save(this.mealEntity);
    }
    @Test
    @DisplayName("Test the addMeal method with negative price")
    void AddMealWithNegativePricetest() throws InvalidTypeImageException, InvalidImageException, ImagePathException {
        this.mealDtoIn = new MealDtoIn();
        this.mealDtoIn.setLabel("Meal 1");
        this.mealDtoIn.setCategory("Frites");
        this.mealDtoIn.setDescription("first Meal To  Test");
        this.mealDtoIn.setPrice(BigDecimal.valueOf(-1.3));
        this.mealDtoIn.setImage(Mockito.mock(MultipartFile.class));
        this.mealDtoIn.setQuantity(1);
        this.mealDtoIn.setStatus(1);
        Assertions.assertThrows(InvalidMealInformationException.class,
                () -> mealService.addMeal(mealDtoIn));

        Mockito.verify(this.mealDao, Mockito.times(0)).save(this.mealEntity);
    }

    @Test
    @DisplayName("Test the addMeal method with too lang label")
    void AddMealWithTooLangLabeltest() throws InvalidTypeImageException, InvalidImageException, ImagePathException {
        String tooLangString = "Lorem ipsum dolor sit adccdcmet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean m";

        this.mealDtoIn = new MealDtoIn();
        this.mealDtoIn.setLabel(tooLangString);
        this.mealDtoIn.setCategory("Frites");
        this.mealDtoIn.setDescription("first Meal To  Test");
        this.mealDtoIn.setPrice(new BigDecimal("1.3"));
        this.mealDtoIn.setImage(Mockito.mock(MultipartFile.class));
        this.mealDtoIn.setQuantity(1);
        this.mealDtoIn.setStatus(1);
        Assertions.assertThrows(InvalidMealInformationException.class,
                () -> mealService.addMeal(mealDtoIn));
    }

    @Test
    @DisplayName("Test the addMeal method with null image")
    void AddMealWithNullImagetest() throws InvalidTypeImageException, InvalidImageException, ImagePathException, IOException, InvalidMealInformationException {
        this.mealDtoIn = new MealDtoIn();
        this.mealDtoIn.setLabel("Meal 1");
        this.mealDtoIn.setCategory("Frites");
        this.mealDtoIn.setDescription("first Meal To  Test");
        this.mealDtoIn.setPrice(new BigDecimal("1.3"));
        this.mealDtoIn.setQuantity(1);
        this.mealDtoIn.setStatus(1);
        this.mealDtoIn.setImage(null);
        Assertions.assertThrows(InvalidMealInformationException.class,
                () -> mealService.addMeal(mealDtoIn));

        Mockito.verify(mealDao, Mockito.times(0)).save(Mockito.any(MealEntity.class));
        Mockito.verify(imageService, Mockito.times(0)).uploadImage(mealDtoIn.getImage(), "images/meals");
    }


    @Test
    @DisplayName("Test the addMeal method with null meal information")
    void AddMealWithNullMealInformationtest() throws InvalidTypeImageException, InvalidImageException, ImagePathException, IOException, InvalidMealInformationException {
        this.mealDtoIn = new MealDtoIn();
        Assertions.assertThrows(InvalidMealInformationException.class,
                () -> mealService.addMeal(mealDtoIn));

        Mockito.verify(mealDao, Mockito.times(0)).save(Mockito.any(MealEntity.class));
        Mockito.verify(imageService, Mockito.times(0)).uploadImage(mealDtoIn.getImage(), "images/meals");
    }





}