package fr.sqli.Cantine.service.admin.meals;


import fr.sqli.Cantine.dao.IMealDao;
import fr.sqli.Cantine.dto.in.MealDtoIn;
import fr.sqli.Cantine.entity.ImageEntity;
import fr.sqli.Cantine.entity.MealEntity;
import fr.sqli.Cantine.service.admin.IMealService;
import fr.sqli.Cantine.service.admin.MealService;
import fr.sqli.Cantine.service.admin.exceptions.InvalidMealInformationAdminException;
import fr.sqli.Cantine.service.images.IImageService;
import fr.sqli.Cantine.service.images.ImageService;
import fr.sqli.Cantine.service.images.exception.ImagePathException;
import fr.sqli.Cantine.service.images.exception.InvalidImageException;
import fr.sqli.Cantine.service.images.exception.InvalidTypeImageException;
import net.bytebuddy.implementation.bind.annotation.Super;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class AddAndRemoveMealTest   {

    @Mock
    private IMealDao mealDao;

     MealEntity mealEntity;
     MealDtoIn mealDtoIn;
    @Mock
    private IImageService imageService;
    @InjectMocks
    private MealService mealService;

    @Mock
    private  MockEnvironment env;
    @BeforeEach
    public void setUp() {

        env = new MockEnvironment();
        env.setProperty("sqli.cantine.images.url.meals", "http://localhost:8080/cantine/download/images/meals/");
        mealService = new MealService(env, mealDao, imageService);
        this.mealEntity =   new MealEntity(1 , "Meal 1","first Meal To  Test", "Frites",new BigDecimal(1.3), 1,1,new ImageEntity());

    }


    @Test
    void  testAddMealWithNegativePrice() throws InvalidTypeImageException, InvalidImageException, ImagePathException{
        this.mealDtoIn = new MealDtoIn();
        this.mealDtoIn.setLabel("Meal 1");
        this.mealDtoIn.setCategorie("Frites");
        this.mealDtoIn.setDescription("first Meal To  Test");
        this.mealDtoIn.setPrixht(new BigDecimal(-1.3));
        this.mealDtoIn.setImage(Mockito.mock(MultipartFile.class));
        this.mealDtoIn.setQuantite(1);
        this.mealDtoIn.setStatus(1);
        Assertions.assertThrows(InvalidMealInformationAdminException.class,
                () -> mealService.addMeal(mealDtoIn));
    }

    @Test
    void  testAddMealWithTooLangLabel() throws InvalidTypeImageException, InvalidImageException, ImagePathException{
        String tooLangString = "Lorem ipsum dolor sit adccdcmet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean m";
        this.mealDtoIn = new MealDtoIn();
        this.mealDtoIn = new MealDtoIn();
        this.mealDtoIn.setLabel(tooLangString);
        this.mealDtoIn.setCategorie("Frites");
        this.mealDtoIn.setDescription("first Meal To  Test");
        this.mealDtoIn.setPrixht(new BigDecimal(1.3));
        this.mealDtoIn.setImage(Mockito.mock(MultipartFile.class));
        this.mealDtoIn.setQuantite(1);
        this.mealDtoIn.setStatus(1);
        Assertions.assertThrows(InvalidMealInformationAdminException.class,
                () -> mealService.addMeal(mealDtoIn));
    }
    @Test
    void testAddMealWithNullImage() throws InvalidTypeImageException, InvalidImageException, ImagePathException, IOException, InvalidMealInformationAdminException {
        this.mealDtoIn = new MealDtoIn();
        this.mealDtoIn.setLabel("Meal 1");
        this.mealDtoIn.setCategorie("Frites");
        this.mealDtoIn.setDescription("first Meal To  Test");
        this.mealDtoIn.setPrixht(new BigDecimal(1.3));
        this.mealDtoIn.setQuantite(1);
        this.mealDtoIn.setStatus(1);
        this.mealDtoIn.setImage(null);
        Assertions.assertThrows(InvalidMealInformationAdminException.class,
                () -> mealService.addMeal(mealDtoIn));

        Mockito.verify(mealDao, Mockito.times(0)).save(Mockito.any(MealEntity.class));
        Mockito.verify(imageService, Mockito.times(0)).uploadImage(mealDtoIn.getImage(), "images/meals" );
    }


    @Test
    void  testAddMealWithNullMealInformation() throws InvalidTypeImageException, InvalidImageException, ImagePathException, IOException, InvalidMealInformationAdminException {
        this.mealDtoIn = new MealDtoIn();
        Assertions.assertThrows(InvalidMealInformationAdminException.class,
                () -> mealService.addMeal(mealDtoIn));

        Mockito.verify(mealDao, Mockito.times(0)).save(Mockito.any(MealEntity.class));
        Mockito.verify(imageService, Mockito.times(0)).uploadImage(mealDtoIn.getImage(), "images/meals" );
    }

    /**
     * Test the addMeal method with valid meal information and valid image
     * @throws InvalidTypeImageException
     * @throws InvalidImageException
     * @throws ImagePathException
     * @throws IOException
     */
    @Test
    public void testAddMeal() throws InvalidTypeImageException, InvalidImageException, ImagePathException, IOException, InvalidMealInformationAdminException {

        this.mealDtoIn = new MealDtoIn();
        this.mealDtoIn.setLabel("Meal 1");
        this.mealDtoIn.setCategorie("Frites");
        this.mealDtoIn.setDescription("first Meal To  Test");
        this.mealDtoIn.setPrixht(new BigDecimal(1.3));
        this.mealDtoIn.setImage(Mockito.mock(MultipartFile.class));
        this.mealDtoIn.setQuantite(1);
        this.mealDtoIn.setStatus(1);

        Mockito.when(env.getProperty("sqli.cantine.images.url.meals")).thenReturn("/images/meals");


        String imageName = "test-image.jpg";
        Mockito.when(imageService.uploadImage(mealDtoIn.getImage(), "images/meals" )).thenReturn(imageName);

        // Mock database save

        Mockito.when(this.mealDao.save(Mockito.any(MealEntity.class))).thenReturn( mealEntity);

        // Call the method
        MealEntity result = mealService.addMeal(mealDtoIn);

        // Verify the result
        Assertions.assertNotNull(result);
        Assertions.assertEquals(mealDtoIn.getLabel(), result.getLabel());
        Assertions.assertEquals(mealDtoIn.getDescription(), result.getDescription());
        Assertions.assertEquals(mealDtoIn.getPrixht(), result.getPrixht());
        Assertions.assertNotNull(result.getImage());

        Mockito.verify(mealDao, Mockito.times(1)).save(Mockito.any(MealEntity.class));
        Mockito.verify(imageService, Mockito.times(1)).uploadImage(mealDtoIn.getImage(), "images/meals" );
    }

    /*
    *
    *  this.mealDtoIn.setLabel(null);
        this.mealDtoIn.setCategorie(null);
        this.mealDtoIn.setDescription(null);
        this.mealDtoIn.setPrixht(null);
        this.mealDtoIn.setImage(Mockito.mock(MultipartFile.class));
        this.mealDtoIn.setQuantite(0);
        this.mealDtoIn.setStatus(0);
    *
    * */

}