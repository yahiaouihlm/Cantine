package fr.sqli.Cantine.service.admin.menus;

import fr.sqli.Cantine.dao.IMenuDao;
import fr.sqli.Cantine.dto.in.MenuDtoIn;
import fr.sqli.Cantine.entity.MealEntity;
import fr.sqli.Cantine.entity.MenuEntity;
import fr.sqli.Cantine.service.admin.meals.MealService;
import fr.sqli.Cantine.service.admin.meals.exceptions.MealNotFoundAdminException;
import fr.sqli.Cantine.service.admin.menus.exceptions.ExistingMenuException;
import fr.sqli.Cantine.service.admin.menus.exceptions.InvalidMenuInformationException;
import fr.sqli.Cantine.service.admin.menus.exceptions.MenuNotFoundException;
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
                new FileInputStream("images/menus/ImageMenuForTest.jpg")));
        this.menu.setMealIDs(Collections.singletonList("1"));

    }
    @Test
    void  updateMenuWithExistingTest () {
        this.menuEntity = new MenuEntity();
        Mockito.when(iMenuDao.findById(1)).thenReturn(Optional.of(this.menuEntity));

        Mockito.when(iMenuDao.findByLabelAndAndPriceAndDescriptionIgnoreCase(this.menu.getLabel().replaceAll("\\s+", ""), this.menu.getDescription(),  this.menu.getPrice()))
                  .thenReturn(Optional.of(this.menuEntity));

        Assertions.assertThrows(ExistingMenuException.class , () -> this.menuService.updateMenu(this.menu, 1 ));

        Mockito.verify(iMenuDao, Mockito.times(1)).findByLabelAndAndPriceAndDescriptionIgnoreCase(this.menu.getLabel(), this.menu.getDescription(),  this.menu.getPrice());
        Mockito.verify(iMenuDao, Mockito.times(1)).findById(1);
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());
    }
    @Test
    void  updateMenuWithMenuNotFoundTest () {
        Mockito.when(iMenuDao.findById(1)).thenReturn(Optional.empty());
        Assertions.assertThrows(MenuNotFoundException.class , () -> this.menuService.updateMenu(this.menu, 1 ));
        Mockito.verify(iMenuDao, Mockito.times(1)).findById(1);
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());
    }




   /*************************************** status  ******************************************/



    @Test
    void UpdateMenuWithInvalidStatusTest(){
        this.menu.setStatus(2);
        Assertions.assertThrows(InvalidMenuInformationException.class , () ->this.menuService.updateMenu(this.menu, 1 ));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }

    @Test
    void UpdateMenuWithNegativeStatusTest(){
        this.menu.setQuantity(-1);
        Assertions.assertThrows(InvalidMenuInformationException.class , () -> this.menuService.updateMenu(this.menu, 1 ));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }

    @Test
    void UpdateMenuWithNullStatusTest () {
        this.menu.setStatus(null);
        Assertions.assertThrows(InvalidMenuInformationException.class, () -> this.menuService.updateMenu(this.menu, 1 ));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }


    /*************************************** Quantiy  ******************************************/


    @Test
    void UpdateMenuWithTooHeightQuantityTest(){
        this.menu.setQuantity(Integer.MAX_VALUE);
        Assertions.assertThrows(InvalidMenuInformationException.class , () -> this.menuService.updateMenu(this.menu, 1 ));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }

    @Test
    void UpdateMenuWithNegativeQuantityTest(){
        this.menu.setQuantity(-1);
        Assertions.assertThrows(InvalidMenuInformationException.class , () -> this.menuService.updateMenu(this.menu, 1 ));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }

    @Test
    void UpdateMenuWithNullQuantityTest () {
        this.menu.setQuantity(null);
        Assertions.assertThrows(InvalidMenuInformationException.class , () -> this.menuService.updateMenu(this.menu, 1 ));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }




    /*************************************** Price ******************************************/

    @Test
    void UpdateMenuWithTooHeightPriceTest(){
        this.menu.setPrice(new BigDecimal(1001   ));
        Assertions.assertThrows(InvalidMenuInformationException.class , () -> this.menuService.updateMenu(this.menu, 1 ));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }
    @Test
    void UpdateMenuWithZeroPriceTest(){
        this.menu.setPrice( new BigDecimal(0));
        Assertions.assertThrows(InvalidMenuInformationException.class , () ->this.menuService.updateMenu(this.menu, 1 ));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }
    @Test

    void UpdateMenuWithNegativePriceTest(){
        this.menu.setPrice(new BigDecimal("-1"));
        Assertions.assertThrows(InvalidMenuInformationException.class , () -> this.menuService.updateMenu(this.menu, 1 ));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }

    @Test
    void UpdateMenuWithNullPriceTest () {
        this.menu.setPrice(null);
        Assertions.assertThrows(InvalidMenuInformationException.class , () -> this.menuService.updateMenu(this.menu, 1 ));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }







    /*************************************** Description ******************************************/
    @Test
    void UpdateMenuWithTooShortDescriptionTest (){
        this.menu.setDescription("abad");
        Assertions.assertThrows(InvalidMenuInformationException.class , () -> this.menuService.updateMenu(this.menu, 1 ));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }
    @Test
    void UpdateMenuWithTooLongDescriptionTest(){
        this.menu.setLabel("a".repeat(1701));
        Assertions.assertThrows(InvalidMenuInformationException.class , () -> this.menuService.updateMenu(this.menu, 1 ));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }
    @Test
    void UpdateMenuWithEmptyDescriptionTest(){
        this.menu.setDescription("");
        Assertions.assertThrows(InvalidMenuInformationException.class , () -> this.menuService.updateMenu(this.menu, 1 ));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }

    @Test
    void UpdateMenuWithNullDescriptionTest () {
        this.menu.setDescription(null);
        Assertions.assertThrows(InvalidMenuInformationException.class , () -> this.menuService.updateMenu(this.menu, 1 ));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }






    /****************************************** label ********************************************/
    @Test
    void UpdateMenuWithTooShortLabelTest (){
        this.menu.setLabel("ab");
        Assertions.assertThrows(InvalidMenuInformationException.class , () -> this.menuService.updateMenu(this.menu, 1 ));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }
    @Test
    void UpdateMenuWithTooLongLabelTest (){
        this.menu.setLabel("a".repeat(101));
        Assertions.assertThrows(InvalidMenuInformationException.class , () -> this.menuService.updateMenu(this.menu, 1 ));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }
    @Test
    void UpdateMenuWithEmptyLabelTest(){
        this.menu.setLabel("");
        Assertions.assertThrows(InvalidMenuInformationException.class , () -> this.menuService.updateMenu(this.menu , 1 ));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }

    @Test
    void UpdateMenuWithNullLabelTest () {
        this.menu.setLabel(null);
        Assertions.assertThrows(InvalidMenuInformationException.class , () -> this.menuService.updateMenu(this.menu, 1 ));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());
    }


    /************************************ Menu ID************************************/

    @Test
    void  UpdateMenuByIdWithNegativeIdTest() throws InvalidMenuInformationException, MealNotFoundAdminException {
        Assertions.assertThrows(InvalidMenuInformationException.class, () -> this.menuService.updateMenu(this.menu,-1));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());
    }
    @Test
    void UpdateMenuByIdWithNullIdTest()  {
        Assertions.assertThrows(InvalidMenuInformationException.class, () -> menuService.updateMenu(this.menu,-1));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());
    }

}
