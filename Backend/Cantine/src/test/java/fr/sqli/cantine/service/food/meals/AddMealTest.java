package fr.sqli.cantine.service.food.meals;

import fr.sqli.cantine.dao.IMealDao;
import fr.sqli.cantine.dto.in.food.MealDtoIn;
import fr.sqli.cantine.entity.ImageEntity;
import fr.sqli.cantine.entity.MealEntity;
import fr.sqli.cantine.entity.MealTypeEnum;
import fr.sqli.cantine.service.food.exceptions.ExistingFoodException;
import fr.sqli.cantine.service.food.exceptions.InvalidFoodInformationException;
import fr.sqli.cantine.service.food.impl.MealService;
import fr.sqli.cantine.service.images.IImageService;
import fr.sqli.cantine.service.images.exception.ImagePathException;
import fr.sqli.cantine.service.images.exception.InvalidFormatImageException;
import fr.sqli.cantine.service.images.exception.InvalidImageException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.mock.web.MockMultipartFile;


import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

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
        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setImagename("image-test");
        MealTypeEnum mealTypeEnum = MealTypeEnum.getMealTypeEnum("ENTREE");
        this.mealEntity = new MealEntity(mealLabel, categoryMeal, descriptionMeal, BigDecimal.valueOf(1.3), 1, 1, mealTypeEnum, imageEntity);
        this.mealEntity.setId(1);


        this.mealDtoIn = new MealDtoIn();
        this.mealDtoIn.setLabel(mealLabel);
        this.mealDtoIn.setCategory(categoryMeal);
        this.mealDtoIn.setDescription(descriptionMeal);
        this.mealDtoIn.setPrice(new BigDecimal("1.3"));
        this.mealDtoIn.setMealType("ENTREE");
        this.mealDtoIn.setQuantity(1);
        this.mealDtoIn.setStatus(1);
        this.mealDtoIn.setImage(new MockMultipartFile("oldImage", "image.jpg", "text/plain", "Spring Framework".getBytes()));

    }

    @AfterEach
    void clean() {
        this.mealDtoIn = null;
        this.mealEntity = null;
        this.mealService = null;
    }


    @Test
    @DisplayName("Test the addMeal method with valid meal information and valid image")
    public void AddMealWihValidInformationTest() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, InvalidFoodInformationException, ExistingFoodException {

        //  spaces  from  the  label  is   removed
        Mockito.when(this.mealDao.findByLabelAndAndCategoryAndDescriptionIgnoreCase(this.mealDtoIn.getLabel(), mealDtoIn.getCategory(), mealDtoIn.getDescription())).thenReturn(Optional.empty());

        String imageName = "test-image.jpg";
        Mockito.when(imageService.uploadImage(mealDtoIn.getImage(), "images/meals")).thenReturn(imageName);

        // Mock database save

        Mockito.when(this.mealDao.save(Mockito.any(MealEntity.class))).thenReturn(mealEntity);


        // Call the method
        MealEntity result = this.mealService.addMeal(this.mealDtoIn);

        // Verify the result
        Assertions.assertNotNull(result);
        Assertions.assertEquals(mealDtoIn.getLabel(), result.getLabel());
        Assertions.assertEquals(mealDtoIn.getDescription().trim(), result.getDescription());
        Assertions.assertEquals(mealDtoIn.getPrice(), result.getPrice());
        Assertions.assertNotNull(result.getImage());

        Mockito.verify(mealDao, Mockito.times(1)).findByLabelAndAndCategoryAndDescriptionIgnoreCase(mealDtoIn.getLabel(), mealDtoIn.getCategory(), mealDtoIn.getDescription());
        Mockito.verify(mealDao, Mockito.times(1)).save(Mockito.any(MealEntity.class));  //  we  can not  make save(this.mealEntity) because  the  new mealEntity object will  be saved  and  not (this.mealEntity)
        Mockito.verify(imageService, Mockito.times(1)).uploadImage(mealDtoIn.getImage(), "images/meals");
    }


    @Test
    @DisplayName("Test the addMeal method with Existing meal")
    void AddMealWithExistingMealTest() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {

        //  the spaces are  removed  from  the  label
        Mockito.when(this.mealDao.findByLabelAndAndCategoryAndDescriptionIgnoreCase(this.mealDtoIn.getLabel(), this.mealDtoIn.getCategory(), this.mealDtoIn.getDescription())).thenReturn(Optional.of(this.mealEntity));
        Assertions.assertThrows(ExistingFoodException.class, () -> mealService.addMeal(mealDtoIn));

        Mockito.verify(this.imageService, Mockito.times(0)).updateImage(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.verify(this.mealDao, Mockito.times(1)).findByLabelAndAndCategoryAndDescriptionIgnoreCase(this.mealDtoIn.getLabel(), this.mealDtoIn.getCategory(), this.mealDtoIn.getDescription());
        Mockito.verify(this.mealDao, Mockito.times(0)).save(this.mealEntity);
    }


    /********************************** Quantity  ************************************/


    @Test
    @DisplayName("Test the addMeal method with Too Big Quantity")
    void AddMealWithTooBigQuantityTest() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {

        this.mealDtoIn.setQuantity(10001);

        Assertions.assertThrows(InvalidFoodInformationException.class, () -> mealService.addMeal(mealDtoIn));

        Mockito.verify(this.imageService, Mockito.times(0)).updateImage(Mockito.any(), Mockito.any(), Mockito.any());

        Mockito.verify(this.mealDao, Mockito.times(0)).save(this.mealEntity);
    }


    @Test
    @DisplayName("Test the addMeal method with negative Quantity")
    void AddMealWithNegativeQuantityTest() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {

        this.mealDtoIn.setQuantity(-2);

        Assertions.assertThrows(InvalidFoodInformationException.class, () -> mealService.addMeal(mealDtoIn));

        Mockito.verify(this.imageService, Mockito.times(0)).updateImage(Mockito.any(), Mockito.any(), Mockito.any());

        Mockito.verify(this.mealDao, Mockito.times(0)).save(this.mealEntity);
    }

    @Test
    @DisplayName("Test the addMeal method with null Quantity")
    void AddMealWithNullQuantityTest() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {

        this.mealDtoIn.setQuantity(null);

        Assertions.assertThrows(InvalidFoodInformationException.class, () -> mealService.addMeal(mealDtoIn));

        Mockito.verify(this.imageService, Mockito.times(0)).updateImage(Mockito.any(), Mockito.any(), Mockito.any());

        Mockito.verify(this.mealDao, Mockito.times(0)).save(this.mealEntity);
    }

    @Test
    public void AddMealWithInvalidMealType() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {
        this.mealDtoIn.setMealType("TEST");

        Assertions.assertThrows(InvalidFoodInformationException.class, () -> mealService.addMeal(mealDtoIn));


        Mockito.verify(this.mealDao, Mockito.times(0)).save(Mockito.any(MealEntity.class));
        Mockito.verify(this.imageService, Mockito.times(0)).uploadImage(Mockito.any(), Mockito.any());
    }
    @Test
    public void AddMealWithEmptyMealType() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {
        this.mealDtoIn.setMealType("");

        Assertions.assertThrows(InvalidFoodInformationException.class, () -> mealService.addMeal(mealDtoIn));


        Mockito.verify(this.mealDao, Mockito.times(0)).save(Mockito.any(MealEntity.class));
        Mockito.verify(this.imageService, Mockito.times(0)).uploadImage(Mockito.any(), Mockito.any());
    }
    @Test
    public void AddMealWithNullMealType() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {
        this.mealDtoIn.setMealType(null);

        Assertions.assertThrows(InvalidFoodInformationException.class, () -> mealService.addMeal(mealDtoIn));


        Mockito.verify(this.mealDao, Mockito.times(0)).save(Mockito.any(MealEntity.class));
        Mockito.verify(this.imageService, Mockito.times(0)).uploadImage(Mockito.any(), Mockito.any());
    }
    @Test
    @DisplayName("Test the addMeal method with status out of bound status = -1")
    public void AddMealWithStatusOutOfBoundTest1() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {
        this.mealDtoIn.setStatus(-1);

        Assertions.assertThrows(InvalidFoodInformationException.class, () -> mealService.addMeal(mealDtoIn));


        Mockito.verify(this.mealDao, Mockito.times(0)).save(Mockito.any(MealEntity.class));
        Mockito.verify(this.imageService, Mockito.times(0)).uploadImage(Mockito.any(), Mockito.any());
    }

    @Test
    @DisplayName("Test the addMeal method with status out of bound status = 3")
    public void AddMealWithStatusOutOfBoundTest() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {
        this.mealDtoIn.setStatus(3);

        Assertions.assertThrows(InvalidFoodInformationException.class, () -> mealService.addMeal(mealDtoIn));


        Mockito.verify(this.mealDao, Mockito.times(0)).save(Mockito.any(MealEntity.class));
        Mockito.verify(this.imageService, Mockito.times(0)).uploadImage(Mockito.any(), Mockito.any());
    }


    @Test
    @DisplayName("Test the addMeal method with null  status ")
    public void AddMealWithNullStatusTest() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {
        this.mealDtoIn.setStatus(null);

        Assertions.assertThrows(InvalidFoodInformationException.class, () -> mealService.addMeal(mealDtoIn));


        Mockito.verify(this.mealDao, Mockito.times(0)).save(Mockito.any(MealEntity.class));
        Mockito.verify(this.imageService, Mockito.times(0)).uploadImage(Mockito.any(), Mockito.any());
    }


    /********************************** Price  ************************************/

    @Test
    @DisplayName("Test the addMeal method with Too Big price")
    void AddMealWithTooBigPriceTest() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {

        this.mealDtoIn.setPrice(BigDecimal.valueOf(1001));

        Assertions.assertThrows(InvalidFoodInformationException.class, () -> mealService.addMeal(mealDtoIn));

        Mockito.verify(this.imageService, Mockito.times(0)).updateImage(Mockito.any(), Mockito.any(), Mockito.any());

        Mockito.verify(this.mealDao, Mockito.times(0)).save(this.mealEntity);
    }


    @Test
    @DisplayName("Test the addMeal method with negative price")
    void AddMealWithNegativePriceTest() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {

        this.mealDtoIn.setPrice(BigDecimal.valueOf(-1.3));

        Assertions.assertThrows(InvalidFoodInformationException.class, () -> mealService.addMeal(mealDtoIn));

        Mockito.verify(this.imageService, Mockito.times(0)).updateImage(Mockito.any(), Mockito.any(), Mockito.any());

        Mockito.verify(this.mealDao, Mockito.times(0)).save(this.mealEntity);
    }

    @Test
    @DisplayName("Test the addMeal method with null price")
    void AddMealWithNullPriceTest() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {

        this.mealDtoIn.setPrice(null);

        Assertions.assertThrows(InvalidFoodInformationException.class, () -> mealService.addMeal(mealDtoIn));

        Mockito.verify(this.imageService, Mockito.times(0)).updateImage(Mockito.any(), Mockito.any(), Mockito.any());

        Mockito.verify(this.mealDao, Mockito.times(0)).save(this.mealEntity);
    }


    /********************************** Category ************************************/

    @Test
    @DisplayName("Test the addMeal method with too lang category")
    void AddMealWithTooLangCategoryTest() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {
        String tooLongString = "t".repeat(101);


        this.mealDtoIn.setCategory(tooLongString);

        Assertions.assertThrows(InvalidFoodInformationException.class, () -> mealService.addMeal(mealDtoIn));

        Mockito.verify(mealDao, Mockito.times(0)).save(Mockito.any(MealEntity.class));
        Mockito.verify(this.imageService, Mockito.times(0)).updateImage(Mockito.any(), Mockito.any(), Mockito.any());
    }


    @Test
    @DisplayName("Test the addMeal method with too short category")
    void AddMealWithTooShortCategoryTest() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {
        String tooshortString = "t" + " ".repeat(10) + "b";


        this.mealDtoIn.setCategory(tooshortString);

        Assertions.assertThrows(InvalidFoodInformationException.class, () -> mealService.addMeal(mealDtoIn));

        Mockito.verify(mealDao, Mockito.times(0)).save(Mockito.any(MealEntity.class));
        Mockito.verify(this.imageService, Mockito.times(0)).updateImage(Mockito.any(), Mockito.any(), Mockito.any());
    }


    @Test
    @DisplayName("Test the addMeal method with null category")
    void AddMealWithNullCategoryTest() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {

        this.mealDtoIn.setCategory(null);

        Assertions.assertThrows(InvalidFoodInformationException.class, () -> mealService.addMeal(mealDtoIn));

        Mockito.verify(mealDao, Mockito.times(0)).save(Mockito.any(MealEntity.class));
        Mockito.verify(this.imageService, Mockito.times(0)).updateImage(Mockito.any(), Mockito.any(), Mockito.any());
    }


    /********************************** Description ************************************/

    @Test
    @DisplayName("Test the addMeal method with too lang Description ")
    void AddMealWithTooLangDescriptionTest() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {
        String tooLongString = "t".repeat(3000) + "ee";


        this.mealDtoIn.setDescription(tooLongString);

        Assertions.assertThrows(InvalidFoodInformationException.class, () -> mealService.addMeal(mealDtoIn));

        Mockito.verify(mealDao, Mockito.times(0)).save(Mockito.any(MealEntity.class));
        Mockito.verify(this.imageService, Mockito.times(0)).updateImage(Mockito.any(), Mockito.any(), Mockito.any());
    }


    @Test
    @DisplayName("Test the addMeal method with too short Description")
    void AddMealWithTooShortDescriptionTest() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {
        String tooshortString = "ta" + " ".repeat(10) + "ba";


        this.mealDtoIn.setDescription(tooshortString);

        Assertions.assertThrows(InvalidFoodInformationException.class, () -> mealService.addMeal(mealDtoIn));

        Mockito.verify(mealDao, Mockito.times(0)).save(Mockito.any(MealEntity.class));
        Mockito.verify(this.imageService, Mockito.times(0)).updateImage(Mockito.any(), Mockito.any(), Mockito.any());
    }


    @Test
    @DisplayName("Test the addMeal method with null Description")
    void AddMealWithNullDescriptionTest() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {

        this.mealDtoIn.setDescription(null);

        Assertions.assertThrows(InvalidFoodInformationException.class, () -> mealService.addMeal(mealDtoIn));

        Mockito.verify(mealDao, Mockito.times(0)).save(Mockito.any(MealEntity.class));
        Mockito.verify(this.imageService, Mockito.times(0)).updateImage(Mockito.any(), Mockito.any(), Mockito.any());
    }


    /********************************** Label ************************************/

    @Test
    @DisplayName("Test the addMeal method with too lang label")
    void AddMealWithTooLangLabelTest() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {
        String tooLongString = "test".repeat(101);


        this.mealDtoIn.setLabel(tooLongString);

        Assertions.assertThrows(InvalidFoodInformationException.class, () -> mealService.addMeal(mealDtoIn));

        Mockito.verify(mealDao, Mockito.times(0)).save(Mockito.any(MealEntity.class));
        Mockito.verify(this.imageService, Mockito.times(0)).updateImage(Mockito.any(), Mockito.any(), Mockito.any());
    }


    @Test
    @DisplayName("Test the addMeal method with too short label")
    void AddMealWithTooShortLabelTest() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {
        String tooshortString = "t" + " ".repeat(10) + "b";


        this.mealDtoIn.setLabel(tooshortString);

        Assertions.assertThrows(InvalidFoodInformationException.class, () -> mealService.addMeal(mealDtoIn));

        Mockito.verify(mealDao, Mockito.times(0)).save(Mockito.any(MealEntity.class));
        Mockito.verify(this.imageService, Mockito.times(0)).updateImage(Mockito.any(), Mockito.any(), Mockito.any());
    }


    @Test
    @DisplayName("Test the addMeal method with null label")
    void AddMealWithNullLabelTest() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {

        this.mealDtoIn.setLabel(null);

        Assertions.assertThrows(InvalidFoodInformationException.class, () -> mealService.addMeal(mealDtoIn));

        Mockito.verify(mealDao, Mockito.times(0)).save(Mockito.any(MealEntity.class));
        Mockito.verify(this.imageService, Mockito.times(0)).updateImage(Mockito.any(), Mockito.any(), Mockito.any());
    }


    /********************************** MealDtoIn ************************************/

    @Test
    @DisplayName("Test the addMeal method with null image")
    void AddMealWithNullImageNet() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {
        this.mealDtoIn.setImage(null);
        Assertions.assertThrows(InvalidFoodInformationException.class, () -> mealService.addMeal(mealDtoIn));

        Mockito.verify(mealDao, Mockito.times(0)).save(Mockito.any(MealEntity.class));
        Mockito.verify(imageService, Mockito.times(0)).uploadImage(mealDtoIn.getImage(), "images/meals");
    }


    @Test
    @DisplayName("Test the addMeal method with null meal information")
    void AddMealWithNullMealInformation() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {
        this.mealDtoIn = new MealDtoIn();

        Assertions.assertThrows(InvalidFoodInformationException.class, () -> mealService.addMeal(mealDtoIn));

        Mockito.verify(mealDao, Mockito.times(0)).save(Mockito.any(MealEntity.class));
        Mockito.verify(imageService, Mockito.times(0)).uploadImage(mealDtoIn.getImage(), "images/meals");
    }

    @Test
    @DisplayName("Test the addMeal method with null  MealDtoIn")
    void AddMealWithNullRequestData() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {

        Assertions.assertThrows(InvalidFoodInformationException.class, () -> mealService.addMeal(null));

        Mockito.verify(mealDao, Mockito.times(0)).save(Mockito.any(MealEntity.class));
        Mockito.verify(imageService, Mockito.times(0)).uploadImage(mealDtoIn.getImage(), "images/meals");
    }


}
