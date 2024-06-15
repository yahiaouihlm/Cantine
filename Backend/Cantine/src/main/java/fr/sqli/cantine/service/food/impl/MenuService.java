package fr.sqli.cantine.service.food.impl;


import fr.sqli.cantine.dao.IMenuDao;
import fr.sqli.cantine.dto.in.food.MenuDtoIn;
import fr.sqli.cantine.dto.out.food.MenuDtOut;
import fr.sqli.cantine.entity.ImageEntity;
import fr.sqli.cantine.entity.MealEntity;
import fr.sqli.cantine.entity.MenuEntity;
import fr.sqli.cantine.service.food.exceptions.*;
import fr.sqli.cantine.service.food.IMealService;
import fr.sqli.cantine.service.food.IMenuService;
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
import java.util.*;

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
    public MenuEntity updateMenu(MenuDtoIn menuDtoIn) throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, InvalidFoodInformationException, ExistingFoodException, FoodNotFoundException, UnavailableFoodException {

        if (menuDtoIn == null) {
            MenuService.LOG.debug("THE MENU DTO CAN NOT BE NULL IN THE updateMenu METHOD ");
            throw new InvalidFoodInformationException("THE MENU CAN NOT BE NULL");
        }

        IMenuService.checkMenuUuidValidity(menuDtoIn.getUuid());

        menuDtoIn.checkMenuInformationsWithOutImage();


        var menuEntity = this.menuDao.findByUuid(menuDtoIn.getUuid()).orElseThrow(() -> {
            MenuService.LOG.error("NO MENU WAS FOUND WITH AN UUID = {} IN THE updateMenu METHOD ", menuDtoIn.getUuid());
            return new FoodNotFoundException("NO MENU WAS FOUND");
        });


        var menuUpdatedDoesExist = this.getMenuWithLabelAndDescriptionAndPrice(menuDtoIn.getLabel(), menuDtoIn.getDescription(), menuDtoIn.getPrice());

        if (menuUpdatedDoesExist.isPresent()) {
            if (menuUpdatedDoesExist.get().getId().intValue() != menuEntity.getId().intValue()) {
                MenuService.LOG.error("THE MENU ALREADY EXISTS IN THE DATABASE with label = {} , description = {} and price = {} ", menuDtoIn.getLabel(), menuDtoIn.getDescription(), menuDtoIn.getPrice());
                throw new ExistingFoodException("THE MENU ALREADY EXISTS");
            }
        }


        menuEntity.setLabel(menuDtoIn.getLabel());
        menuEntity.setDescription(menuDtoIn.getDescription());
        menuEntity.setPrice(menuDtoIn.getPrice());
        menuEntity.setStatus(menuDtoIn.getStatus());
        menuEntity.setQuantity(menuDtoIn.getQuantity());

        var mealsUuids = menuDtoIn.getMealUuids();

        Set<MealEntity> mealsInMenu = new HashSet<>(); //  check  existing meals in the   database  and  add them to the menu

        for (String mealUuid : mealsUuids) {
            var meal = this.mealService.getMealEntityByUUID(mealUuid);

            if (meal.getStatus() != 1) {
                MenuService.LOG.error("THE MEAL WITH UUID = {} IS NOT AVAILABLE OR  DELETED MEAL  (meal.status = {}) CAN NOT BE ADDED TO  MENU", mealUuid, meal.getStatus());
                throw new UnavailableFoodException("THE UNAVAILABLE  OR DELETED  MEAL CAN NOT BE ADDED TO  MENU");
            }
            mealsInMenu.add(meal);
        }
        // check  if  the  menu  contains  at  least  2 meals
        if (mealsInMenu.size() < 2) {
            MenuService.LOG.error("THE MENU MUST CONTAIN AT LEAST 2 MEALS FOR THE UPDATE");
            throw new InvalidFoodInformationException("FEW MEALS IN THE MENU");
        }

        menuEntity.setMeals(new ArrayList<>(mealsInMenu));

        if (menuDtoIn.getImage() != null && !menuDtoIn.getImage().isEmpty() && menuDtoIn.getImage().getSize() > 0) {
            var oldImageName = menuEntity.getImage().getImagename();
            var newImageName = this.imageService.updateImage(oldImageName, menuDtoIn.getImage(), this.MENUS_IMAGES_PATH);
            menuEntity.getImage().setImagename(newImageName);
        }

        return this.menuDao.save(menuEntity);

    }

    @Override
    public MenuEntity removeMenu(String menuUuid) throws ImagePathException, InvalidFoodInformationException, FoodNotFoundException, RemoveFoodException {

        IMenuService.checkMenuUuidValidity(menuUuid);

        var menu = this.menuDao.findByUuid(menuUuid).orElseThrow(() -> {
            MenuService.LOG.debug("NO MENU WAS FOUND WITH AN UUID = {} IN THE removeMenu METHOD ", menuUuid);
            return new FoodNotFoundException("MENU NOT FOUND");
        });

        // check that the menu is not used in the orders

        if (menu.getOrders() != null && menu.getOrders().size() > 0) {
            menu.setStatus(3);
            this.menuDao.save(menu);
            MenuService.LOG.error("THE MENU WITH UUID = {} IS USED IN THE ORDERS CAN NOT BE DELETED", menuUuid);
            throw new RemoveFoodException("THE MENU IS USED IN THE ORDERS CAN NOT BE DELETED" +
                    "PS -> THE  MEAL WILL  BE  AUTOMATICALLY  REMOVED IN  BATCH  TRAITEMENT");
        }


        var imageName = menu.getImage().getImagename();
        this.imageService.deleteImage(imageName, this.MENUS_IMAGES_PATH);
        this.menuDao.delete(menu);
        return menu;
    }

    @Override
    public MenuEntity addMenu(MenuDtoIn menuDtoIn) throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, UnavailableFoodException, InvalidFoodInformationException, ExistingFoodException, FoodNotFoundException {

        if (menuDtoIn == null) {
            MenuService.LOG.debug("THE MENU DTO CAN NOT BE NULL IN THE addMenu METHOD ");
            throw new InvalidFoodInformationException("THE MENU CAN NOT BE NULL");
        }
        menuDtoIn.checkMenuInformationValidity();

        if (this.getMenuWithLabelAndDescriptionAndPrice(menuDtoIn.getLabel(), menuDtoIn.getDescription(), menuDtoIn.getPrice()).isPresent()) {
            MenuService.LOG.error("THE MENU ALREADY EXISTS IN THE DATABASE with label = {} , description = {} and price = {} ", menuDtoIn.getLabel(), menuDtoIn.getDescription(), menuDtoIn.getPrice());
            throw new ExistingFoodException("THE MENU ALREADY EXISTS IN THE DATABASE");
        }
        //945b5e94-e694-4380-b8eb-24c3b0b59031
        // use  the Set to  avoid  duplicate  meals in the  menu
        Set<MealEntity> mealsInMenu = new HashSet<>();

        for (String mealUuid : menuDtoIn.getMealUuids()) {
            var meal = this.mealService.getMealEntityByUUID(mealUuid);
            if (meal.getStatus() != 1) {
                MenuService.LOG.error("THE MEAL WITH UUID = {} IS NOT AVAILABLE OR  DELETED MEAL  (meal.status = {}) CAN NOT BE ADDED TO  MENU", mealUuid, meal.getStatus());
                throw new UnavailableFoodException("THE UNAVAILABLE OR DELETED  MEAL CAN NOT BE ADDED TO  MENU");
            }


            mealsInMenu.add(meal);
        }
        // check  if  the  menu  contains  at  least  2 meals
        if (mealsInMenu.size() < 2) {
            MenuService.LOG.error("THE MENU MUST CONTAIN AT LEAST 2 MEALS FOR THE UPDATE");
            throw new InvalidFoodInformationException("FEW MEALS IN THE MENU");
        }
        MultipartFile image = menuDtoIn.getImage();
        var imageName = this.imageService.uploadImage(image, this.MENUS_IMAGES_PATH);
        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setImagename(imageName);

        var menuEntity = new MenuEntity(menuDtoIn.getLabel(), menuDtoIn.getDescription(), menuDtoIn.getPrice(), menuDtoIn.getStatus(), menuDtoIn.getQuantity(), imageEntity, mealsInMenu);


        return this.menuDao.save(menuEntity);
    }

    @Override
    public MenuDtOut getMenuByUuId(String menuUuid) throws InvalidFoodInformationException, FoodNotFoundException {

        IMenuService.checkMenuUuidValidity(menuUuid);

        var menu = this.menuDao.findByUuid(menuUuid).orElseThrow(() -> {
            MenuService.LOG.debug("NO MENU WAS FOUND WITH AN UUID = {} IN THE getMenuByUuId METHOD ", menuUuid);
            return new FoodNotFoundException("NO MENU WAS FOUND");
        });


        return new MenuDtOut(menu, this.MENUS_IMAGES_URL, this.MEALS_IMAGES_PATH);


    }

    @Override
    public List<MenuDtOut> searchMenuByLabel(String label) {
        if (label == null || label.trim().length() <= 1)
            return Collections.emptyList();
        return this.menuDao.findMenuEntitiesByLabel(label).stream()
                .map(menuEntity -> new MenuDtOut(menuEntity, this.MENUS_IMAGES_URL, this.MEALS_IMAGES_PATH))
                .toList();
    }

    @Override
    public List<String> searchLablesOfMenuContainsTerm(String term) {
        if (term == null || term.trim().length() <= 1)
            return Collections.emptyList();

        return this.menuDao.findLabelsContainsIgnoreCase(term);
    }

    @Override
    public List<MenuDtOut> getAvailableMenu() {
        return this.menuDao.getAvailableMenus().stream()
                .map(menuEntity -> new MenuDtOut(menuEntity, this.MENUS_IMAGES_URL, this.MEALS_IMAGES_PATH))
                .toList();
    }

    @Override
    public List<MenuDtOut> getAllMenus() {
        return this.menuDao.findAll().stream()
                .map(menuEntity -> new MenuDtOut(menuEntity, this.MENUS_IMAGES_URL, this.MEALS_IMAGES_PATH))
                .toList();
    }

    @Override
    public List<MenuDtOut> getMenusInDeletionProcess() {
        return this.menuDao.getMenusInDeletionProcess().stream()
                .map(menuEntity -> new MenuDtOut(menuEntity, this.MENUS_IMAGES_URL, this.MEALS_IMAGES_PATH))
                .toList();
    }

    @Override
    public List<MenuDtOut> getUnavailableMenus() {
        return this.menuDao.getUnavailableMenu().stream()
                .map(menuEntity -> new MenuDtOut(menuEntity, this.MENUS_IMAGES_URL, this.MEALS_IMAGES_PATH))
                .toList();
    }


    @Override
    public Optional<MenuEntity> getMenuWithLabelAndDescriptionAndPrice(String label, String description, BigDecimal price) {
        return this.menuDao.findByLabelAndAndPriceAndDescriptionIgnoreCase(label, description, price);

    }

}