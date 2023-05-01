package fr.sqli.Cantine.service.admin.menus;


import fr.sqli.Cantine.dao.IMenuDao;
import fr.sqli.Cantine.dto.in.MealDtoIn;
import fr.sqli.Cantine.dto.in.MenuDtoIn;
import fr.sqli.Cantine.entity.MealEntity;
import fr.sqli.Cantine.entity.MenuEntity;
import fr.sqli.Cantine.service.admin.meals.MealService;
import fr.sqli.Cantine.service.admin.menus.exceptions.InvalidMenuInformationException;
import fr.sqli.Cantine.service.images.IImageService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.mock.web.MockMultipartFile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;


@ExtendWith(MockitoExtension.class)
public class RemoveMealTest {
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
    void setUp() throws IOException {
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
                new FileInputStream("images/menus/ImageMenuForTest.jpg")));
        this.mealDtoIn.setMealIDs(Collections.singletonList(1));
    }
    @Test
    void removeMenuWithInvalidID2(){
        Assertions.assertThrows(InvalidMenuInformationException.class, () -> {
            this.menuService.removeMenu(-10);
        });
    }
    @Test
    void removeMenuWithInvalidID(){
        Assertions.assertThrows(InvalidMenuInformationException.class, () -> {
            this.menuService.removeMenu(-1);
        });
    }
    @Test
    void removeMenuWithNullID(){
        Assertions.assertThrows(InvalidMenuInformationException.class, () -> {
            this.menuService.removeMenu(null);
        });
    }
}
