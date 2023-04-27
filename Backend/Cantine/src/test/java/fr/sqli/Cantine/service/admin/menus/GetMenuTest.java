package fr.sqli.Cantine.service.admin.menus;

import fr.sqli.Cantine.dao.IMenuDao;
import fr.sqli.Cantine.entity.ImageEntity;
import fr.sqli.Cantine.entity.MealEntity;
import fr.sqli.Cantine.entity.MenuEntity;
import fr.sqli.Cantine.service.admin.meals.IMealService;
import fr.sqli.Cantine.service.admin.meals.MealService;
import fr.sqli.Cantine.service.admin.meals.exceptions.MealNotFoundAdminException;
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

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class GetMenuTest {
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


    @BeforeEach
    void setUp() {
        this.environment = new MockEnvironment();
        this.environment.setProperty("sqli.cantine.images.url.menus", "http://localhost:8080/images/menus/");
        this.environment.setProperty("sqli.cantine.images.menus.path", "images/menus");
        this.menuService = new MenuService(environment, iMealService, imageService, iMenuDao);


        this.mealEntity = new MealEntity();  //  a meal
        this.mealEntity.setId(1);
        this.mealEntity.setStatus(1);
        this.mealEntity.setPrice(BigDecimal.valueOf(1.3));
        this.mealEntity.setQuantity(1);
        this.mealEntity.setCategory("Frites");
        this.mealEntity.setDescription("first Meal To  Test");
        this.mealEntity.setLabel("Meal 1");
        this.mealEntity.setImage(new ImageEntity());


        this.menuEntity = new MenuEntity(); // a menu
        this.menuEntity.setId(1);
        this.menuEntity.setStatus(1);
        this.menuEntity.setPrice(BigDecimal.valueOf(1.3));
        this.menuEntity.setQuantity(1);
        this.menuEntity.setDescription("first Menu To  Test");
        this.menuEntity.setLabel("Menu 1");
        this.menuEntity.setMeals(Collections.singletonList(mealEntity));
        this.menuEntity.setImage(new ImageEntity());

        ;
    }



    @Test
    void getMenuByIdWithNullIdTest() throws InvalidMenuInformationException, MealNotFoundAdminException {
        Assertions.assertThrows(InvalidMenuInformationException.class, () -> menuService.getMenuById(null));
    }


     /******************************** getAllMenus()  ********************************/
    @Test
    void getAllMenusWithTwoMenuTest() {

        var menuEntity2 = new MenuEntity(); // a menu
        menuEntity2.setId(1);
        menuEntity2.setStatus(1);
        menuEntity2.setPrice(BigDecimal.valueOf(1.3));
        menuEntity2.setQuantity(1);
        menuEntity2.setDescription("first Menu To  Test");
        menuEntity2.setLabel("Menu 1");
        menuEntity2.setMeals(Collections.singletonList(mealEntity));
        menuEntity2.setImage(new ImageEntity());

        Mockito.when(iMenuDao.findAll()).thenReturn(List.of(this.menuEntity, menuEntity2));

        var result = menuService.getAllMenus();
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(result.get(0).getDescription(), this.menuEntity.getDescription());

        Mockito.verify(iMenuDao, Mockito.times(1)).findAll();
    }


    @Test
    void getAllMenusWithOneMenuTest() {

        Mockito.when(iMenuDao.findAll()).thenReturn(List.of(this.menuEntity));

        var result = menuService.getAllMenus();
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(result.get(0).getDescription(), this.menuEntity.getDescription());
        Assertions.assertEquals(result.get(0).getQuantity(), this.menuEntity.getQuantity());
        Mockito.verify(iMenuDao, Mockito.times(1)).findAll();
    }

    @Test
    void getAllMenusWithEmptyMenuTest() {

        Mockito.when(iMenuDao.findAll()).thenReturn(Collections.emptyList());

        var result = menuService.getAllMenus();
        Assertions.assertEquals(0, result.size());
        Mockito.verify(iMenuDao, Mockito.times(1)).findAll();
    }


}//  end class












