package fr.sqli.Cantine.service.admin.menus;

import fr.sqli.Cantine.dao.IMenuDao;
import fr.sqli.Cantine.dto.in.food.MenuDtoIn;
import fr.sqli.Cantine.entity.MealEntity;
import fr.sqli.Cantine.entity.MenuEntity;
import fr.sqli.Cantine.service.admin.meals.MealService;
import fr.sqli.Cantine.service.admin.menus.exceptions.ExistingMenuException;
import fr.sqli.Cantine.service.admin.menus.exceptions.InvalidMenuInformationException;
import fr.sqli.Cantine.service.images.IImageService;
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
    private MealService iMealService;
    @Mock
    IImageService imageService;

    @InjectMocks
    private MenuService menuService;

    @Mock
    MockEnvironment environment;
    private MealEntity mealEntity;

    private MenuEntity menuEntity;

    private MenuDtoIn menu  ;

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
        this.menu.setMealIDs(Collections.singletonList("1"));

    }

    @Test
    void AddMenuWithExistingMenu() {
        Mockito.when(iMenuDao.findByLabelAndAndPriceAndDescriptionIgnoreCase(this.menu.getLabel().trim(), this.menu.getDescription(), this.menu.getPrice())).thenReturn(Optional.of(new MenuEntity()));

        Assertions.assertThrows(ExistingMenuException.class , () -> this.menuService.addMenu(this.menu));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());
    }
    @Test
    void AddMeuWithoutMeals () {
        this.menu.setMealIDs(null);
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
