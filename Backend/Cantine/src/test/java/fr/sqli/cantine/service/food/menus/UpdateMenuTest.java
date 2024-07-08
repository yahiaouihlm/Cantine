package fr.sqli.cantine.service.food.menus;

import fr.sqli.cantine.dao.IMenuDao;
import fr.sqli.cantine.dto.in.food.MenuDtoIn;
import fr.sqli.cantine.entity.ImageEntity;
import fr.sqli.cantine.entity.MealEntity;
import fr.sqli.cantine.entity.MenuEntity;
import fr.sqli.cantine.service.food.exceptions.ExistingFoodException;
import fr.sqli.cantine.service.food.exceptions.FoodNotFoundException;
import fr.sqli.cantine.service.food.exceptions.InvalidFoodInformationException;
import fr.sqli.cantine.service.food.exceptions.UnavailableFoodException;
import fr.sqli.cantine.service.food.impl.MealService;
import fr.sqli.cantine.service.food.impl.MenuService;
import fr.sqli.cantine.service.images.IImageService;
import fr.sqli.cantine.service.images.exception.ImagePathException;
import fr.sqli.cantine.service.images.exception.InvalidFormatImageException;
import fr.sqli.cantine.service.images.exception.InvalidImageException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.mock.web.MockMultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;


@ExtendWith(MockitoExtension.class)
public class UpdateMenuTest {
    @Mock
    private IMenuDao iMenuDao;
    @Mock
    private MealService iMealService;
    @Mock
    IImageService imageService;

    @InjectMocks
    private MenuService menuService;

    @Mock
    MockEnvironment environment;
    private MealEntity mealEntity;

   private  MenuEntity menuEntity;

    private MenuDtoIn menuDtoIn;

    @BeforeEach
    void  init () throws IOException {

        this.menuEntity = new MenuEntity();

        this.menuEntity.setLabel("Test label");
        this.menuEntity.setDescription("Test description");
        this.menuEntity.setPrice(BigDecimal.valueOf(1.5));
        this.menuEntity.setId(1);
        this.menuEntity.setImage(new ImageEntity());

        this.menuDtoIn =  new MenuDtoIn();
        /*TODO:  this  is  not  correct*/
        this.menuDtoIn.setLabel("label test");
        this.menuDtoIn.setUuid(java.util.UUID.randomUUID().toString());
        this.menuDtoIn.setDescription("description  test");
        this.menuDtoIn.setQuantity(10);
        this.menuDtoIn.setStatus(1);
        this.menuDtoIn.setPrice(BigDecimal.valueOf(1.5));
        this.menuDtoIn.setImage( new MockMultipartFile(
                "image",                         // nom du champ de fichier
                "ImageMenuForTest.jpg",          // nom du fichier
                "image/jpg",                    // type MIME
                new FileInputStream("imagesTests/ImageForTest.jpg")));


    }


    @Test
    void updateMenuWithFewMealsTest() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, InvalidFoodInformationException, ExistingFoodException, FoodNotFoundException, UnavailableFoodException {
        this.menuDtoIn.setUuid(java.util.UUID.randomUUID().toString());
        this.menuDtoIn.setImage(null);
        String mealUuid = java.util.UUID.randomUUID().toString();
        MealEntity mealEntity = new MealEntity();
        mealEntity.setUuid(mealUuid);
        mealEntity.setStatus(1);
        this.menuDtoIn.setListOfMealsAsString(" [\"" + mealUuid + "\" ] ");


        Mockito.when(iMenuDao.findByUuid(this.menuDtoIn.getUuid())).thenReturn(Optional.of(this.menuEntity));

        Mockito.when(this.iMenuDao.findByLabelAndAndPriceAndDescriptionIgnoreCase(this.menuDtoIn.getLabel().trim(), this.menuDtoIn.getDescription(), this.menuDtoIn.getPrice()))
                .thenReturn(Optional.of(this.menuEntity));

        Mockito.when(this.iMealService.getMealEntityByUUID(mealUuid)).thenReturn(mealEntity);



        Assertions.assertThrows(InvalidFoodInformationException.class , () -> this.menuService.updateMenu(this.menuDtoIn));
    }

