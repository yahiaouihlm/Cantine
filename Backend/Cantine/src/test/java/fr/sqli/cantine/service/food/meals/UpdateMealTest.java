package fr.sqli.cantine.service.food.meals;


import fr.sqli.cantine.dao.IMealDao;
import fr.sqli.cantine.dto.in.food.MealDtoIn;
import fr.sqli.cantine.entity.ImageEntity;
import fr.sqli.cantine.entity.MealEntity;
import fr.sqli.cantine.entity.MealTypeEnum;
import fr.sqli.cantine.service.food.exceptions.ExistingFoodException;
import fr.sqli.cantine.service.food.exceptions.FoodNotFoundException;
import fr.sqli.cantine.service.food.exceptions.InvalidFoodInformationException;
import fr.sqli.cantine.service.food.impl.MealService;
import fr.sqli.cantine.service.images.IImageService;
import fr.sqli.cantine.service.images.exception.ImagePathException;
import fr.sqli.cantine.service.images.exception.InvalidImageException;
import fr.sqli.cantine.service.images.exception.InvalidFormatImageException;
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
        String categoryMeal = " Frites ";
        String descriptionMeal = "first Meal To  Test  ";
        String mealLabel = "Meal 1";
        env = new MockEnvironment();
        env.setProperty("sqli.cantine.images.url.meals", "http://localhost:8080/cantine/download/images/meals/");
        env.setProperty("sqli.cantine.images.meals.path", "images/meals");

        mealService = new MealService(env, mealDao, imageService);
        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setImagename("oldImage");
        MealTypeEnum mealTypeEnum = MealTypeEnum.getMealTypeEnum("ENTREE");
        this.mealEntity = new MealEntity(mealLabel, categoryMeal, descriptionMeal, BigDecimal.valueOf(1.3), 1, 1,mealTypeEnum, imageEntity);


        this.mealEntity.setImage(imageEntity);
        this.mealEntity.setId(1);

        this.mealDtoIn = new MealDtoIn();
        this.mealDtoIn.setUuid(this.mealEntity.getUuid());
        this.mealDtoIn.setLabel(mealLabel);
        this.mealDtoIn.setCategory(categoryMeal);
        this.mealDtoIn.setDescription(descriptionMeal);
        this.mealDtoIn.setPrice(new BigDecimal("1.3"));
        MockMultipartFile multipartFile = new MockMultipartFile("oldImage", "test.txt", "text/plain", "Spring Framework".getBytes());
        this.mealDtoIn.setImage(multipartFile);
        this.mealDtoIn.setQuantity(1);
        this.mealDtoIn.setStatus(1);
        this.mealDtoIn.setMealType("ENTREE");
    }

    @AfterEach
    public void tearDown() {
        this.mealEntity = null;
        this.mealDtoIn = null;
        this.env = null;

    }


    @Test
    @DisplayName("Update Meal With Valid ID And Meal Found With Image")
    void updateMealTestWithRightMealAndWithImage() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, InvalidFoodInformationException, ExistingFoodException, FoodNotFoundException {
        //init
        this.mealDtoIn.setLabel("Meal 1 Updated");
        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setImagename("newImage");
        MockMultipartFile multipartFile = new MockMultipartFile("oldImage", "test.txt", "text/plain", "Spring Framework".getBytes());
        this.mealDtoIn.setImage(multipartFile);


        // when
        Mockito.when(imageService.updateImage("oldImage", this.mealDtoIn.getImage(), "images/meals")).thenReturn("newImage");
        Mockito.when(mealDao.findByUuid(this.mealEntity.getUuid())).thenReturn(Optional.of(mealEntity));
        Mockito.when(mealDao.findByLabelAndAndCategoryAndDescriptionIgnoreCase("Meal 1 Updated", mealEntity.getCategory(), mealEntity.getDescription())).thenReturn(Optional.of(mealEntity));
        Mockito.when(mealDao.save(mealEntity)).thenReturn(mealEntity);

        this.mealDtoIn.setUuid(this.mealEntity.getUuid());
        var result = mealService.updateMeal(mealDtoIn);

        // the spaces in label are removed in  MealDtoIn  and saved in database with spaces
        Assertions.assertEquals("Meal 1 Updated", result.getLabel());
        Assertions.assertEquals("Frites", result.getCategory());
        Assertions.assertEquals("newImage", result.getImage().getImagename());
        Mockito.verify(mealDao, Mockito.times(1)).findByLabelAndAndCategoryAndDescriptionIgnoreCase("Meal 1 Updated", mealEntity.getCategory(), mealEntity.getDescription());
        Mockito.verify(mealDao, Mockito.times(1)).findByUuid(this.mealEntity.getUuid());
        Mockito.verify(mealDao, Mockito.times(1)).save(mealEntity);
        Mockito.verify(imageService, Mockito.times(1)).updateImage(Mockito.anyString(), Mockito.any(MultipartFile.class), Mockito.anyString());
    }

    @Test
    @DisplayName("Update Meal With Valid ID And Meal Found  WithOut Image")
    void updateMealTestWithRightMealAndWithOutImage() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, InvalidFoodInformationException, ExistingFoodException, FoodNotFoundException {
        this.mealDtoIn.setLabel("Meal 1 Updated");
        this.mealDtoIn.setImage(null);
        Mockito.when(mealDao.findByUuid(this.mealDtoIn.getUuid())).thenReturn(Optional.of(mealEntity));
        Mockito.when(mealDao.findByLabelAndAndCategoryAndDescriptionIgnoreCase("Meal 1 Updated", mealEntity.getCategory(), mealEntity.getDescription())).thenReturn(Optional.of(mealEntity));

        Mockito.when(mealDao.save(mealEntity)).thenReturn(mealEntity);
        this.mealDtoIn.setUuid(this.mealDtoIn.getUuid());

        var result = mealService.updateMeal(mealDtoIn);

        // the spaces in label are removed in  MealDtoIn  and saved in database with spaces
        Assertions.assertEquals("Meal 1 Updated", result.getLabel());
        Assertions.assertEquals("Frites", result.getCategory());

        Mockito.verify(mealDao, Mockito.times(1)).findByLabelAndAndCategoryAndDescriptionIgnoreCase("Meal 1 Updated", mealEntity.getCategory(), mealEntity.getDescription());
        Mockito.verify(mealDao, Mockito.times(1)).findByUuid(this.mealDtoIn.getUuid());
        Mockito.verify(mealDao, Mockito.times(1)).save(mealEntity);
        Mockito.verify(imageService, Mockito.times(0)).updateImage(Mockito.anyString(), Mockito.any(MultipartFile.class), Mockito.anyString());
    }


    @Test
    @DisplayName("Update Meal With Valid ID But Existing Meal after update")
    void updateMealTestWithExistingMealAfterUpdate() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {

        Mockito.when(mealDao.findByUuid(this.mealEntity.getUuid())).thenReturn(Optional.of(mealEntity));
        // when  we  submit the  modification  of  the  meal, and we  check if  the  meal  already  exists  in  the  database we return  another  meal  with  the  another id
        Mockito.when(mealDao.findByLabelAndAndCategoryAndDescriptionIgnoreCase(this.mealEntity.getLabel(), mealEntity.getCategory(), mealEntity.getDescription())).thenReturn(Optional.of(new MealEntity() {{
            setUuid(java.util.UUID.randomUUID().toString());
        }}));

        this.mealDtoIn.setUuid(this.mealEntity.getUuid());
        Assertions.assertThrows(ExistingFoodException.class, () -> {
            this.mealService.updateMeal(mealDtoIn);
        });
        Mockito.verify(mealDao, Mockito.times(1)).findByLabelAndAndCategoryAndDescriptionIgnoreCase(this.mealEntity.getLabel(), mealEntity.getCategory(), mealEntity.getDescription());
        Mockito.verify(mealDao, Mockito.times(1)).findByUuid(this.mealEntity.getUuid());
        Mockito.verify(mealDao, Mockito.times(0)).save(mealEntity);
        Mockito.verify(imageService, Mockito.times(0)).updateImage(Mockito.anyString(), Mockito.any(MultipartFile.class), Mockito.anyString());
    }

    @Test
    @DisplayName("Update Meal With Valid ID But Meal Not Found")
    void updateMealTestWithIdMealNotFound() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {


        Mockito.when(mealDao.findByUuid(this.mealDtoIn.getUuid())).thenReturn(Optional.empty());


        Assertions.assertThrows(FoodNotFoundException.class, () -> {
            mealService.updateMeal(mealDtoIn);
        });

        Mockito.verify(mealDao, Mockito.times(1)).findByUuid(this.mealDtoIn.getUuid());
        Mockito.verify(mealDao, Mockito.times(0)).save(mealEntity);
        Mockito.verify(imageService, Mockito.times(0)).updateImage(Mockito.anyString(), Mockito.any(MultipartFile.class), Mockito.anyString());
    }


    //--------------------------------------------- TEST  MEALTYPE ---------------------------------------------//
    @Test
    public void updateMealWithInvalidMealType() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {
        this.mealDtoIn.setMealType("TEST");

        Assertions.assertThrows(InvalidFoodInformationException.class, () -> mealService.addMeal(mealDtoIn));


        Mockito.verify(this.mealDao, Mockito.times(0)).save(Mockito.any(MealEntity.class));
        Mockito.verify(this.imageService, Mockito.times(0)).uploadImage(Mockito.any(), Mockito.any());
    }
    @Test
    public void updateMealWithEmptyMealType() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {
        this.mealDtoIn.setMealType("");

        Assertions.assertThrows(InvalidFoodInformationException.class, () -> mealService.addMeal(mealDtoIn));


        Mockito.verify(this.mealDao, Mockito.times(0)).save(Mockito.any(MealEntity.class));
        Mockito.verify(this.imageService, Mockito.times(0)).uploadImage(Mockito.any(), Mockito.any());
    }
    @Test
    public void UpdateMealWithNullMealType() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {
        this.mealDtoIn.setMealType(null);

        Assertions.assertThrows(InvalidFoodInformationException.class, () -> mealService.addMeal(mealDtoIn));


        Mockito.verify(this.mealDao, Mockito.times(0)).save(Mockito.any(MealEntity.class));
        Mockito.verify(this.imageService, Mockito.times(0)).uploadImage(Mockito.any(), Mockito.any());
    }

    //--------------------------------------------- TEST  QUANTITY ---------------------------------------------//

    @Test
    @DisplayName("Update Meal with Too Big Quantity")
    void updateMealWithTooBigQuantity() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {
        this.mealDtoIn.setQuantity(10001);
        Assertions.assertThrows(InvalidFoodInformationException.class, () -> {
            mealService.updateMeal(mealDtoIn);
        });
        Mockito.verify(mealDao, Mockito.times(0)).findByLabelAndAndCategoryAndDescriptionIgnoreCase(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.verify(mealDao, Mockito.times(0)).save(mealEntity);
        Mockito.verify(imageService, Mockito.times(0)).updateImage(Mockito.anyString(), Mockito.any(MultipartFile.class), Mockito.anyString());
    }


    @Test
    @DisplayName("Update Meal with Negative Quantity")
    void updateMealTestWithNegativeQuantity() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {

        this.mealDtoIn.setQuantity(-2);

        Assertions.assertThrows(InvalidFoodInformationException.class, () -> {
            mealService.updateMeal(mealDtoIn);
        });
        Mockito.verify(mealDao, Mockito.times(0)).findByLabelAndAndCategoryAndDescriptionIgnoreCase(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.verify(mealDao, Mockito.times(0)).save(mealEntity);
        Mockito.verify(imageService, Mockito.times(0)).updateImage(Mockito.anyString(), Mockito.any(MultipartFile.class), Mockito.anyString());
    }


    @Test
    @DisplayName("Update Meal  With null  Quantity ")
    void updateMealTestWithNullQuantity() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {

        this.mealDtoIn.setQuantity(null);


        Assertions.assertThrows(InvalidFoodInformationException.class, () -> {
            mealService.updateMeal(mealDtoIn);
        });

        Mockito.verify(mealDao, Mockito.times(0)).findByLabelAndAndCategoryAndDescriptionIgnoreCase(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.verify(mealDao, Mockito.times(0)).save(mealEntity);
        Mockito.verify(imageService, Mockito.times(0)).updateImage(Mockito.anyString(), Mockito.any(MultipartFile.class), Mockito.anyString());
    }


    //--------------------------------------------- TEST  STATUS ---------------------------------------------//

    @Test
    @DisplayName("Update Meal with status out of bound status = -1")
    void updateMealWithStatusOutOfBoundTest1() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {
        this.mealDtoIn.setStatus(-1);
        Assertions.assertThrows(InvalidFoodInformationException.class, () -> {
            mealService.updateMeal(mealDtoIn);
        });
        Mockito.verify(mealDao, Mockito.times(0)).findByLabelAndAndCategoryAndDescriptionIgnoreCase(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.verify(mealDao, Mockito.times(0)).save(mealEntity);
        Mockito.verify(imageService, Mockito.times(0)).updateImage(Mockito.anyString(), Mockito.any(MultipartFile.class), Mockito.anyString());
    }


    @Test
    @DisplayName("Update Meal with status out of bound status = 3")
    void updateMealTestWithStatusOutOfBoundTest() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {

        this.mealDtoIn.setStatus(3);

        Assertions.assertThrows(InvalidFoodInformationException.class, () -> {
            mealService.updateMeal(mealDtoIn);
        });
        Mockito.verify(mealDao, Mockito.times(0)).findByLabelAndAndCategoryAndDescriptionIgnoreCase(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.verify(mealDao, Mockito.times(0)).save(mealEntity);
        Mockito.verify(imageService, Mockito.times(0)).updateImage(Mockito.anyString(), Mockito.any(MultipartFile.class), Mockito.anyString());
    }


    @Test
    @DisplayName("Update Meal  With null  status ")
    void updateMealTestWithNullStatus() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {

        this.mealDtoIn.setStatus(null);


        Assertions.assertThrows(InvalidFoodInformationException.class, () -> {
            mealService.updateMeal(mealDtoIn);
        });

        Mockito.verify(mealDao, Mockito.times(0)).findByLabelAndAndCategoryAndDescriptionIgnoreCase(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.verify(mealDao, Mockito.times(0)).save(mealEntity);
        Mockito.verify(imageService, Mockito.times(0)).updateImage(Mockito.anyString(), Mockito.any(MultipartFile.class), Mockito.anyString());
    }


    //--------------------------------------------- TEST  Price ---------------------------------------------//

    @Test
    @DisplayName("Update Meal with Too Big price")
    void updateMealTestWithBigPrice() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {

        this.mealDtoIn.setPrice(BigDecimal.valueOf(1001));

        Assertions.assertThrows(InvalidFoodInformationException.class, () -> {
            mealService.updateMeal(mealDtoIn);
        });
        Mockito.verify(mealDao, Mockito.times(0)).findByLabelAndAndCategoryAndDescriptionIgnoreCase(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.verify(mealDao, Mockito.times(0)).save(mealEntity);
        Mockito.verify(imageService, Mockito.times(0)).updateImage(Mockito.anyString(), Mockito.any(MultipartFile.class), Mockito.anyString());
    }


    @Test
    @DisplayName("Update Meal With  negative price")
    void updateMealTestWithNegativePrice() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {

        this.mealDtoIn.setPrice(BigDecimal.valueOf(-1.3));

        Assertions.assertThrows(InvalidFoodInformationException.class, () -> {
            mealService.updateMeal(mealDtoIn);
        });
        Mockito.verify(mealDao, Mockito.times(0)).findByLabelAndAndCategoryAndDescriptionIgnoreCase(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.verify(mealDao, Mockito.times(0)).save(mealEntity);
        Mockito.verify(imageService, Mockito.times(0)).updateImage(Mockito.anyString(), Mockito.any(MultipartFile.class), Mockito.anyString());
    }


    @Test
    @DisplayName("Update Meal With NULL Price")
    void updateMealTestWithNullPrice() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {

        this.mealDtoIn.setPrice(null);


        Assertions.assertThrows(InvalidFoodInformationException.class, () -> {
            mealService.updateMeal(mealDtoIn);
        });

        Mockito.verify(mealDao, Mockito.times(0)).findByLabelAndAndCategoryAndDescriptionIgnoreCase(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.verify(mealDao, Mockito.times(0)).save(mealEntity);
        Mockito.verify(imageService, Mockito.times(0)).updateImage(Mockito.anyString(), Mockito.any(MultipartFile.class), Mockito.anyString());
    }


    //--------------------------------------------- TEST  Description ---------------------------------------------//


    @Test
    @DisplayName("Update Meal With TOO LONG Description")
    void updateMealTestWithTooLongDescription() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {
        String tooLongString = "t".repeat(3000) + "ee";
        this.mealDtoIn.setDescription(tooLongString);


        Assertions.assertThrows(InvalidFoodInformationException.class, () -> {
            mealService.updateMeal(mealDtoIn);
        });
        Mockito.verify(mealDao, Mockito.times(0)).findByLabelAndAndCategoryAndDescriptionIgnoreCase(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.verify(mealDao, Mockito.times(0)).save(mealEntity);
        Mockito.verify(imageService, Mockito.times(0)).updateImage(Mockito.anyString(), Mockito.any(MultipartFile.class), Mockito.anyString());
    }


    @Test
    @DisplayName("Update Meal With TOO Short Description")
    void updateMealTestWithTooShortDescription() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {
        String tooshortString = "ta" + " ".repeat(10) + "ba";
        this.mealDtoIn.setDescription(tooshortString);

        Assertions.assertThrows(InvalidFoodInformationException.class, () -> {
            mealService.updateMeal(mealDtoIn);
        });
        Mockito.verify(mealDao, Mockito.times(0)).findByLabelAndAndCategoryAndDescriptionIgnoreCase(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.verify(mealDao, Mockito.times(0)).save(mealEntity);
        Mockito.verify(imageService, Mockito.times(0)).updateImage(Mockito.anyString(), Mockito.any(MultipartFile.class), Mockito.anyString());
    }


    @Test
    @DisplayName("Update Meal With NULL Description")
    void updateMealTestWithNullDescription() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {

        this.mealDtoIn.setDescription(null);


        Assertions.assertThrows(InvalidFoodInformationException.class, () -> {
            mealService.updateMeal(mealDtoIn);
        });

        Mockito.verify(mealDao, Mockito.times(0)).findByLabelAndAndCategoryAndDescriptionIgnoreCase(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.verify(mealDao, Mockito.times(0)).save(mealEntity);
        Mockito.verify(imageService, Mockito.times(0)).updateImage(Mockito.anyString(), Mockito.any(MultipartFile.class), Mockito.anyString());
    }


    //--------------------------------------------- TEST  Category ---------------------------------------------//


    @Test
    @DisplayName("Update Meal With TOO LONG Category")
    void updateMealTestWithTooLongCategory() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {
        String tooLongString = "t".repeat(101);
        this.mealDtoIn.setCategory(tooLongString);

        Assertions.assertThrows(InvalidFoodInformationException.class, () -> {
            mealService.updateMeal(mealDtoIn);
        });
        Mockito.verify(mealDao, Mockito.times(0)).findByLabelAndAndCategoryAndDescriptionIgnoreCase(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.verify(mealDao, Mockito.times(0)).save(mealEntity);
        Mockito.verify(imageService, Mockito.times(0)).updateImage(Mockito.anyString(), Mockito.any(MultipartFile.class), Mockito.anyString());
    }


    @Test
    @DisplayName("Update Meal With TOO Short Category")
    void updateMealTestWithTooShortCategory() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {
        String tooshortString = "t" + " ".repeat(10) + "b";
        this.mealDtoIn.setCategory(tooshortString);

        Assertions.assertThrows(InvalidFoodInformationException.class, () -> {
            mealService.updateMeal(mealDtoIn);
        });
        Mockito.verify(mealDao, Mockito.times(0)).findByLabelAndAndCategoryAndDescriptionIgnoreCase(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.verify(mealDao, Mockito.times(0)).save(mealEntity);
        Mockito.verify(imageService, Mockito.times(0)).updateImage(Mockito.anyString(), Mockito.any(MultipartFile.class), Mockito.anyString());
    }


    @Test
    @DisplayName("Update Meal With NULL Category")
    void updateMealTestWithNullCategory() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {

        this.mealDtoIn.setCategory(null);


        Assertions.assertThrows(InvalidFoodInformationException.class, () -> {
            mealService.updateMeal(mealDtoIn);
        });

        Mockito.verify(mealDao, Mockito.times(0)).findByLabelAndAndCategoryAndDescriptionIgnoreCase(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.verify(mealDao, Mockito.times(0)).save(mealEntity);
        Mockito.verify(imageService, Mockito.times(0)).updateImage(Mockito.anyString(), Mockito.any(MultipartFile.class), Mockito.anyString());
    }


    //--------------------------------------------- TEST  Label ---------------------------------------------//


    @Test
    @DisplayName("Update Meal With TOO LONG Label")
    void updateMealTestWithTooLongLabel() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {
        String tooLangString = "t".repeat(101);
        this.mealDtoIn.setLabel(tooLangString);

        Assertions.assertThrows(InvalidFoodInformationException.class, () -> {
            mealService.updateMeal(mealDtoIn);
        });
        Mockito.verify(mealDao, Mockito.times(0)).findByLabelAndAndCategoryAndDescriptionIgnoreCase(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.verify(mealDao, Mockito.times(0)).save(mealEntity);
        Mockito.verify(imageService, Mockito.times(0)).updateImage(Mockito.anyString(), Mockito.any(MultipartFile.class), Mockito.anyString());
    }


    @Test
    @DisplayName("Update Meal With TOO Short Label")
    void updateMealTestWithTooShortLabel() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {
        String tooshortString = "t" + " ".repeat(10) + "b";
        this.mealDtoIn.setLabel(tooshortString);


        Assertions.assertThrows(InvalidFoodInformationException.class, () -> {
            mealService.updateMeal(mealDtoIn);
        });
        Mockito.verify(mealDao, Mockito.times(0)).findByLabelAndAndCategoryAndDescriptionIgnoreCase(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.verify(mealDao, Mockito.times(0)).save(mealEntity);
        Mockito.verify(imageService, Mockito.times(0)).updateImage(Mockito.anyString(), Mockito.any(MultipartFile.class), Mockito.anyString());
    }


    @Test
    @DisplayName("Update Meal With NULL Label")
    void updateMealTestWithNullLabel() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {

        this.mealDtoIn.setLabel(null);

        Assertions.assertThrows(InvalidFoodInformationException.class, () -> {
            mealService.updateMeal(mealDtoIn);
        });

        Mockito.verify(mealDao, Mockito.times(0)).findByLabelAndAndCategoryAndDescriptionIgnoreCase(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.verify(mealDao, Mockito.times(0)).save(mealEntity);
        Mockito.verify(imageService, Mockito.times(0)).updateImage(Mockito.anyString(), Mockito.any(MultipartFile.class), Mockito.anyString());
    }


    //--------------------------------------------- TEST  UUID ---------------------------------------------//
    @Test
    @DisplayName("Update Meal With Short Uuid")
    void updateMealTestWithShortUuiD() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {
        String uuidMeal = "a".repeat(19);
        this.mealDtoIn.setUuid(uuidMeal);

        Assertions.assertThrows(InvalidFoodInformationException.class, () -> {
            mealService.updateMeal(mealDtoIn);
        });

        Mockito.verify(mealDao, Mockito.times(0)).findByLabelAndAndCategoryAndDescriptionIgnoreCase(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.verify(mealDao, Mockito.times(0)).save(mealEntity);
        Mockito.verify(imageService, Mockito.times(0)).updateImage(Mockito.anyString(), Mockito.any(MultipartFile.class), Mockito.anyString());
    }

    @Test
    @DisplayName("Update Meal With Empty Uuid")
    void updateMealTestWithEmptyID() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {
        String uuidMeal = "    ";
        this.mealDtoIn.setUuid(uuidMeal);

        Assertions.assertThrows(InvalidFoodInformationException.class, () -> {
            mealService.updateMeal(mealDtoIn);
        });

        Mockito.verify(mealDao, Mockito.times(0)).findByLabelAndAndCategoryAndDescriptionIgnoreCase(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.verify(mealDao, Mockito.times(0)).save(mealEntity);
        Mockito.verify(imageService, Mockito.times(0)).updateImage(Mockito.anyString(), Mockito.any(MultipartFile.class), Mockito.anyString());
    }

    @Test
    @DisplayName("Update Meal With Null Uuid ")
    void updateMealTestWithNullUuId() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {
        this.mealDtoIn.setUuid(null);
        Assertions.assertThrows(InvalidFoodInformationException.class, () -> {
            mealService.updateMeal(mealDtoIn);
        });
        Mockito.verify(mealDao, Mockito.times(0)).findByLabelAndAndCategoryAndDescriptionIgnoreCase(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.verify(mealDao, Mockito.times(0)).save(mealEntity);
        Mockito.verify(imageService, Mockito.times(0)).updateImage(Mockito.anyString(), Mockito.any(MultipartFile.class), Mockito.anyString());

    }


    @Test
    void updateMealWithNullMealDtoInTest() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {

        Assertions.assertThrows(InvalidFoodInformationException.class, () -> {
            mealService.updateMeal(null);
        });
        Mockito.verify(mealDao, Mockito.times(0)).findByLabelAndAndCategoryAndDescriptionIgnoreCase(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.verify(mealDao, Mockito.times(0)).save(Mockito.any());
        Mockito.verify(imageService, Mockito.times(0)).updateImage(Mockito.anyString(), Mockito.any(MultipartFile.class), Mockito.anyString());

    }


}
