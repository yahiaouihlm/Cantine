package fr.sqli.cantine.service.food.meals;

import fr.sqli.cantine.dao.IMealDao;
import fr.sqli.cantine.dto.in.food.MealDtoIn;
import fr.sqli.cantine.entity.ImageEntity;
import fr.sqli.cantine.entity.MealEntity;
import fr.sqli.cantine.service.food.exceptions.InvalidFoodInformationException;
import fr.sqli.cantine.service.images.IImageService;
import fr.sqli.cantine.service.images.exception.ImagePathException;
import fr.sqli.cantine.service.images.exception.InvalidFormatImageException;
import fr.sqli.cantine.service.images.exception.InvalidImageException;
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

@ExtendWith(MockitoExtension.class)
public class AddMealTest {

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
        String categoryMeal = " Frites ";
        String descriptionMeal = "first Meal To  Test  ";
        String mealLabel = "Meal 1";

        env = new MockEnvironment();
        env.setProperty("sqli.cantine.images.url.meals", "http://localhost:8080/cantine/download/images/meals/");
        env.setProperty("sqli.cantine.images.meals.path", "images/meals");
        mealService = new MealService(env, mealDao, imageService);
        ImageEntity imageEntity =  new ImageEntity();
        imageEntity.setImagename("image-test");
        this.mealEntity = new MealEntity(mealLabel,categoryMeal, descriptionMeal, BigDecimal.valueOf(1.3), 1,  1, imageEntity);
        this.mealEntity.setId(1);


        this.mealDtoIn = new MealDtoIn();
        this.mealDtoIn.setLabel(mealLabel);
        this.mealDtoIn.setCategory(categoryMeal);
        this.mealDtoIn.setDescription(descriptionMeal);
        this.mealDtoIn.setPrice(new BigDecimal("1.3"));
        this.mealDtoIn.setQuantity(1);
        this.mealDtoIn.setStatus(1);
        this.mealDtoIn.setImage(Mockito.mock(MultipartFile.class));

    }





    @Test
    @DisplayName("Test the addMeal method with too lang label")
    void AddMealWithTooLangLabelTest() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {
        String tooLongString = "test".repeat(101);


        this.mealDtoIn.setLabel(tooLongString);

        Assertions.assertThrows(InvalidFoodInformationException.class,
                () -> mealService.addMeal(mealDtoIn));

        Mockito.verify(mealDao, Mockito.times(0)).save(Mockito.any(MealEntity.class));
        Mockito.verify(this.imageService, Mockito.times(0)).updateImage(Mockito.any(), Mockito.any(), Mockito.any());
    }


    @Test
    @DisplayName("Test the addMeal method with too short label")
    void AddMealWithTooShortLabelTest() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {
        String tooshortString = "t" +" ".repeat(10) + "b";


        this.mealDtoIn.setLabel(tooshortString);

        Assertions.assertThrows(InvalidFoodInformationException.class,
                () -> mealService.addMeal(mealDtoIn));

        Mockito.verify(mealDao, Mockito.times(0)).save(Mockito.any(MealEntity.class));
        Mockito.verify(this.imageService, Mockito.times(0)).updateImage(Mockito.any(), Mockito.any(), Mockito.any());
    }


    @Test
    @DisplayName("Test the addMeal method with null label")
    void AddMealWithNullLangLabelTest() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {

        this.mealDtoIn.setLabel(null);

        Assertions.assertThrows(InvalidFoodInformationException.class,
                () -> mealService.addMeal(mealDtoIn));

        Mockito.verify(mealDao, Mockito.times(0)).save(Mockito.any(MealEntity.class));
        Mockito.verify(this.imageService, Mockito.times(0)).updateImage(Mockito.any(), Mockito.any(), Mockito.any());
    }





    @Test
    @DisplayName("Test the addMeal method with null image")
    void AddMealWithNullImageNet() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {
        this.mealDtoIn.setImage(null);
        Assertions.assertThrows(InvalidFoodInformationException.class,
                () -> mealService.addMeal(mealDtoIn));

        Mockito.verify(mealDao, Mockito.times(0)).save(Mockito.any(MealEntity.class));
        Mockito.verify(imageService, Mockito.times(0)).uploadImage(mealDtoIn.getImage(), "images/meals");
    }


    @Test
    @DisplayName("Test the addMeal method with null meal information")
    void AddMealWithNullMealInformation() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {
        this.mealDtoIn = new MealDtoIn();

        Assertions.assertThrows(InvalidFoodInformationException.class,
                () -> mealService.addMeal(mealDtoIn));

        Mockito.verify(mealDao, Mockito.times(0)).save(Mockito.any(MealEntity.class));
        Mockito.verify(imageService, Mockito.times(0)).uploadImage(mealDtoIn.getImage(), "images/meals");
    }
    @Test
    @DisplayName("Test the addMeal method with null  MealDtoIn")
    void AddMealWithNullRequestData() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {

        Assertions.assertThrows(InvalidFoodInformationException.class,
                () -> mealService.addMeal(null));

        Mockito.verify(mealDao, Mockito.times(0)).save(Mockito.any(MealEntity.class));
        Mockito.verify(imageService, Mockito.times(0)).uploadImage(mealDtoIn.getImage(), "images/meals");
    }



}
