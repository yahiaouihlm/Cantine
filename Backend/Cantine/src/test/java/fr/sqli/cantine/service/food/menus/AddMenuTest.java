package fr.sqli.cantine.service.food.menus;


import fr.sqli.cantine.dao.IMenuDao;
import fr.sqli.cantine.dto.in.food.MenuDtoIn;
import fr.sqli.cantine.entity.MealEntity;
import fr.sqli.cantine.entity.MenuEntity;
import fr.sqli.cantine.service.food.exceptions.ExistingFoodException;
import fr.sqli.cantine.service.food.exceptions.FoodNotFoundException;
import fr.sqli.cantine.service.food.exceptions.InvalidFoodInformationException;
import fr.sqli.cantine.service.food.impl.MenuService;
import fr.sqli.cantine.service.food.IMealService;
import fr.sqli.cantine.service.food.exceptions.UnavailableFoodException;
import fr.sqli.cantine.service.images.IImageService;
import fr.sqli.cantine.service.images.exception.ImagePathException;
import fr.sqli.cantine.service.images.exception.InvalidFormatImageException;
import fr.sqli.cantine.service.images.exception.InvalidImageException;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class AddMenuTest {
    @Mock
    private IMenuDao iMenuDao;


    @Mock
    IImageService imageService;

    @InjectMocks
    private MenuService menuService;

    private MenuDtoIn menuDtoIn;

    @Mock
    private MockEnvironment env;
    @Mock
    private IMealService iMealService;


    @BeforeEach
    void init() throws IOException {
        this.menuDtoIn = new MenuDtoIn();
        this.menuDtoIn.setLabel("label test");
        this.menuDtoIn.setDescription("description  test");
        this.menuDtoIn.setQuantity(10);
        this.menuDtoIn.setStatus(1);
        this.menuDtoIn.setPrice(BigDecimal.valueOf(1.5));
        this.menuDtoIn.setImage(new MockMultipartFile(
                "image",                         // nom du champ de fichier
                "ImageMenuForTest.jpg",          // nom du fichier
                "image/jpg",                    // type MIME
                new FileInputStream("imagesTests/ImageForTest.jpg")));
        env = new MockEnvironment();
        env.setProperty("sqli.cantine.images.url.menus", "http://localhost:8080/cantine/download/images/menus/");
        env.setProperty("sqli.cantine.images.menus.path", "images/menus");
    }

    @AfterEach
    void clean() {
        this.menuDtoIn = null;

        this.iMealService = null;
    }


    @Test
    void addMenuWithFewMealsTest() throws InvalidFoodInformationException, FoodNotFoundException {
        this.menuDtoIn.setId(java.util.UUID.randomUUID().toString());
        String mealUuid = java.util.UUID.randomUUID().toString();
        MealEntity mealEntity = new MealEntity();
        mealEntity.setUuid(mealUuid);
        mealEntity.setStatus(1);

        this.menuDtoIn.setListOfMealsAsString(" [\"" + mealUuid + "\" ] ");

        Mockito.when(this.iMenuDao.findByLabelAndAndPriceAndDescriptionIgnoreCase(this.menuDtoIn.getLabel().trim(), this.menuDtoIn.getDescription(), this.menuDtoIn.getPrice()))
                .thenReturn(Optional.empty());

        Mockito.when(this.iMealService.getMealEntityByUUID(mealUuid)).thenReturn(mealEntity);


        Assertions.assertThrows(InvalidFoodInformationException.class, () -> this.menuService.addMenu(this.menuDtoIn));
    }

    @Test
    void addMenuWithExistingMenu() {
        // allow the  meal parsing  by the service
        this.menuDtoIn.setListOfMealsAsString(" [\"" + java.util.UUID.randomUUID() + "\" ] ");
        Mockito.when(iMenuDao.findByLabelAndAndPriceAndDescriptionIgnoreCase(this.menuDtoIn.getLabel().trim(), this.menuDtoIn.getDescription(), this.menuDtoIn.getPrice())).thenReturn(Optional.of(new MenuEntity()));
        Assertions.assertThrows(ExistingFoodException.class, () -> this.menuService.addMenu(this.menuDtoIn));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }


    @Test
    void AddMenuWithUnalienableMeal() throws Exception {
        String uuid = java.util.UUID.randomUUID().toString();
        MealEntity meal = new MealEntity();
        meal.setUuid(uuid);
        meal.setLabel("label test");
        meal.setId(1);
        meal.setStatus(0);
        // allow the  meal parsing  by the service
        this.menuDtoIn.setListOfMealsAsString(" [\"" + uuid + "\" ] ");

        Mockito.when(iMenuDao.findByLabelAndAndPriceAndDescriptionIgnoreCase(this.menuDtoIn.getLabel().trim(), this.menuDtoIn.getDescription(), this.menuDtoIn.getPrice())).thenReturn(Optional.empty());

        Mockito.when(this.iMealService.getMealEntityByUUID(uuid)).thenReturn(meal);

        Assertions.assertThrows(UnavailableFoodException.class, () -> this.menuService.addMenu(this.menuDtoIn));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());
    }


    @Test
    void AddMeuWithoutNoMeal() {
        this.menuDtoIn.setMealUuids(List.of());
        Assertions.assertThrows(InvalidFoodInformationException.class, () -> this.menuService.addMenu(this.menuDtoIn));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());
    }


    @Test
    void AddMeuWithoutNullMeals() {
        this.menuDtoIn.setMealUuids(null);
        Assertions.assertThrows(InvalidFoodInformationException.class, () -> this.menuService.addMenu(this.menuDtoIn));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());
    }

    /*************************************** Image  ******************************************/
    @Test
    void AddMenuWithNullImageTest() throws IOException, InvalidFormatImageException, InvalidImageException, ImagePathException {
        this.menuDtoIn.setImage(null);
        Assertions.assertThrows(InvalidFoodInformationException.class, () -> this.menuService.addMenu(this.menuDtoIn));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());
        Mockito.verify(imageService, Mockito.times(0)).uploadImage(Mockito.any(MultipartFile.class), Mockito.any(String.class));
    }

    /*************************************** Status   ******************************************/

    @Test
    void AddMenuWithInvalidStatusTest() {
        this.menuDtoIn.setStatus(2);
        Assertions.assertThrows(InvalidFoodInformationException.class, () -> this.menuService.addMenu(this.menuDtoIn));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }

    @Test
    void AddMenuWithNegativeStatusTest() {
        this.menuDtoIn.setStatus(-1);
        Assertions.assertThrows(InvalidFoodInformationException.class, () -> this.menuService.addMenu(this.menuDtoIn));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }

    @Test
    void AddMenuWithNullStatusTest() {
        this.menuDtoIn.setStatus(null);
        Assertions.assertThrows(InvalidFoodInformationException.class, () -> this.menuService.addMenu(this.menuDtoIn));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }


    /*************************************** Quantiy  ******************************************/


    @Test
    void AddMenuWithTooHeightQuantityTest() {
        this.menuDtoIn.setQuantity(10001);
        Assertions.assertThrows(InvalidFoodInformationException.class, () -> this.menuService.addMenu(this.menuDtoIn));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }

    @Test
    void AddMenuWithNegativeQuantityTest() {
        this.menuDtoIn.setQuantity(-1);
        Assertions.assertThrows(InvalidFoodInformationException.class, () -> this.menuService.addMenu(this.menuDtoIn));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }

    @Test
    void AddMenuWithNullQuantityTest() {
        this.menuDtoIn.setQuantity(null);
        Assertions.assertThrows(InvalidFoodInformationException.class, () -> this.menuService.addMenu(this.menuDtoIn));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }


    /*************************************** Price ******************************************/

    @Test
    void AddMenuWithTooHeightPriceTest() {
        this.menuDtoIn.setPrice(new BigDecimal(1001));
        Assertions.assertThrows(InvalidFoodInformationException.class, () -> this.menuService.addMenu(this.menuDtoIn));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }

    @Test
    void AddMenuWithZeroPriceTest() {
        this.menuDtoIn.setPrice(new BigDecimal(0));
        Assertions.assertThrows(InvalidFoodInformationException.class, () -> this.menuService.addMenu(this.menuDtoIn));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }

    @Test
    void AddMenuWithNegativePriceTest() {
        this.menuDtoIn.setPrice(new BigDecimal("-1"));
        Assertions.assertThrows(InvalidFoodInformationException.class, () -> this.menuService.addMenu(this.menuDtoIn));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }

    @Test
    void AddMenuWithNullPriceTest() {
        this.menuDtoIn.setPrice(null);
        Assertions.assertThrows(InvalidFoodInformationException.class, () -> this.menuService.addMenu(this.menuDtoIn));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }


    /*************************************** Description ******************************************/
    @Test
    void AddMenuWithTooShortDescriptionTest() {
        this.menuDtoIn.setDescription("ab" + " ".repeat(100) + "ad");
        Assertions.assertThrows(InvalidFoodInformationException.class, () -> this.menuService.addMenu(this.menuDtoIn));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }

    @Test
    void AddMenuWithTooLongDescriptionTest() {
        this.menuDtoIn.setLabel("a".repeat(1701));
        Assertions.assertThrows(InvalidFoodInformationException.class, () -> this.menuService.addMenu(this.menuDtoIn));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }

    @Test
    void AddMenuWithEmptyDescriptionTest() {
        this.menuDtoIn.setDescription("");
        Assertions.assertThrows(InvalidFoodInformationException.class, () -> this.menuService.addMenu(this.menuDtoIn));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }

    @Test
    void AddMenuWithNullDescriptionTest() {
        this.menuDtoIn.setDescription(null);
        Assertions.assertThrows(InvalidFoodInformationException.class, () -> this.menuService.addMenu(this.menuDtoIn));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }


    /****************************************** label ********************************************/
    @Test
    void AddMenuWithTooShortLabelTest() {
        this.menuDtoIn.setLabel("a" + " ".repeat(100) + "b");
        Assertions.assertThrows(InvalidFoodInformationException.class, () -> this.menuService.addMenu(this.menuDtoIn));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }

    @Test
    void AddMenuWithTooLongLabelTest() {
        this.menuDtoIn.setLabel("a".repeat(101));
        Assertions.assertThrows(InvalidFoodInformationException.class, () -> this.menuService.addMenu(this.menuDtoIn));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }

    @Test
    void AddMenuWithEmptyLabelTest() {
        this.menuDtoIn.setLabel("");
        Assertions.assertThrows(InvalidFoodInformationException.class, () -> this.menuService.addMenu(this.menuDtoIn));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());

    }

    @Test
    void AddMenuWithNullLabelTest() {
        this.menuDtoIn.setLabel(null);
        Assertions.assertThrows(InvalidFoodInformationException.class, () -> this.menuService.addMenu(this.menuDtoIn));
        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any());
    }


    @Test
    void AddMenuWithNullMenuInformation() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {
        this.menuDtoIn = new MenuDtoIn();

        Assertions.assertThrows(InvalidFoodInformationException.class, () -> menuService.addMenu(this.menuDtoIn));

        Mockito.verify(this.iMenuDao, Mockito.times(0)).save(Mockito.any(MenuEntity.class));
        Mockito.verify(imageService, Mockito.times(0)).uploadImage(this.menuDtoIn.getImage(), "images/meals");
    }

    @Test
    void AddMenuWithNullRequestData() throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {

        Assertions.assertThrows(InvalidFoodInformationException.class, () -> menuService.addMenu(null));

        Mockito.verify(iMenuDao, Mockito.times(0)).save(Mockito.any(MenuEntity.class));
        Mockito.verify(imageService, Mockito.times(0)).uploadImage(menuDtoIn.getImage(), "images/meals");
    }

}
