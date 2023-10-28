package fr.sqli.cantine.service.food.menus;


import fr.sqli.cantine.dao.IMenuDao;
import fr.sqli.cantine.dto.in.food.MenuDtoIn;
import fr.sqli.cantine.dto.out.food.MenuDtout;
import fr.sqli.cantine.entity.ImageEntity;
import fr.sqli.cantine.entity.MealEntity;
import fr.sqli.cantine.entity.MenuEntity;
import fr.sqli.cantine.service.food.exceptions.InvalidFoodInformationException;
import fr.sqli.cantine.service.food.meals.IMealService;
import fr.sqli.cantine.service.food.meals.exceptions.MealNotFoundException;
import fr.sqli.cantine.service.food.menus.exceptions.ExistingMenuException;
import fr.sqli.cantine.service.food.menus.exceptions.InvalidMenuInformationException;
import fr.sqli.cantine.service.food.menus.exceptions.MenuNotFoundException;
import fr.sqli.cantine.service.food.menus.exceptions.UnavailableMealException;
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
    public MenuEntity updateMenu(MenuDtoIn menuDtoIn) throws InvalidMenuInformationException, MealNotFoundException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, MenuNotFoundException, ExistingMenuException, InvalidFoodInformationException {

        if  (menuDtoIn == null) {
            MenuService.LOG.debug("THE MENU DTO CAN NOT BE NULL IN THE updateMenu METHOD ");
            throw new InvalidMenuInformationException("THE MENU DTO CAN NOT BE NULL");
        }

        IMenuService.checkMenuUuidValidity(menuDtoIn.getUuid());
        menuDtoIn.toMenuEntityWithoutImage();



        var menuToUpdate = this.menuDao.findByUuid(menuDtoIn.getUuid());
        if (menuToUpdate.isEmpty()) {
            MenuService.LOG.error("NO MENU WAS FOUND WITH AN UUID = {} IN THE updateMenu METHOD ", menuDtoIn.getUuid());
            throw new MenuNotFoundException("NO MENU WAS FOUND WITH THIS ID ");
        }


        var menuUpdatedDoesExist = this.checkExistingMenu(menuDtoIn.getLabel(), menuDtoIn.getDescription(), menuDtoIn.getPrice());

        if (menuUpdatedDoesExist.isPresent()) {
            if (menuUpdatedDoesExist.get().getId() != menuToUpdate.get().getId()) {
                MenuService.LOG.error("THE MENU ALREADY EXISTS IN THE DATABASE with label = {} , description = {} and price = {} ", menuDtoIn.getLabel(), menuDtoIn.getDescription(), menuDtoIn.getPrice());
                throw new ExistingMenuException("THE MENU ALREADY EXISTS IN THE DATABASE");
            }
        }

        var menuEntity = menuToUpdate.get();
        menuEntity.setLabel(menuDtoIn.getLabel());
        menuEntity.setDescription(menuDtoIn.getDescription());
        menuEntity.setPrice(menuDtoIn.getPrice());
        menuEntity.setStatus(menuDtoIn.getStatus());
        menuEntity.setQuantity(menuDtoIn.getQuantity());

        var  mealsUuids = menuDtoIn.getMealUuids();
        List<MealEntity> mealsInMenu = new ArrayList<>(); //  check  existing meals in the   database  and  add them to the menu
        for (String mealUuid : mealsUuids ) {
            var meal = this.mealService.getMealEntityByUUID(mealUuid);
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
    public MenuEntity removeMenu(String menuUuid) throws MenuNotFoundException, InvalidMenuInformationException, ImagePathException {

        IMenuService.checkMenuUuidValidity(menuUuid);

        var menu = this.menuDao.findByUuid(menuUuid);

        if (menu.isEmpty()) {
            MenuService.LOG.error("NO MENU WAS FOUND WITH AN UUID = {} IN THE removeMenu METHOD ", menuUuid);
            throw new MenuNotFoundException("NO MENU WAS FOUND WITH THIS ID ");
        }

        var imageName = menu.get().getImage().getImagename();
        this.imageService.deleteImage(imageName, this.MENUS_IMAGES_PATH);
        this.menuDao.delete(menu.get());
        return menu.get();
    }

    @Override
    public MenuEntity addMenu(MenuDtoIn menuDtoIn) throws InvalidMenuInformationException, MealNotFoundException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, ExistingMenuException, UnavailableMealException, InvalidFoodInformationException {

         menuDtoIn.checkMenuInformationValidity();


        var menu = this.checkExistingMenu(menuDtoIn.getLabel(), menuDtoIn.getDescription(), menuDtoIn.getPrice());

        if (menu.isPresent()) {
            MenuService.LOG.error("THE MENU ALREADY EXISTS IN THE DATABASE with label = {} , description = {} and price = {} ", menuDtoIn.getLabel(), menuDtoIn.getDescription(), menuDtoIn.getPrice());
            throw new ExistingMenuException("THE MENU ALREADY EXISTS IN THE DATABASE");
        }

        List<String> mealUuids = menuDtoIn.getMealUuids();
        List<MealEntity> mealsInMenu = new ArrayList<>();

        for (String  mealUuid : mealUuids) {
            var meal = this.mealService.getMealEntityByUUID(mealUuid);
            if (meal.getStatus() == 0 ){
                MenuService.LOG.error("THE MEAL WITH UUID = {} IS NOT AVAILABLE ", mealUuid);
                throw new UnavailableMealException(" LE PLAT  " + meal.getLabel() + " N'EST PAS DISPONIBLE ");
            }
            mealsInMenu.add(meal);
        }

        MultipartFile image = menuDtoIn.getImage();
        var imageName = this.imageService.uploadImage(image, this.MENUS_IMAGES_PATH);
        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setImagename(imageName);

       MenuEntity menuEntity = new MenuEntity(menuDtoIn.getLabel(), menuDtoIn.getDescription(), menuDtoIn.getPrice(), menuDtoIn.getStatus(), menuDtoIn.getQuantity(), imageEntity, mealsInMenu);


        return this.menuDao.save(menuEntity);
    }

    @Override
    public MenuDtout getMenuById(String menuUuid) throws MealNotFoundException, InvalidMenuInformationException {
        IMenuService.checkMenuUuidValidity(menuUuid);
        var menu = this.menuDao.findByUuid(menuUuid);
        if (menu.isPresent()) {
            return new MenuDtout(menu.get(), this.MENUS_IMAGES_URL, this.MEALS_IMAGES_PATH);
        }


        MenuService.LOG.debug("NO DISH WAS FOUND WITH AN UUID = {} IN THE getMealByID METHOD ", menuUuid);
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