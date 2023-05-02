package fr.sqli.Cantine.service.admin.menus;


import fr.sqli.Cantine.dao.IMenuDao;
import fr.sqli.Cantine.dto.in.MenuDtoIn;
import fr.sqli.Cantine.dto.out.MenuDtout;
import fr.sqli.Cantine.entity.ImageEntity;
import fr.sqli.Cantine.entity.MealEntity;
import fr.sqli.Cantine.entity.MenuEntity;
import fr.sqli.Cantine.service.admin.meals.IMealService;
import fr.sqli.Cantine.service.admin.meals.exceptions.InvalidMealInformationException;
import fr.sqli.Cantine.service.admin.meals.exceptions.MealNotFoundAdminException;
import fr.sqli.Cantine.service.admin.menus.exceptions.ExistingMenuException;
import fr.sqli.Cantine.service.admin.menus.exceptions.InvalidMenuInformationException;
import fr.sqli.Cantine.service.admin.menus.exceptions.MenuNotFoundException;
import fr.sqli.Cantine.service.images.IImageService;
import fr.sqli.Cantine.service.images.exception.ImagePathException;
import fr.sqli.Cantine.service.images.exception.InvalidImageException;
import fr.sqli.Cantine.service.images.exception.InvalidFormatImageException;
import jdk.swing.interop.SwingInterOpUtils;
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
        this.MENUS_IMAGES_URL = environment.getProperty("sqli.cantine.images.url.menus"); // the link  to images of menus
        this.MENUS_IMAGES_PATH = environment.getProperty("sqli.cantine.images.menus.path"); //  the path  to the images of menus directory
        this.MEALS_IMAGES_PATH = environment.getProperty("sqli.cantine.images.url.meals"); //  the path  to the images of meals directory
    }

    @Override
    public MenuEntity  removeMenu(Integer menuID) throws MenuNotFoundException, InvalidMenuInformationException, ImagePathException {

        IMenuService.verifyMealInformation("THE CAN NOT BE NULL OR LESS THAN 0", menuID);

        var menu = this.menuDao.findById(menuID);

        if (menu.isEmpty()) {
            MenuService.LOG.error("NO MENU WAS FOUND WITH AN ID = {} IN THE removeMenu METHOD ", menuID);
            throw new MenuNotFoundException("NO MENU WAS FOUND WITH THIS ID ");
        }

        var imageName = menu.get().getImage().getImagename();
        this.imageService.deleteImage(imageName, this.MENUS_IMAGES_PATH);
        this.menuDao.delete(menu.get());
        return  menu.get();
    }

    @Override
    public MenuEntity addMenu(MenuDtoIn menuDtoIn) throws InvalidMenuInformationException, InvalidMealInformationException, MealNotFoundAdminException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, ExistingMenuException {
        var menuEntity = menuDtoIn.toMenuEntity();

        if (menuDtoIn.getMealIDs() == null || menuDtoIn.getMealIDs().size() == 0 || menuDtoIn.getMealIDs().isEmpty()) {
            MenuService.LOG.error("The menu doesn't contain any meal");
            throw new InvalidMenuInformationException("The menu doesn't contain any meal");
        }

        this.checkExistingMenu(menuEntity.getLabel(), menuEntity.getDescription(), menuEntity.getPrice());

        List<MealEntity> mealsInMenu = new ArrayList<>();
        for (Integer mealID : menuDtoIn.getMealIDs()) {
            var meal = this.mealService.getMealEntityByID(mealID);
            mealsInMenu.add(meal);
        }
        menuEntity.setMeals(mealsInMenu);
        MultipartFile image = menuDtoIn.getImage();

        var imageName = this.imageService.uploadImage(image, this.MENUS_IMAGES_PATH);

        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setImagename(imageName);
        menuEntity.setImage(imageEntity);


        menuEntity.setCreatedDate(LocalDate.now());

        return  this.menuDao.save(menuEntity);
    }

    @Override
    public MenuDtout getMenuById(Integer menuID) throws MealNotFoundAdminException, InvalidMenuInformationException {
        IMenuService.verifyMealInformation("THE ID CAN NOT BE NULL OR LESS THAN 0", menuID);
        var menu = this.menuDao.findById(menuID);
        if (menu.isPresent()) {
            return new MenuDtout(menu.get(), this.MENUS_IMAGES_URL, this.MEALS_IMAGES_PATH);
        }


        MenuService.LOG.debug("NO DISH WAS FOUND WITH AN ID = {} IN THE getMealByID METHOD ", menuID);
        throw new MealNotFoundAdminException("NO MENU WAS FOUND WITH THIS ID ");
    }

    @Override
    public List<MenuDtout> getAllMenus() {
        return this.menuDao.findAll().stream()
                .map(menuEntity -> new MenuDtout(menuEntity, this.MENUS_IMAGES_URL, this.MEALS_IMAGES_PATH))
                .toList();
    }

    @Override
    public void checkExistingMenu(String label, String description, BigDecimal price) throws ExistingMenuException {

        var menu = this.menuDao.findByLabelAndAndPriceAndDescriptionIgnoreCase(label, description, price);

        if (menu.isPresent()) {
            MenuService.LOG.error("THE MENU ALREADY EXISTS IN THE DATABASE with label = {} , description = {} and price = {} ", label, description, price);
            throw new ExistingMenuException("THE MENU ALREADY EXISTS IN THE DATABASE");
        }


    }
}