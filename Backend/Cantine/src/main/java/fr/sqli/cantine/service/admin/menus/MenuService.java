package fr.sqli.cantine.service.admin.menus;


import fr.sqli.cantine.dao.IMenuDao;
import fr.sqli.cantine.dto.in.food.MenuDtoIn;
import fr.sqli.cantine.dto.out.food.MenuDtout;
import fr.sqli.cantine.entity.ImageEntity;
import fr.sqli.cantine.entity.MealEntity;
import fr.sqli.cantine.entity.MenuEntity;
import fr.sqli.cantine.service.admin.meals.IMealService;
import fr.sqli.cantine.service.admin.meals.exceptions.InvalidMealInformationException;
import fr.sqli.cantine.service.admin.meals.exceptions.MealNotFoundException;
import fr.sqli.cantine.service.admin.menus.exceptions.ExistingMenuException;
import fr.sqli.cantine.service.admin.menus.exceptions.InvalidMenuInformationException;
import fr.sqli.cantine.service.admin.menus.exceptions.MenuNotFoundException;
import fr.sqli.cantine.service.admin.menus.exceptions.UnavailableMealException;
import fr.sqli.cantine.service.images.IImageService;
import fr.sqli.cantine.service.images.exception.ImagePathException;
import fr.sqli.cantine.service.images.exception.InvalidImageException;
import fr.sqli.cantine.service.images.exception.InvalidFormatImageException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MenuService implements IMenuService {

    private static final Logger LOG = LogManager.getLogger();
    private final String MENUS_IMAGES_PATH;

    private final String MENUS_IMAGES_URL;
    private final String MEALS_IMAGES_PATH;
    private final IMealService mealService;
    private final IImageService imageService;
    private final IMenuDao menuDao;

    @Autowired
    public MenuService(Environment environment, IMealService mealService, IImageService imageService, IMenuDao menuDao) {
        this.mealService = mealService;
        this.imageService = imageService;
        this.menuDao = menuDao;
        this.MENUS_IMAGES_URL = environment.getProperty("sqli.cantine.images.url.menus"); // the link  to images of menus (url used in the front end) "http://localhost:8080/images/menus/"
        this.MENUS_IMAGES_PATH = environment.getProperty("sqli.cantine.images.menus.path"); //  the path  to the images of menus directory
        this.MEALS_IMAGES_PATH = environment.getProperty("sqli.cantine.images.url.meals"); //  the path  to the images of meals directory
    }

    @Override
    public MenuEntity updateMenu(MenuDtoIn menuDtoIn) throws InvalidMenuInformationException, InvalidMealInformationException, MealNotFoundException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, MenuNotFoundException, ExistingMenuException {

        IMenuService.verifyMealInformation("THE ID CAN NOT BE NULL OR LESS THAN 0", menuDtoIn.getMenuId());
        var menu = menuDtoIn.toMenuEntityWithoutImage();
        IMenuService.ValidateMealID(menuDtoIn);


        var menuToUpdate = this.menuDao.findById(menuDtoIn.getMenuId());
        if (menuToUpdate.isEmpty()) {
            MenuService.LOG.error("NO MENU WAS FOUND WITH AN ID = {} IN THE updateMenu METHOD ", menuDtoIn.getMenuId());
            throw new MenuNotFoundException("NO MENU WAS FOUND WITH THIS ID ");
        }


        var menuUpdatedDoesExist = this.checkExistingMenu(menu.getLabel(), menu.getDescription(), menu.getPrice());

        if (menuUpdatedDoesExist.isPresent()) {
            if (menuUpdatedDoesExist.get().getId() != menuToUpdate.get().getId()) {
                MenuService.LOG.error("THE MENU ALREADY EXISTS IN THE DATABASE with label = {} , description = {} and price = {} ", menu.getLabel(), menu.getDescription(), menu.getPrice());
                throw new ExistingMenuException("THE MENU ALREADY EXISTS IN THE DATABASE");
            }
        }

        var menuEntity = menuToUpdate.get();
        menuEntity.setLabel(menu.getLabel());
        menuEntity.setDescription(menu.getDescription());
        menuEntity.setPrice(menu.getPrice());
        menuEntity.setStatus(menu.getStatus());
        menuEntity.setQuantity(menu.getQuantity());

        var  mealIDs = menuDtoIn.fromStringMealIDsToIntegerMealIDs();
        List<MealEntity> mealsInMenu = new ArrayList<>(); //  check  existing meals in the   database  and  add them to the menu
        for (Integer mealID : mealIDs ) {
            var meal = this.mealService.getMealEntityByID(mealID);
            mealsInMenu.add(meal);
        }

        menuEntity.setMeals(mealsInMenu);

        if (menuDtoIn.getImage() != null && !menuDtoIn.getImage().isEmpty()) {
            var oldImageName = menuEntity.getImage().getImagename();
            var newImageName = this.imageService.updateImage(oldImageName, menuDtoIn.getImage(), this.MENUS_IMAGES_PATH);
            menuEntity.getImage().setImagename(newImageName);
        }

        return this.menuDao.save(menuEntity);

    }

    @Override
    public MenuEntity removeMenu(Integer menuID) throws MenuNotFoundException, InvalidMenuInformationException, ImagePathException {

        IMenuService.verifyMealInformation("THE ID CAN NOT BE NULL OR LESS THAN 0", menuID);

        var menu = this.menuDao.findById(menuID);

        if (menu.isEmpty()) {
            MenuService.LOG.error("NO MENU WAS FOUND WITH AN ID = {} IN THE removeMenu METHOD ", menuID);
            throw new MenuNotFoundException("NO MENU WAS FOUND WITH THIS ID ");
        }

        var imageName = menu.get().getImage().getImagename();
        this.imageService.deleteImage(imageName, this.MENUS_IMAGES_PATH);
        this.menuDao.delete(menu.get());
        return menu.get();
    }

    @Override
    public MenuEntity addMenu(MenuDtoIn menuDtoIn) throws InvalidMenuInformationException, InvalidMealInformationException, MealNotFoundException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, ExistingMenuException, UnavailableMealException {

        var menuEntity = menuDtoIn.toMenuEntity();

        IMenuService.ValidateMealID(menuDtoIn);

        var menu = this.checkExistingMenu(menuEntity.getLabel(), menuEntity.getDescription(), menuEntity.getPrice());

        if (menu.isPresent()) {
            MenuService.LOG.error("THE MENU ALREADY EXISTS IN THE DATABASE with label = {} , description = {} and price = {} ", menuEntity.getLabel(), menuEntity.getDescription(), menuEntity.getPrice());
            throw new ExistingMenuException("THE MENU ALREADY EXISTS IN THE DATABASE");
        }

        List<Integer> mealIDs = menuDtoIn.fromStringMealIDsToIntegerMealIDs();
        List<MealEntity> mealsInMenu = new ArrayList<>();

        for (Integer mealID : mealIDs) {
            var meal = this.mealService.getMealEntityByID(mealID);
            if (meal.getStatus() == 0 ){
                MenuService.LOG.error("THE MEAL WITH ID = {} IS NOT AVAILABLE ", mealID);
                throw new UnavailableMealException(" LE PLAT  " + meal.getLabel() + " N'EST PAS DISPONIBLE ");
            }
            mealsInMenu.add(meal);
        }
        menuEntity.setMeals(mealsInMenu);
        MultipartFile image = menuDtoIn.getImage();

        var imageName = this.imageService.uploadImage(image, this.MENUS_IMAGES_PATH);

        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setImagename(imageName);
        menuEntity.setImage(imageEntity);


        menuEntity.setCreatedDate(LocalDate.now());

        return this.menuDao.save(menuEntity);
    }

    @Override
    public MenuDtout getMenuById(Integer menuID) throws MealNotFoundException, InvalidMenuInformationException {
        IMenuService.verifyMealInformation("THE ID CAN NOT BE NULL OR LESS THAN 0", menuID);
        var menu = this.menuDao.findById(menuID);
        if (menu.isPresent()) {
            return new MenuDtout(menu.get(), this.MENUS_IMAGES_URL, this.MEALS_IMAGES_PATH);
        }


        MenuService.LOG.debug("NO DISH WAS FOUND WITH AN ID = {} IN THE getMealByID METHOD ", menuID);
        throw new MealNotFoundException("NO MENU WAS FOUND WITH THIS ID ");
    }

    @Override
    public List<MenuDtout> getAllMenus() {
        return this.menuDao.findAll().stream()
                .map(menuEntity -> new MenuDtout(menuEntity, this.MENUS_IMAGES_URL, this.MEALS_IMAGES_PATH))
                .toList();
    }


    @Override
    public Optional<MenuEntity> checkExistingMenu(String label, String description, BigDecimal price) {
        return this.menuDao.findByLabelAndAndPriceAndDescriptionIgnoreCase(label, description, price);

    }

}