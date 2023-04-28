package fr.sqli.Cantine.service.admin.menus;

import fr.sqli.Cantine.dao.IMenuDao;
import fr.sqli.Cantine.dto.in.MenuDtoIn;
import fr.sqli.Cantine.entity.MealEntity;
import fr.sqli.Cantine.entity.MenuEntity;
import fr.sqli.Cantine.service.admin.meals.MealService;
import fr.sqli.Cantine.service.admin.menus.exceptions.InvalidMenuInformationException;
import fr.sqli.Cantine.service.images.IImageService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
                "ImageMealForTest.jpg",          // nom du fichier
                "image/jpg",                    // type MIME
                new FileInputStream("images/meals/ImageMealForTest.jpg")));
        this.menu.setMealIDs(Collections.singletonList(1));

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
    @Disabled
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
