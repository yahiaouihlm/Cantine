package fr.sqli.cantine.controller.food;

import fr.sqli.cantine.dto.in.food.MenuDtoIn;
import fr.sqli.cantine.dto.out.ResponseDtout;
import fr.sqli.cantine.dto.out.food.MealDtOut;
import fr.sqli.cantine.dto.out.food.MenuDtOut;
import fr.sqli.cantine.service.food.exceptions.*;
import fr.sqli.cantine.service.images.exception.ImagePathException;
import fr.sqli.cantine.service.images.exception.InvalidImageException;
import fr.sqli.cantine.service.images.exception.InvalidFormatImageException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

public interface IMenuController {

    /*------------------ ENDPOINTS ------------------*/
    String MENUS_BASIC_URL_ADMIN = "/cantine/admin/api/menus";

    String  ENDPOINT_UPDATE_MENU_URL = "/update";
    String ENDPOINT_ADD_MENU_URL = "/add";
    String ENDPOINT_DELETE_MENU_URL = "/delete";
    String  ENDPOINT_GET_ALL_MENU =  "/getAll";

    String ENDPOINT_GET_ONE_MENU_URL = "/get";
    String  ENDPOINT_GET_ONLY_AVAILABLE_MENUS =  "/getAvailableMenus";

    String ENDPOINT_GET_ONLY_UNAVAILABLE_MENUS = "/getUnavailableMenus";

    String GET_ONLY_MENUS_IN_DELETION_PROCESS_URL = "/getMenusInDeletionProcess";




    /*------------------ MESSAGES ------------------*/
    String  MENU_UPDATED_SUCCESSFULLY = "MENU UPDATED SUCCESSFULLY";
    String MENU_ADDED_SUCCESSFULLY = "MENU ADDED SUCCESSFULLY";
    String MENU_DELETED_SUCCESSFULLY = "MENU DELETED SUCCESSFULLY";

    /*------------------ METHODS ------------------*/
    @PostMapping(value = ENDPOINT_UPDATE_MENU_URL,  consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDtout> update (MenuDtoIn menuDtoIn) throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, InvalidFoodInformationException, ExistingFoodException, FoodNotFoundException, UnavailableFoodException;

    @PostMapping(value = ENDPOINT_DELETE_MENU_URL)
    public ResponseEntity<ResponseDtout> deleteMenu(@RequestParam("uuidMenu")String uuidMenu) throws ImagePathException, InvalidFoodInformationException, FoodNotFoundException, RemoveFoodException;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = ENDPOINT_ADD_MENU_URL , consumes = MULTIPART_FORM_DATA_VALUE )
    ResponseEntity<ResponseDtout>  addMenu(MenuDtoIn menuDtoIn) throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, UnavailableFoodException, InvalidFoodInformationException, ExistingFoodException, FoodNotFoundException;

    @GetMapping(value = ENDPOINT_GET_ONE_MENU_URL)
    public ResponseEntity<MenuDtOut> getMenuById(@RequestParam("uuidMenu") String uuidMenu) throws InvalidFoodInformationException, FoodNotFoundException;


    @GetMapping(value = ENDPOINT_GET_ALL_MENU)
    public  ResponseEntity<List<MenuDtOut>> getAllMenu();

    @GetMapping(value = ENDPOINT_GET_ONLY_AVAILABLE_MENUS)
    ResponseEntity<List<MenuDtOut>> getAvailableMenus();

    @GetMapping(value = ENDPOINT_GET_ONLY_UNAVAILABLE_MENUS)
    ResponseEntity<List<MenuDtOut>> getUnavailableMenus();

    @GetMapping(value = GET_ONLY_MENUS_IN_DELETION_PROCESS_URL)
    ResponseEntity<List<MenuDtOut>> getMenusInDeletionProcess();

}
