package fr.sqli.Cantine.service.admin.meals;


import fr.sqli.Cantine.dao.IMealDao;
import fr.sqli.Cantine.dto.in.MealDtoIn;
import fr.sqli.Cantine.entity.ImageEntity;
import fr.sqli.Cantine.entity.MealEntity;
import fr.sqli.Cantine.entity.MenuEntity;
import fr.sqli.Cantine.service.admin.MealService;
import fr.sqli.Cantine.service.admin.exceptions.InvalidMealInformationAdminException;
import fr.sqli.Cantine.service.admin.exceptions.MealNotFoundAdminException;
import fr.sqli.Cantine.service.admin.exceptions.RemoveMealAdminException;
import fr.sqli.Cantine.service.images.IImageService;
import fr.sqli.Cantine.service.images.exception.ImagePathException;
import fr.sqli.Cantine.service.images.exception.InvalidImageException;
import fr.sqli.Cantine.service.images.exception.InvalidTypeImageException;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;


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
        this.mealEntity.setIdplat(1);
        this.mealEntity.setStatus(1);
        this.mealEntity.setPrice(BigDecimal.valueOf(1.3));
        this.mealEntity.setQuantity(1);
        this.mealEntity.setCategory("Frites");
        this.mealEntity.setDescription("first Meal To  Test");
        this.mealEntity.setLabel("Meal 1");
        this.mealEntity.setImage(new ImageEntity());

    }

    @Test
    @DisplayName("Test the addMeal method with valid meal")
    void removeMealTestWithValidMeal() throws MealNotFoundAdminException, RemoveMealAdminException, ImagePathException, InvalidMealInformationAdminException {
        this.mealEntity.setMenus(List.of()); // make  menu  with  empty list ==> meal  is not in association with menu
        Mockito.when(mealDao.findById(1)).thenReturn(java.util.Optional.ofNullable(mealEntity));
        Mockito.doNothing().when(this.imageService).deleteImage(null, "images/meals");
        var result = mealService.removeMeal(1);

        // tests
        Assertions.assertEquals("Meal 1", result.getLabel());
        Assertions.assertEquals("first Meal To  Test", result.getDescription());
        Assertions.assertEquals("Frites", result.getCategorie());
        Assertions.assertEquals(BigDecimal.valueOf(1.3), result.getPrixht());

        ///  verify  the  calls  to  the  methods
        Mockito.verify(mealDao, Mockito.times(1)).findById(1);  // the meal is found the method findById is called once
        Mockito.verify(this.imageService, Mockito.times(1)).deleteImage(null, "images/meals"); // the image is deleted
        Mockito.verify(mealDao, Mockito.times(1)).delete(this.mealEntity);
    }

    @Test
    @DisplayName("Test  removeMeal method with positive id and meal not in  association with menu ")
    void removeMealTestWithMealInAssociationWithMenu() {
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
    void removeMealTestWithNegativeID() throws InvalidMealInformationAdminException {
        Assertions.assertThrows(InvalidMealInformationAdminException.class,
                () -> mealService.removeMeal(-1));
        Mockito.verify(mealDao, Mockito.times(0)).findById(-1);
        Mockito.verify(mealDao, Mockito.times(0)).delete(this.mealEntity);
    }

    @Test
    @DisplayName("Test the removeMeal method with NULL id")
    void removeMealTestWithNullId() {
        Assertions.assertThrows(InvalidMealInformationAdminException.class,
                () -> mealService.removeMeal(null));
        Mockito.verify(mealDao, Mockito.times(0)).findById(-1);
        Mockito.verify(mealDao, Mockito.times(0)).delete(this.mealEntity);
    }


    /**************************** Add Meal Test ****************************/

    @Test
    @DisplayName("Test the addMeal method with negative price")
    void testAddMealWithNegativePrice() throws InvalidTypeImageException, InvalidImageException, ImagePathException {
        this.mealDtoIn = new MealDtoIn();
        this.mealDtoIn.setLabel("Meal 1");
        this.mealDtoIn.setCategorie("Frites");
        this.mealDtoIn.setDescription("first Meal To  Test");
        this.mealDtoIn.setPrice(BigDecimal.valueOf(-1.3));
        this.mealDtoIn.setImage(Mockito.mock(MultipartFile.class));
        this.mealDtoIn.setQuantity(1);
        this.mealDtoIn.setStatus(1);
        Assertions.assertThrows(InvalidMealInformationAdminException.class,
                () -> mealService.addMeal(mealDtoIn));


        Mockito.verify(this.mealDao, Mockito.times(0)).save(this.mealEntity);
    }

    @Test
    @DisplayName("Test the addMeal method with too lang label")
    void testAddMealWithTooLangLabel() throws InvalidTypeImageException, InvalidImageException, ImagePathException {
        String tooLangString = "Lorem ipsum dolor sit adccdcmet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean m";
        this.mealDtoIn = new MealDtoIn();
        this.mealDtoIn = new MealDtoIn();
        this.mealDtoIn.setLabel(tooLangString);
        this.mealDtoIn.setCategorie("Frites");
        this.mealDtoIn.setDescription("first Meal To  Test");
        this.mealDtoIn.setPrixht(new BigDecimal("1.3"));
        this.mealDtoIn.setImage(Mockito.mock(MultipartFile.class));
        this.mealDtoIn.setQuantite(1);
        this.mealDtoIn.setStatus(1);
        Assertions.assertThrows(InvalidMealInformationAdminException.class,
                () -> mealService.addMeal(mealDtoIn));
    }

    @Test
    @DisplayName("Test the addMeal method with null image")
    void testAddMealWithNullImage() throws InvalidTypeImageException, InvalidImageException, ImagePathException, IOException, InvalidMealInformationAdminException {
        this.mealDtoIn = new MealDtoIn();
        this.mealDtoIn.setLabel("Meal 1");
        this.mealDtoIn.setCategorie("Frites");
        this.mealDtoIn.setDescription("first Meal To  Test");
        this.mealDtoIn.setPrixht(new BigDecimal("1.3"));
        this.mealDtoIn.setQuantite(1);
        this.mealDtoIn.setStatus(1);
        this.mealDtoIn.setImage(null);
        Assertions.assertThrows(InvalidMealInformationAdminException.class,
                () -> mealService.addMeal(mealDtoIn));

        Mockito.verify(mealDao, Mockito.times(0)).save(Mockito.any(MealEntity.class));
        Mockito.verify(imageService, Mockito.times(0)).uploadImage(mealDtoIn.getImage(), "images/meals");
    }


    @Test
    @DisplayName("Test the addMeal method with null meal information")
    void testAddMealWithNullMealInformation() throws InvalidTypeImageException, InvalidImageException, ImagePathException, IOException, InvalidMealInformationAdminException {
        this.mealDtoIn = new MealDtoIn();
        Assertions.assertThrows(InvalidMealInformationAdminException.class,
                () -> mealService.addMeal(mealDtoIn));

        Mockito.verify(mealDao, Mockito.times(0)).save(Mockito.any(MealEntity.class));
        Mockito.verify(imageService, Mockito.times(0)).uploadImage(mealDtoIn.getImage(), "images/meals");
    }


    @Test
    @DisplayName("Test the addMeal method with valid meal information and valid image")
    public void testAddMeal() throws InvalidTypeImageException, InvalidImageException, ImagePathException, IOException, InvalidMealInformationAdminException {

        this.mealDtoIn = new MealDtoIn();
        this.mealDtoIn.setLabel("Meal 1");
        this.mealDtoIn.setCategorie("Frites");
        this.mealDtoIn.setDescription("first Meal To  Test");
        this.mealDtoIn.setPrixht(new BigDecimal("1.3"));
        this.mealDtoIn.setImage(Mockito.mock(MultipartFile.class));
        this.mealDtoIn.setQuantite(1);
        this.mealDtoIn.setStatus(1);


        String imageName = "test-image.jpg";
        Mockito.when(imageService.uploadImage(mealDtoIn.getImage(), "images/meals")).thenReturn(imageName);

        // Mock database save

        Mockito.when(this.mealDao.save(Mockito.any(MealEntity.class))).thenReturn(mealEntity);

        // Call the method
        MealEntity result = this.mealService.addMeal(mealDtoIn);

        // Verify the result
        Assertions.assertNotNull(result);
        Assertions.assertEquals(mealDtoIn.getLabel(), result.getLabel());
        Assertions.assertEquals(mealDtoIn.getDescription(), result.getDescription());
        Assertions.assertEquals(mealDtoIn.getPrixht(), result.getPrixht());
        Assertions.assertNotNull(result.getImage());

        Mockito.verify(mealDao, Mockito.times(1)).save(Mockito.any(MealEntity.class));
        Mockito.verify(imageService, Mockito.times(1)).uploadImage(mealDtoIn.getImage(), "images/meals");
    }


}