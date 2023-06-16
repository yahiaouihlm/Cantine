package fr.sqli.Cantine.controller.admin.menus;

import fr.sqli.Cantine.dto.in.food.MenuDtoIn;
import fr.sqli.Cantine.dto.out.food.MenuDtout;
import fr.sqli.Cantine.service.admin.meals.exceptions.InvalidMealInformationException;
import fr.sqli.Cantine.service.admin.meals.exceptions.MealNotFoundException;
import fr.sqli.Cantine.service.admin.menus.exceptions.ExistingMenuException;
import fr.sqli.Cantine.service.admin.menus.exceptions.InvalidMenuInformationException;
import fr.sqli.Cantine.service.admin.menus.exceptions.MenuNotFoundException;
import fr.sqli.Cantine.service.images.exception.ImagePathException;
import fr.sqli.Cantine.service.images.exception.InvalidImageException;
import fr.sqli.Cantine.service.images.exception.InvalidFormatImageException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

public interface IMenuController {

    /*------------------ ENDPOINTS ------------------*/
    String MENUS_BASIC_URL_ADMIN = "/cantine/api/admin/menus";

    String  ENDPOINT_UPDATE_MENU_URL = "/update";
    String ENDPOINT_ADD_MENU_URL = "/add";
    String ENDPOINT_DELETE_MENU_URL = "/delete";
    String ENDPOINT_GET_ALL_MENUS_URL = "/getAll";

    String ENDPOINT_GET_ONE_MENU_URL = "/get";



    /*------------------ MESSAGES ------------------*/
    String  MENU_UPDATED_SUCCESSFULLY = "MENU UPDATED SUCCESSFULLY";
    String MENU_ADDED_SUCCESSFULLY = "MENU ADDED SUCCESSFULLY";
    String MENU_DELETED_SUCCESSFULLY = "MENU DELETED SUCCESSFULLY";

    /*------------------ METHODS ------------------*/

    public ResponseEntity<String> update (@RequestParam("idMenu")Integer idMenu,   MenuDtoIn menuDtoIn) throws InvalidMenuInformationException, MealNotFoundException, InvalidMealInformationException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, ExistingMenuException, MenuNotFoundException;


    public ResponseEntity<String> deleteMenu(@RequestParam("idMenu")Integer idMenu) throws InvalidMenuInformationException, MealNotFoundException, MenuNotFoundException, ImagePathException;

    public ResponseEntity<String> addMenu(MenuDtoIn menuDtoIn) throws InvalidMenuInformationException, MealNotFoundException, InvalidMealInformationException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, ExistingMenuException;

    public ResponseEntity<MenuDtout> getMenuById(Integer idMenu) throws InvalidMenuInformationException, MealNotFoundException;
    public ResponseEntity<List<MenuDtout>> getAllMenus();
}