    @Test
    void  updateMenuWithInvalidMealsUuId () throws InvalidFoodInformationException, FoodNotFoundException {
        this.menuEntity = new MenuEntity();
       String uuid  = java.util.UUID.randomUUID().toString();
        this.menuEntity.setLabel("Test label");
        this.menuEntity.setDescription("Test description");
        this.menuEntity.setPrice(BigDecimal.valueOf(1.5));
        this.menuEntity.setId(1);
        this.menuEntity.setImage(new ImageEntity());
        this.menuDtoIn.setListOfMealsAsString(" [\"" + uuid+ "\" ] ");

        MealEntity mealEntity = new MealEntity();
        mealEntity.setUuid(uuid);
        mealEntity.setStatus(0);


        Mockito.when(iMenuDao.findByUuid(this.menuDtoIn.getUuid())).thenReturn(Optional.of(this.menuEntity));
        Mockito.when(this.iMealService.getMealEntityByUUID(uuid)).thenReturn(mealEntity);

        Assertions.assertThrows(UnavailableFoodException.class , () -> this.menuService.updateMenu(this.menuDtoIn));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());
    }
    @Test
    void  updateMenuWithExistingMenuTest () {
        MenuEntity existingMenu = new MenuEntity();
        existingMenu.setUuid(java.util.UUID.randomUUID().toString());
        existingMenu.setId(10);
        this.menuDtoIn.setListOfMealsAsString(" [\"" + java.util.UUID.randomUUID() + "\" ] ");
        Mockito.when(iMenuDao.findByUuid(this.menuDtoIn.getUuid())).thenReturn(Optional.of(this.menuEntity));


        Mockito.when(this.iMenuDao.findByLabelAndAndPriceAndDescriptionIgnoreCase(this.menuDtoIn.getLabel().trim(), this.menuDtoIn.getDescription(),  this.menuDtoIn.getPrice()))
                  .thenReturn(Optional.of(existingMenu));

        Assertions.assertThrows(ExistingFoodException.class , () -> this.menuService.updateMenu(this.menuDtoIn));
        Mockito.verify(iMenuDao, Mockito.times(1)).findByLabelAndAndPriceAndDescriptionIgnoreCase(this.menuDtoIn.getLabel(), this.menuDtoIn.getDescription(),  this.menuDtoIn.getPrice());
        Mockito.verify(iMenuDao, Mockito.times(1)).findByUuid(this.menuDtoIn.getUuid());
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());
    }
    @Test
    void  updateMenuWithMenuNotFoundTest () {
        this.menuDtoIn.setListOfMealsAsString(" [\"" + java.util.UUID.randomUUID() + "\" ] ");
        Mockito.when(iMenuDao.findByUuid(this.menuDtoIn.getUuid())).thenReturn(Optional.empty());
        Assertions.assertThrows(FoodNotFoundException.class , () -> this.menuService.updateMenu(this.menuDtoIn));
        Mockito.verify(iMenuDao, Mockito.times(1)).findByUuid(this.menuDtoIn.getUuid());
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());
    }




   /*************************************** status  ******************************************/



    @Test
    void UpdateMenuWithInvalidStatusTest(){
        this.menuDtoIn.setStatus(2);
        Assertions.assertThrows(InvalidFoodInformationException.class , () ->this.menuService.updateMenu(this.menuDtoIn));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }

    @Test
    void UpdateMenuWithNegativeStatusTest(){
        this.menuDtoIn.setStatus(-1);
        Assertions.assertThrows(InvalidFoodInformationException.class , () -> this.menuService.updateMenu(this.menuDtoIn));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }

    @Test
    void UpdateMenuWithNullStatusTest () {
        this.menuDtoIn.setStatus(null);
        Assertions.assertThrows(InvalidFoodInformationException.class, () -> this.menuService.updateMenu(this.menuDtoIn));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }


    /*************************************** Quantiy  ******************************************/


    @Test
    void UpdateMenuWithTooHeightQuantityTest(){
        this.menuDtoIn.setQuantity(1000000);
        Assertions.assertThrows(InvalidFoodInformationException.class , () -> this.menuService.updateMenu(this.menuDtoIn));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }

    @Test
    void UpdateMenuWithNegativeQuantityTest(){
        this.menuDtoIn.setQuantity(-1);
        Assertions.assertThrows(InvalidFoodInformationException.class , () -> this.menuService.updateMenu(this.menuDtoIn));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }

    @Test
    void UpdateMenuWithNullQuantityTest () {
        this.menuDtoIn.setQuantity(null);
        Assertions.assertThrows(InvalidFoodInformationException.class , () -> this.menuService.updateMenu(this.menuDtoIn));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }




    /*************************************** Price ******************************************/

    @Test
    void UpdateMenuWithTooHeightPriceTest(){
        this.menuDtoIn.setPrice(new BigDecimal(1001   ));
        Assertions.assertThrows(InvalidFoodInformationException.class , () -> this.menuService.updateMenu(this.menuDtoIn));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }
    @Test
    void UpdateMenuWithZeroPriceTest(){
        this.menuDtoIn.setPrice( new BigDecimal(0));
        Assertions.assertThrows(InvalidFoodInformationException.class , () ->this.menuService.updateMenu(this.menuDtoIn));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }
    @Test

    void UpdateMenuWithNegativePriceTest(){
        this.menuDtoIn.setPrice(new BigDecimal("-1"));
        Assertions.assertThrows(InvalidFoodInformationException.class , () -> this.menuService.updateMenu(this.menuDtoIn));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }

    @Test
    void UpdateMenuWithNullPriceTest () {
        this.menuDtoIn.setPrice(null);
        Assertions.assertThrows(InvalidFoodInformationException.class , () -> this.menuService.updateMenu(this.menuDtoIn));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }







    /*************************************** Description ******************************************/
    @Test
    void UpdateMenuWithTooShortDescriptionTest (){
        this.menuDtoIn.setDescription("abad");
        Assertions.assertThrows(InvalidFoodInformationException.class , () -> this.menuService.updateMenu(this.menuDtoIn));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }
    @Test
    void UpdateMenuWithTooLongDescriptionTest(){
        this.menuDtoIn.setLabel("a".repeat(1701));
        Assertions.assertThrows(InvalidFoodInformationException.class , () -> this.menuService.updateMenu(this.menuDtoIn));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }
    @Test
    void UpdateMenuWithEmptyDescriptionTest(){
        this.menuDtoIn.setDescription("");
        Assertions.assertThrows(InvalidFoodInformationException.class , () -> this.menuService.updateMenu(this.menuDtoIn));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }

    @Test
    void UpdateMenuWithNullDescriptionTest () {
        this.menuDtoIn.setDescription(null);
        Assertions.assertThrows(InvalidFoodInformationException.class , () -> this.menuService.updateMenu(this.menuDtoIn));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }






    /****************************************** label ********************************************/
    @Test
    void UpdateMenuWithTooShortLabelTest (){
        this.menuDtoIn.setLabel("ab");
        Assertions.assertThrows(InvalidFoodInformationException.class , () -> this.menuService.updateMenu(this.menuDtoIn));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }
    @Test
    void UpdateMenuWithTooLongLabelTest (){
        this.menuDtoIn.setLabel("a".repeat(101));
        Assertions.assertThrows(InvalidFoodInformationException.class , () -> this.menuService.updateMenu(this.menuDtoIn));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }
    @Test
    void UpdateMenuWithEmptyLabelTest(){
        this.menuDtoIn.setLabel("");
        Assertions.assertThrows(InvalidFoodInformationException.class , () -> this.menuService.updateMenu(this.menuDtoIn));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }

    @Test
    void UpdateMenuWithNullLabelTest () {
        this.menuDtoIn.setLabel(null);
        Assertions.assertThrows(InvalidFoodInformationException.class , () -> this.menuService.updateMenu(this.menuDtoIn));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());
    }


    /************************************ Menu ID************************************/

    @Test
    void  UpdateMenuByUuIdWithShortUuidTest() {
        this.menuDtoIn.setUuid("erferferfe");
        Assertions.assertThrows(InvalidFoodInformationException.class, () -> this.menuService.updateMenu(this.menuDtoIn));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());
    }
    @Test
    void  UpdateMenuByUuIdWithEmptyUuidTest() {
        this.menuDtoIn.setUuid("");
        Assertions.assertThrows(InvalidFoodInformationException.class, () -> this.menuService.updateMenu(this.menuDtoIn));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());
    }
    @Test
    void UpdateMenuByUuIdWithNullUuidTest()  {
        this.menuDtoIn.setUuid(null);
        Assertions.assertThrows(InvalidFoodInformationException.class, () -> menuService.updateMenu(this.menuDtoIn));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void UpdateMenuByUuIdWithNullMenuDtoInTest()  {
        Assertions.assertThrows(InvalidFoodInformationException.class, () -> menuService.updateMenu(null));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());
    }

}
