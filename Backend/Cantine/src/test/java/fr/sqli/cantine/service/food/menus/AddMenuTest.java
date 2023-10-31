package fr.sqli.cantine.service.food.menus;

import fr.sqli.cantine.dao.IMealDao;
import fr.sqli.cantine.dao.IMenuDao;
import fr.sqli.cantine.dto.in.food.MenuDtoIn;
import fr.sqli.cantine.entity.MealEntity;
import fr.sqli.cantine.entity.MenuEntity;
import fr.sqli.cantine.service.food.impl.MenuService;
import fr.sqli.cantine.service.food.IMealService;
import fr.sqli.cantine.service.food.impl.MealService;
import fr.sqli.cantine.service.food.menus.exceptions.ExistingMenuException;
import fr.sqli.cantine.service.food.menus.exceptions.InvalidMenuInformationException;
import fr.sqli.cantine.service.food.exceptions.UnavailableFoodException;
import fr.sqli.cantine.service.images.IImageService;
import org.junit.jupiter.api.AfterEach;
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
public class AddMenuTest {
    @Mock
    private IMenuDao iMenuDao;

    @Mock
    private MealService mealService;

    @Mock
    IImageService imageService;

    @InjectMocks
    private MenuService menuService;

    @Mock
    private  MockEnvironment environment;
    @Mock
    private IMealDao iMealDao;

    private MenuEntity menuEntity;

    private MenuDtoIn menu  ;


    @Mock
    private IMealService iMealService;

