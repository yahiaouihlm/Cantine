package fr.sqli.cantine.controller.admin.menus;

import fr.sqli.cantine.dto.in.food.MenuDtoIn;
import fr.sqli.cantine.dto.out.ResponseDtout;
import fr.sqli.cantine.dto.out.food.MenuDtout;
import fr.sqli.cantine.service.food.meals.exceptions.InvalidMealInformationException;
import fr.sqli.cantine.service.food.meals.exceptions.MealNotFoundException;
import fr.sqli.cantine.service.food.menus.exceptions.ExistingMenuException;
import fr.sqli.cantine.service.food.menus.exceptions.InvalidMenuInformationException;
import fr.sqli.cantine.service.food.menus.exceptions.MenuNotFoundException;
import fr.sqli.cantine.service.food.menus.exceptions.UnavailableMealException;
import fr.sqli.cantine.service.images.exception.ImagePathException;
import fr.sqli.cantine.service.images.exception.InvalidImageException;
import fr.sqli.cantine.service.images.exception.InvalidFormatImageException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

public interface IMenuController {

    /*------------------ ENDPOINTS ------------------*/
    String MENUS_BASIC_URL_ADMIN = "/cantine/api/admin/menus";

    String  ENDPOINT_UPDATE_MENU_URL = "/update";
    String ENDPOINT_ADD_MENU_URL = "/add";
    String ENDPOINT_DELETE_MENU_URL = "/delete";


    String ENDPOINT_GET_ONE_MENU_URL = "/get";



    /*------------------ MESSAGES ------------------*/
    String  MENU_UPDATED_SUCCESSFULLY = "MENU UPDATED SUCCESSFULLY";
    String MENU_ADDED_SUCCESSFULLY = "MENU ADDED SUCCESSFULLY";
    String MENU_DELETED_SUCCESSFULLY = "MENU DELETED SUCCESSFULLY";

    /*------------------ METHODS ------------------*/
    @PutMapping(value = ENDPOINT_UPDATE_MENU_URL,  consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDtout> update (MenuDtoIn menuDtoIn) throws InvalidMenuInformationException, MealNotFoundException, InvalidMealInformationException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, ExistingMenuException, MenuNotFoundException;


    public ResponseEntity<String> deleteMenu(@RequestParam("idMenu")Integer idMenu) throws InvalidMenuInformationException, MealNotFoundException, MenuNotFoundException, ImagePathException;

    @PostMapping(value = ENDPOINT_ADD_MENU_URL , consumes = MULTIPART_FORM_DATA_VALUE )
    ResponseEntity<ResponseDtout>  addMenu(MenuDtoIn menuDtoIn) throws InvalidMenuInformationException, MealNotFoundException, InvalidMealInformationException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, ExistingMenuException, UnavailableMealException;

    @GetMapping(value = ENDPOINT_GET_ONE_MENU_URL)
    public ResponseEntity<MenuDtout> getMenuById(Integer idMenu) throws InvalidMenuInformationException, MealNotFoundException;

}
