package fr.sqli.Cantine.service.admin.meals;


import fr.sqli.Cantine.dao.IMealDao;
import fr.sqli.Cantine.dto.in.food.MealDtoIn;
import fr.sqli.Cantine.entity.ImageEntity;
import fr.sqli.Cantine.entity.MealEntity;
import fr.sqli.Cantine.service.admin.meals.exceptions.ExistingMealException;
import fr.sqli.Cantine.service.admin.meals.exceptions.InvalidMealInformationException;
import fr.sqli.Cantine.service.admin.meals.exceptions.MealNotFoundException;
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
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;


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
        env.setProperty("sqli.cantine.images.meals.path", "images/meals");

        mealService = new MealService(env, mealDao, imageService);
        this.mealEntity = new MealEntity();
        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setImagename("oldImage");
        this.mealEntity.setImage(imageEntity);
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
        MockMultipartFile multipartFile = new MockMultipartFile("oldImage", "test.txt", "text/plain", "Spring Framework".getBytes());
        this.mealDtoIn.setImage(multipartFile);
        this.mealDtoIn.setQuantity(1);
        this.mealDtoIn.setStatus(1);
    }

    @AfterEach
    public void tearDown() {
        this.mealEntity = null;
        this.mealDtoIn = null;
        this.env = null;

    }


    @Test
    @DisplayName("Update Meal With Valid ID And Meal Found With Image")
    void updateMealTestWithRightMealAndWithImage() throws ExistingMealException, InvalidMenuInformationException, MealNotFoundException, InvalidMealInformationException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {
        //init
        this.mealDtoIn.setLabel("Meal 1 Updated");
        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setImagename("newImage");
        MockMultipartFile multipartFile = new MockMultipartFile("oldImage", "test.txt", "text/plain", "Spring Framework".getBytes());
        this.mealDtoIn.setImage(multipartFile);


        // when
        Mockito.when(imageService.updateImage("oldImage", this.mealDtoIn.getImage(), "images/meals")).thenReturn("newImage");
        Mockito.when(mealDao.findById(1)).thenReturn(Optional.of(mealEntity));
        Mockito.when(mealDao.findByLabelAndAndCategoryAndDescriptionIgnoreCase("Meal 1 Updated", mealEntity.getCategory(), mealEntity.getDescription())).thenReturn(Optional.of(mealEntity));
        Mockito.when(mealDao.save(mealEntity)).thenReturn(mealEntity);

        var result = mealService.updateMeal(mealDtoIn, 1);

        // the spaces in label are removed in  MealDtoIn  and saved in database with spaces
        Assertions.assertEquals("Meal 1 Updated", result.getLabel());
        Assertions.assertEquals("Frites", result.getCategory());
        Assertions.assertEquals("newImage", result.getImage().getImagename());
        Mockito.verify(mealDao, Mockito.times(1)).findByLabelAndAndCategoryAndDescriptionIgnoreCase("Meal 1 Updated", mealEntity.getCategory(), mealEntity.getDescription());
        Mockito.verify(mealDao, Mockito.times(1)).findById(1);
        Mockito.verify(mealDao, Mockito.times(1)).save(mealEntity);
        Mockito.verify(imageService, Mockito.times(1)).updateImage(Mockito.anyString(), Mockito.any(MultipartFile.class), Mockito.anyString());
    }

    @Test
    @DisplayName("Update Meal With Valid ID And Meal Found  WithOut Image")
    void updateMealTestWithRightMealAndWithOutImage() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, MealNotFoundException, InvalidMealInformationException, ExistingMealException, InvalidMenuInformationException {
        this.mealDtoIn.setLabel("Meal 1 Updated");
        this.mealDtoIn.setImage(null);
        Mockito.when(mealDao.findById(1)).thenReturn(Optional.of(mealEntity));
        Mockito.when(mealDao.findByLabelAndAndCategoryAndDescriptionIgnoreCase("Meal 1 Updated", mealEntity.getCategory(), mealEntity.getDescription())).thenReturn(Optional.of(mealEntity));

        Mockito.when(mealDao.save(mealEntity)).thenReturn(mealEntity);


        var result = mealService.updateMeal(mealDtoIn, 1);

        // the spaces in label are removed in  MealDtoIn  and saved in database with spaces
        Assertions.assertEquals("Meal 1 Updated", result.getLabel());
        Assertions.assertEquals("Frites", result.getCategory());

        Mockito.verify(mealDao, Mockito.times(1)).findByLabelAndAndCategoryAndDescriptionIgnoreCase("Meal 1 Updated", mealEntity.getCategory(), mealEntity.getDescription());
        Mockito.verify(mealDao, Mockito.times(1)).findById(1);
        Mockito.verify(mealDao, Mockito.times(1)).save(mealEntity);
        Mockito.verify(imageService, Mockito.times(0)).updateImage(Mockito.anyString(), Mockito.any(MultipartFile.class), Mockito.anyString());
    }


    @Test
    @DisplayName("Update Meal With Valid ID But Existing Meal after update")
    void updateMealTestWithExistingMealAfterUpdate() throws ExistingMealException, InvalidMenuInformationException, MealNotFoundException, InvalidMealInformationException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {
        var idMeal = 1;
        Mockito.when(mealDao.findById(idMeal)).thenReturn(Optional.of(mealEntity));
        // when  we  submit the  modification  of  the  meal, and we  check if  the  meal  already  exists  in  the  database we return  another  meal  with  the  another id
        Mockito.when(mealDao.findByLabelAndAndCategoryAndDescriptionIgnoreCase("Meal 1", mealEntity.getCategory(), mealEntity.getDescription())).thenReturn(Optional.of(new MealEntity() {{
            setId(2);
        }}));

        Assertions.assertThrows(ExistingMealException.class, () -> {
            this.mealService.updateMeal(mealDtoIn, idMeal);
        });
        Mockito.verify(mealDao, Mockito.times(1)).findByLabelAndAndCategoryAndDescriptionIgnoreCase("Meal 1", mealEntity.getCategory(), mealEntity.getDescription());
        Mockito.verify(mealDao, Mockito.times(1)).findById(idMeal);
        Mockito.verify(mealDao, Mockito.times(0)).save(mealEntity);
        Mockito.verify(imageService, Mockito.times(0)).updateImage(Mockito.anyString(), Mockito.any(MultipartFile.class), Mockito.anyString());
    }

    @Test
    @DisplayName("Update Meal With Valid ID But Meal Not Found")
    void updateMealTestWithIdMealNotFound() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {
        var mealIDNotFound = 2;
        Mockito.when(mealDao.findById(mealIDNotFound)).thenReturn(Optional.ofNullable(null));

        Assertions.assertThrows(MealNotFoundException.class, () -> {
            mealService.updateMeal(mealDtoIn, mealIDNotFound);
        });

        Mockito.verify(mealDao, Mockito.times(1)).findById(mealIDNotFound);
        Mockito.verify(mealDao, Mockito.times(0)).save(mealEntity);
        Mockito.verify(imageService, Mockito.times(0)).updateImage(Mockito.anyString(), Mockito.any(MultipartFile.class), Mockito.anyString());
    }

    @Test
    @DisplayName("Update Meal With TOO LONG Label")
    void updateMealTestWithTooLongLabel() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {
        String tooLangString = "t".repeat(101);
        this.mealDtoIn.setLabel(tooLangString);
        Assertions.assertThrows(InvalidMealInformationException.class, () -> {
            mealService.updateMeal(mealDtoIn, 1);
        });
        Mockito.verify(mealDao, Mockito.times(0)).findByLabelAndAndCategoryAndDescriptionIgnoreCase(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.verify(mealDao, Mockito.times(0)).save(mealEntity);
        Mockito.verify(imageService, Mockito.times(0)).updateImage(Mockito.anyString(), Mockito.any(MultipartFile.class), Mockito.anyString());
    }

    @Test
    @DisplayName("Update Meal With NULL Label")
    void updateMealTestWithNullPrice() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {
        this.mealDtoIn.setPrice(null);
        Assertions.assertThrows(InvalidMealInformationException.class, () -> {
            mealService.updateMeal(mealDtoIn, 1);
        });
        Mockito.verify(mealDao, Mockito.times(0)).findByLabelAndAndCategoryAndDescriptionIgnoreCase(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.verify(mealDao, Mockito.times(0)).save(mealEntity);
        Mockito.verify(imageService, Mockito.times(0)).updateImage(Mockito.anyString(), Mockito.any(MultipartFile.class), Mockito.anyString());
    }

    @Test
    @DisplayName("Update Meal With NULL Label")
    void updateMealTestWithNullLabel() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {
        this.mealDtoIn.setLabel(null);
        Assertions.assertThrows(InvalidMealInformationException.class, () -> {
            mealService.updateMeal(mealDtoIn, 1);
        });

        Mockito.verify(mealDao, Mockito.times(0)).findByLabelAndAndCategoryAndDescriptionIgnoreCase(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.verify(mealDao, Mockito.times(0)).save(mealEntity);
        Mockito.verify(imageService, Mockito.times(0)).updateImage(Mockito.anyString(), Mockito.any(MultipartFile.class), Mockito.anyString());
    }

    @Test
    @DisplayName("Update Meal With Valid ID")
    void updateMealTestWithNegativeID() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {
        Assertions.assertThrows(InvalidMealInformationException.class, () -> {
            mealService.updateMeal(mealDtoIn, -1);
        });

        Mockito.verify(mealDao, Mockito.times(0)).findByLabelAndAndCategoryAndDescriptionIgnoreCase(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.verify(mealDao, Mockito.times(0)).save(mealEntity);
        Mockito.verify(imageService, Mockito.times(0)).updateImage(Mockito.anyString(), Mockito.any(MultipartFile.class), Mockito.anyString());
    }

    @Test
    @DisplayName("Update Meal With Null ID")
    void updateMealTestWithNullID() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {
        Assertions.assertThrows(InvalidMealInformationException.class, () -> {
            mealService.updateMeal(mealDtoIn, null);
        });
        Mockito.verify(mealDao, Mockito.times(0)).findByLabelAndAndCategoryAndDescriptionIgnoreCase(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.verify(mealDao, Mockito.times(0)).save(mealEntity);
        Mockito.verify(imageService, Mockito.times(0)).updateImage(Mockito.anyString(), Mockito.any(MultipartFile.class), Mockito.anyString());

    }

    @Test
    @DisplayName("Update Meal With Null MealDtoIn ")
    void updateMealTestWithNullMealDtoIn() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {
        Assertions.assertThrows(InvalidMealInformationException.class, () -> {
            mealService.updateMeal(mealDtoIn, null);
        });
        Mockito.verify(mealDao, Mockito.times(0)).findByLabelAndAndCategoryAndDescriptionIgnoreCase(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.verify(mealDao, Mockito.times(0)).save(Mockito.any());
        Mockito.verify(imageService, Mockito.times(0)).updateImage(Mockito.anyString(), Mockito.any(MultipartFile.class), Mockito.anyString());

    }
}
