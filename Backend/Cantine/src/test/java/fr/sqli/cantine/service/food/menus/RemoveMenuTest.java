package fr.sqli.cantine.service.food.menus;


import fr.sqli.cantine.dao.IMenuDao;
import fr.sqli.cantine.dto.in.food.MenuDtoIn;
import fr.sqli.cantine.entity.ImageEntity;
import fr.sqli.cantine.entity.MealEntity;
import fr.sqli.cantine.entity.MenuEntity;
import fr.sqli.cantine.service.food.exceptions.FoodNotFoundException;
import fr.sqli.cantine.service.food.exceptions.InvalidFoodInformationException;
import fr.sqli.cantine.service.food.exceptions.RemoveFoodException;
import fr.sqli.cantine.service.food.impl.MealService;
import fr.sqli.cantine.service.food.impl.MenuService;
import fr.sqli.cantine.service.images.IImageService;
import fr.sqli.cantine.service.images.exception.ImagePathException;
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

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;


@ExtendWith(MockitoExtension.class)
public class RemoveMenuTest {
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

    private MenuDtoIn mealDtoIn;
    private MealEntity mealEntity;
    private MenuEntity menuEntity;


    @BeforeEach
    void setUp() throws IOException {/*
        environment = new MockEnvironment();
        environment.setProperty("sqli.cantine.images.url.menu", "http://localhost:8080/cantine/download/images/menus/");
        environment.setProperty("sqli.cantine.images.menus.path", "images/menus");
        this.menuService = new MenuService( environment, iMealService, imageService, iMenuDao);

        this.mealDtoIn =  new MenuDtoIn();
        this.mealDtoIn.setLabel("label test");
        this.mealDtoIn.setDescription("description  test");
        this.mealDtoIn.setQuantity(10);
        this.mealDtoIn.setStatus(1);
        this.mealDtoIn.setPrice(new BigDecimal(1.5));
        this.mealDtoIn.setImage( new MockMultipartFile(
                "image",                         // nom du champ de fichier
                "ImageMenuForTest.jpg",          // nom du fichier
                "image/jpg",                    // type MIME
                new FileInputStream("imagesTests/ImageForTest.jpg")));
        this.mealDtoIn.setMealUuids(Collections.singletonList("1"));*/
        environment.setProperty("sqli.cantine.images.url.menus", "http://localhost:8080/cantine/download/images/menus/");
        environment.setProperty("sqli.cantine.images.menus.path", "images/menus");
         this.menuEntity = new MenuEntity();
         this.menuEntity.setId(java.util.UUID.randomUUID().toString());
        this.menuEntity.setLabel("label test");
        this.menuEntity.setDescription("description  test");
        this.menuEntity.setQuantity(10);
        this.menuEntity.setStatus(1);
        this.menuEntity.setPrice(new BigDecimal("1.5"));
        ImageEntity image =  new ImageEntity();
        image.setName("ImageMenuForTest.jpg");
        this.menuEntity.setImage(image);
        this.menuEntity.setMeals(Collections.singletonList(new MealEntity()));
    }

    @AfterEach
    void tearDown() {
        this.mealDtoIn = null;
        this.mealEntity = null;
        this.menuEntity = null;
    }

    @Test  /*TODO :  inject  the object  directly  in  the  method  and  not  in  the  constructor*/
    void  removeMenuTest() throws ImagePathException, RemoveFoodException, InvalidFoodInformationException, FoodNotFoundException {
        Mockito.when(this.iMenuDao.findMenuById(this.menuEntity.getId())).thenReturn(Optional.of(this.menuEntity));

        Mockito.doNothing().when(this.imageService).deleteImage(this.menuEntity.getImage().getName(), null);
        Mockito.doNothing().when(this.iMenuDao).delete(this.menuEntity);

       var  result = this.menuService.removeMenu(this.menuEntity.getId());

        Assertions.assertEquals(this.menuEntity, result);
        Mockito.verify(this.imageService, Mockito.times(1)).deleteImage(this.menuEntity.getImage().getName(), null);
        Mockito.verify(this.iMenuDao, Mockito.times(1)).findMenuById(this.menuEntity.getId());
        Mockito.verify(this.iMenuDao, Mockito.times(1)).delete(this.menuEntity);
    }

    @Test
    void  removeMenuWithMenuDoesNotExist(){

        Mockito.when(this.iMenuDao.findMenuById(this.menuEntity.getId())).thenReturn(Optional.empty());

        Assertions.assertThrows(FoodNotFoundException.class, () -> {
            this.menuService.removeMenu(this.menuEntity.getId());
        });
        Mockito.verify(this.iMenuDao, Mockito.times(1)).findMenuById(this.menuEntity.getId());
       Mockito.verify(this.iMenuDao, Mockito.times(0)).delete(Mockito.any());
    }


    @Test
    void removeMenuWithShortUuid(){
        Assertions.assertThrows(InvalidFoodInformationException.class, () -> {
            this.menuService.removeMenu("-zezedzedaaedae");
        });
        Mockito.verify(this.iMenuDao, Mockito.times(0)).findMenuById(Mockito.any());
        Mockito.verify(this.iMenuDao, Mockito.times(0)).delete(Mockito.any());
    }
    @Test
    void removeMenuWithEmptyUuID(){
        Assertions.assertThrows(InvalidFoodInformationException.class, () -> {
            this.menuService.removeMenu("    ");
        });
        Mockito.verify(this.iMenuDao, Mockito.times(0)).findMenuById(Mockito.any());
        Mockito.verify(this.iMenuDao, Mockito.times(0)).delete(Mockito.any());
    }
    @Test
    void removeMenuWithNullUuID(){
        Assertions.assertThrows(InvalidFoodInformationException.class, () -> {
            this.menuService.removeMenu(null);
        });
        Mockito.verify(this.iMenuDao, Mockito.times(0)).findMenuById(Mockito.any());
        Mockito.verify(this.iMenuDao, Mockito.times(0)).delete(Mockito.any());
    }

}
