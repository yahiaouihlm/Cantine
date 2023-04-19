package fr.sqli.Cantine.service.admin.meals;


import fr.sqli.Cantine.dao.IMealDao;
import fr.sqli.Cantine.dto.in.MealDtoIn;
import fr.sqli.Cantine.entity.ImageEntity;
import fr.sqli.Cantine.entity.MealEntity;
import fr.sqli.Cantine.service.admin.meals.exceptions.ExistingMeal;
import fr.sqli.Cantine.service.admin.meals.exceptions.InvalidMealInformationException;
import fr.sqli.Cantine.service.admin.meals.exceptions.MealNotFoundAdminException;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class UpdateMealTest {
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

        this.mealEntity.setImage(new ImageEntity());
        this.mealEntity.setId(1);
        this.mealEntity.setPrice(new BigDecimal("1.3"));
        this.mealEntity.setQuantity(1);
        this.mealEntity.setStatus(1);
        this.mealEntity.setCategory("Frites");
        this.mealEntity.setDescription("first Meal To  Test");
        this.mealEntity.setLabel("Meal 1");

        this.mealDtoIn = new MealDtoIn();
        this.mealDtoIn.setLabel("Meal 1");
        this.mealDtoIn.setCategory("Frites");
        this.mealDtoIn.setDescription("first Meal To  Test");
        this.mealDtoIn.setPrice(new BigDecimal("1.3"));
        this.mealDtoIn.setImage(Mockito.mock(MultipartFile.class));
        this.mealDtoIn.setQuantity(1);
        this.mealDtoIn.setStatus(1);
    }

    @AfterEach
    public void tearDown() {
        this.mealEntity = null;
        this.mealDtoIn = null;
        this.env =  null ;

    }

   @Test
   @DisplayName("Update Meal With Valid ID And Meal Found")
   void  updateMealTestWithRightMeal() throws InvalidTypeImageException, InvalidImageException, ImagePathException, IOException, MealNotFoundAdminException, InvalidMealInformationException, ExistingMeal, InvalidMenuInformationException {
       this.mealDtoIn.setLabel("Meal 1 Updated");
       this.mealDtoIn.setImage(null );
       Mockito.when(mealDao.findById(1)).thenReturn(Optional.of(mealEntity));
       Mockito.when(mealDao.save(mealEntity)).thenReturn(mealEntity);


       var result = mealService.updateMeal(mealDtoIn, 1);

       // the spaces in label are removed in  MealDtoIn  and saved in database with spaces
         Assertions.assertEquals("Meal1Updated", result.getLabel());
         Assertions.assertEquals("Frites", result.getCategory());
         Mockito.verify(mealDao, Mockito.times(1)).findById(1);
         Mockito.verify(mealDao, Mockito.times(1)).save(mealEntity);

    }


    @Test
    @DisplayName("Update Meal With Valid ID But Meal Not Found")
    void  updateMealTestWithIdMealNotFound() throws InvalidTypeImageException, InvalidImageException, ImagePathException, IOException {
        var mealIDNotFound = 2;
        Mockito.when(mealDao.findById(mealIDNotFound)).thenReturn(Optional.ofNullable(null));

        Assertions.assertThrows(MealNotFoundAdminException.class, () -> {
            mealService.updateMeal(mealDtoIn, mealIDNotFound);
        });

        Mockito.verify(mealDao, Mockito.times(1)).findById(mealIDNotFound);
        Mockito.verify(mealDao, Mockito.times(0)).save(mealEntity);
        Mockito.verify(imageService, Mockito.times(0)).
                updateImage( Mockito.anyString(),Mockito.any(MultipartFile.class), Mockito.anyString());
    }

    @Test
    @DisplayName("Update Meal With TOO LONG Label")
    void updateMealTestWithTooLongLable() {
        String tooLangString = "Lorem ipsum dolor sit adccdcmet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean m";
        this.mealDtoIn.setLabel(tooLangString);
        Assertions.assertThrows(InvalidMealInformationException.class, () -> {
            mealService.updateMeal(mealDtoIn, 1);
        });
    }

    @Test
    @DisplayName("Update Meal With NULL Label")
    void updateMealTestWithNullPrice() {
        this.mealDtoIn.setPrice(null);
        Assertions.assertThrows(InvalidMealInformationException.class, () -> {
            mealService.updateMeal(mealDtoIn, 1);
        });
    }

    @Test
    @DisplayName("Update Meal With NULL Label")
    void updateMealTestWithNullLable() {
        this.mealDtoIn.setLabel(null);
        Assertions.assertThrows(InvalidMealInformationException.class, () -> {
            mealService.updateMeal(mealDtoIn, 1);
        });
    }

    @Test
    @DisplayName("Update Meal With Valid ID")
    void updateMealTestWithNegativeID() {
        Assertions.assertThrows(InvalidMealInformationException.class, () -> {
            mealService.updateMeal(mealDtoIn, -1);
        });
    }

    @Test
    @DisplayName("Update Meal With Null ID")
    void updateMealTestWithNullID() {
        Assertions.assertThrows(InvalidMealInformationException.class, () -> {
            mealService.updateMeal(mealDtoIn, null);
        });
    }
}
