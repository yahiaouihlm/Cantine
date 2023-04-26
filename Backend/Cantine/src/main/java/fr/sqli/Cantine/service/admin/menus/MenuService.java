package fr.sqli.Cantine.service.admin.menus;


import fr.sqli.Cantine.dao.IMenuDao;
import fr.sqli.Cantine.dto.in.MenuDtoIn;
import fr.sqli.Cantine.dto.out.MenuDtout;
import fr.sqli.Cantine.entity.ImageEntity;
import fr.sqli.Cantine.entity.MenuEntity;
import fr.sqli.Cantine.service.admin.meals.IMealService;
import fr.sqli.Cantine.service.admin.meals.MealService;
import fr.sqli.Cantine.service.admin.meals.exceptions.InvalidMealInformationException;
import fr.sqli.Cantine.service.admin.meals.exceptions.MealNotFoundAdminException;
import fr.sqli.Cantine.service.admin.menus.exceptions.InvalidMenuInformationException;
import fr.sqli.Cantine.service.images.IImageService;
import fr.sqli.Cantine.service.images.ImageService;
import fr.sqli.Cantine.service.images.exception.ImagePathException;
import fr.sqli.Cantine.service.images.exception.InvalidImageException;
import fr.sqli.Cantine.service.images.exception.InvalidTypeImageException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
public class MenuService implements IMenuService {
    private static final Logger LOG = LogManager.getLogger();
    private final String MENUS_IMAGES_PATH;
    private final IMealService mealService;
    private final IImageService imageService;
    private final IMenuDao menuDao;

    @Autowired
    public MenuService(Environment environment, IMealService mealService, IImageService imageService, IMenuDao menuDao) {
        this.mealService = mealService;
        this.imageService = imageService;
        this.menuDao = menuDao;
        this.MENUS_IMAGES_PATH = environment.getProperty("sqli.cantine.images.menus.path");
    }

    @Override
    public MenuEntity addMenu(MenuDtoIn menuDtoIn) throws InvalidMenuInformationException, InvalidMealInformationException, MealNotFoundAdminException, InvalidTypeImageException, InvalidImageException, ImagePathException, IOException {
        var menuEntity = menuDtoIn.toMenuEntity();

        if (menuDtoIn.getMealIDs() == null || menuDtoIn.getMealIDs().size() == 0 || menuDtoIn.getMealIDs().isEmpty()) {
           MenuService.LOG.error("The menu doesn't contain any meal");
            throw new InvalidMenuInformationException("The menu doesn't contain any meal");
        }

        for (Integer mealID : menuDtoIn.getMealIDs()) {
            this.mealService.getMealByID(mealID);
        }

        MultipartFile image = menuDtoIn.getImage();

        var imageName = this.imageService.uploadImage(image, this.MENUS_IMAGES_PATH);

        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setImagename(imageName);
        menuEntity.setImage(imageEntity);


        menuEntity.setCreatedDate(LocalDate.now());

        return this.menuDao.save(menuEntity);
    }

    @Override
    public List<MenuDtout> getAllMenus() {
        return this.menuDao.findAll().stream()
                   .map(menuEntity -> new MenuDtout(menuEntity, this.MENUS_IMAGES_PATH))
                    .toList();
    }


}
