package fr.sqli.Cantine.service.admin.meals;


import fr.sqli.Cantine.dao.IMealDao;
import fr.sqli.Cantine.dto.in.MealDtoIn;
import fr.sqli.Cantine.entity.ImageEntity;
import fr.sqli.Cantine.entity.MealEntity;
import fr.sqli.Cantine.service.admin.MealService;
import fr.sqli.Cantine.service.admin.exceptions.InvalidMealInformationAdminException;
import fr.sqli.Cantine.service.images.IImageService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

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
        this.mealEntity = new MealEntity(1, "Meal 1", "first Meal To  Test", "Frites", new BigDecimal("1.3"), 1, 1, new ImageEntity());
        this.mealDtoIn = new MealDtoIn();
        this.mealDtoIn.setLabel("Meal 1");
        this.mealDtoIn.setCategorie("Frites");
        this.mealDtoIn.setDescription("first Meal To  Test");
        this.mealDtoIn.setPrixht(new BigDecimal("1.3"));
        this.mealDtoIn.setImage(Mockito.mock(MultipartFile.class));
        this.mealDtoIn.setQuantite(1);
        this.mealDtoIn.setStatus(1);
    }


    @Test
    @DisplayName("Update Meal With TOO LONG Label")
    void updateMealTestWithTooLongLable() {
        String tooLangString = "Lorem ipsum dolor sit adccdcmet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean m";
        this.mealDtoIn.setLabel(tooLangString);
        Assertions.assertThrows(InvalidMealInformationAdminException.class, () -> {
            mealService.updateMeal(mealDtoIn, 1);
        });
    }

    @Test
    @DisplayName("Update Meal With NULL Label")
    void updateMealTestWithNullPrice() {
        this.mealDtoIn.setPrixht(null);
        Assertions.assertThrows(InvalidMealInformationAdminException.class, () -> {
            mealService.updateMeal(mealDtoIn, 1);
        });
    }

    @Test
    @DisplayName("Update Meal With NULL Label")
    void updateMealTestWithNullLable() {
        this.mealDtoIn.setLabel(null);
        Assertions.assertThrows(InvalidMealInformationAdminException.class, () -> {
            mealService.updateMeal(mealDtoIn, 1);
        });
    }

    @Test
    @DisplayName("Update Meal With Valid ID")
    void updateMealTestWithNegativeID() {
        Assertions.assertThrows(InvalidMealInformationAdminException.class, () -> {
            mealService.updateMeal(mealDtoIn, -1);
        });
    }

    @Test
    @DisplayName("Update Meal With Null ID")
    void updateMealTestWithNullID() {
        Assertions.assertThrows(InvalidMealInformationAdminException.class, () -> {
            mealService.updateMeal(mealDtoIn, null);
        });
    }
}
