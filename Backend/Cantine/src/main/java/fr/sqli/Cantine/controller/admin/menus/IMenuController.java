package fr.sqli.Cantine.controller.admin.menus;

import fr.sqli.Cantine.dto.in.MenuDtoIn;
import fr.sqli.Cantine.dto.out.MenuDtout;
import fr.sqli.Cantine.service.admin.meals.exceptions.InvalidMealInformationException;
import fr.sqli.Cantine.service.admin.meals.exceptions.MealNotFoundAdminException;
import fr.sqli.Cantine.service.admin.menus.exceptions.ExistingMenuException;
import fr.sqli.Cantine.service.admin.menus.exceptions.InvalidMenuInformationException;
import fr.sqli.Cantine.service.images.exception.ImagePathException;
import fr.sqli.Cantine.service.images.exception.InvalidImageException;
import fr.sqli.Cantine.service.images.exception.InvalidFormatImageException;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;

public interface IMenuController {

    /*------------------ ENDPOINTS ------------------*/
    String MENUS_URL_ADMIN = "/cantine/api/admin/menus";
    String ENDPOINT_ADD_MENU_URL = "/add";
    String ENDPOINT_GET_ALL_MENUS_URL = "/getAll";

    String ENDPOINT_GET_ONE_MENU_URL = "/get";

    /*------------------ MESSAGES ------------------*/
    String MENU_ADDED_SUCCESSFULLY = "MENU ADDED SUCCESSFULLY";

    /*------------------ METHODS ------------------*/
    public ResponseEntity<String> addMenu(MenuDtoIn menuDtoIn) throws InvalidMenuInformationException, MealNotFoundAdminException, InvalidMealInformationException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, ExistingMenuException;

    public ResponseEntity<MenuDtout> getMenuById(Integer idMenu) throws InvalidMenuInformationException, MealNotFoundAdminException;
    public ResponseEntity<List<MenuDtout>> getAllMenus();
}