    @BeforeEach
    void  init () throws IOException {
            this.menu =  new MenuDtoIn();
        this.menu.setLabel("label test");
        this.menu.setDescription("description  test");
        this.menu.setQuantity(10);
        this.menu.setStatus(1);
        this.menu.setPrice(new BigDecimal(1.5));
        this.menu.setImage( new MockMultipartFile(
                "image",                         // nom du champ de fichier
                "ImageMenuForTest.jpg",          // nom du fichier
                "image/jpg",                    // type MIME
                new FileInputStream("imagesTests/ImageForTest.jpg")));
        this.menu.setMealUuids(Collections.singletonList("1"));

    }
   @AfterEach
   void  end () {
            this.menu = null;
        this.menuEntity = null;
    }
    @Test
    void AddMenuWithUnalienableMeal() throws  Exception{

        MealEntity meal = new MealEntity();
        meal.setLabel("label test");
        meal.setId(1);
        meal.setStatus(0);

        Mockito.when(iMenuDao.findByLabelAndAndPriceAndDescriptionIgnoreCase(this.menu.getLabel().trim(), this.menu.getDescription(), this.menu.getPrice())).thenReturn(Optional.empty());
        //Mockito.when(this.iMealDao.findById(1)).thenReturn(Optional.of(meal));
        /*TODO : modifier le  test pour qu'il passe */
      Mockito.when(this.iMealService.getMealEntityByUUID(java.util.UUID.randomUUID().toString())).thenReturn(meal);

        Assertions.assertThrows(UnavailableFoodException.class , () -> this.menuService.addMenu(this.menu));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void AddMenuWithExistingMenu() {
        Mockito.when(iMenuDao.findByLabelAndAndPriceAndDescriptionIgnoreCase(this.menu.getLabel().trim(), this.menu.getDescription(), this.menu.getPrice())).thenReturn(Optional.of(new MenuEntity()));

        Assertions.assertThrows(ExistingMenuException.class , () -> this.menuService.addMenu(this.menu));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());
    }
    @Test
    void AddMeuWithoutMeals () {
        this.menu.setMealUuids(null);
        Assertions.assertThrows(InvalidMenuInformationException.class , () -> this.menuService.addMenu(this.menu));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());
    }

    /*************************************** Image  ******************************************/
    @Test
    void AddMenuWithNullImageTest() throws IOException {
        this.menu.setImage(null);
        Assertions.assertThrows(InvalidMenuInformationException.class , () -> this.menuService.addMenu(this.menu));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());
    }

    /*************************************** Status   ******************************************/

    @Test
    void AddMenuWithInvalidStatusTest(){
        this.menu.setStatus(2);
        Assertions.assertThrows(InvalidMenuInformationException.class , () -> this.menuService.addMenu(this.menu));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }

    @Test
    void AddMenuWithNegativeStatusTest(){
        this.menu.setQuantity(-1);
        Assertions.assertThrows(InvalidMenuInformationException.class , () -> this.menuService.addMenu(this.menu));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }

    @Test
    void AddMenuWithNullStatusTest () {
        this.menu.setStatus(null);
        Assertions.assertThrows(InvalidMenuInformationException.class, () -> this.menuService.addMenu(this.menu));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }


        /*************************************** Quantiy  ******************************************/


    @Test
    void AddMenuWithTooHeightQuantityTest(){
        this.menu.setQuantity(Integer.MAX_VALUE);
        Assertions.assertThrows(InvalidMenuInformationException.class , () -> this.menuService.addMenu(this.menu));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }

    @Test
    void AddMenuWithNegativeQuantityTest(){
        this.menu.setQuantity(-1);
        Assertions.assertThrows(InvalidMenuInformationException.class , () -> this.menuService.addMenu(this.menu));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }

    @Test
    void AddMenuWithNullQuantityTest () {
        this.menu.setQuantity(null);
        Assertions.assertThrows(InvalidMenuInformationException.class , () -> this.menuService.addMenu(this.menu));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }




    /*************************************** Price ******************************************/

    @Test
    void AddMenuWithTooHeightPriceTest(){
        this.menu.setPrice(new BigDecimal(1001   ));
        Assertions.assertThrows(InvalidMenuInformationException.class , () -> this.menuService.addMenu(this.menu));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }
    @Test
    void AddMenuWithZeroPriceTest(){
        this.menu.setPrice( new BigDecimal(0));
        Assertions.assertThrows(InvalidMenuInformationException.class , () -> this.menuService.addMenu(this.menu));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }
    @Test

    void AddMenuWithNegativePriceTest(){
        this.menu.setPrice(new BigDecimal("-1"));
        Assertions.assertThrows(InvalidMenuInformationException.class , () -> this.menuService.addMenu(this.menu));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }

    @Test
    void AddMenuWithNullPriceTest () {
        this.menu.setPrice(null);
        Assertions.assertThrows(InvalidMenuInformationException.class , () -> this.menuService.addMenu(this.menu));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }







  /*************************************** Description ******************************************/
  @Test
  void AddMenuWithTooShortDescriptionTest (){
      this.menu.setDescription("abad");
      Assertions.assertThrows(InvalidMenuInformationException.class , () -> this.menuService.addMenu(this.menu));
      Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

  }
    @Test
    void AddMenuWithTooLongDescriptionTest(){
        this.menu.setLabel("a".repeat(1701));
        Assertions.assertThrows(InvalidMenuInformationException.class , () -> this.menuService.addMenu(this.menu));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }
    @Test
    void AddMenuWithEmptyDescriptionTest(){
        this.menu.setDescription("");
        Assertions.assertThrows(InvalidMenuInformationException.class , () -> this.menuService.addMenu(this.menu));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }

    @Test
    void AddMenuWithNullDescriptionTest () {
        this.menu.setDescription(null);
        Assertions.assertThrows(InvalidMenuInformationException.class , () -> this.menuService.addMenu(this.menu));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }






  /****************************************** label ********************************************/
  @Test
  void AddMenuWithTooShortLabelTest (){
      this.menu.setLabel("ab");
      Assertions.assertThrows(InvalidMenuInformationException.class , () -> this.menuService.addMenu(this.menu));
      Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

  }
  @Test
  void AddMenuWithTooLongLabelTest (){
      this.menu.setLabel("a".repeat(101));
      Assertions.assertThrows(InvalidMenuInformationException.class , () -> this.menuService.addMenu(this.menu));
      Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

  }
  @Test
  void AddMenuWithEmptyLabelTest(){
      this.menu.setLabel("");
      Assertions.assertThrows(InvalidMenuInformationException.class , () -> this.menuService.addMenu(this.menu));
      Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

  }

   @Test
    void AddMenuWithNullLabelTest () {
       this.menu.setLabel(null);
       Assertions.assertThrows(InvalidMenuInformationException.class , () -> this.menuService.addMenu(this.menu));
       Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());
   }

}
