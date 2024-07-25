package fr.sqli.cantine.controller.food;


import fr.sqli.cantine.dto.in.food.MenuDtoIn;
import fr.sqli.cantine.dto.out.ResponseDtout;
import fr.sqli.cantine.dto.out.food.MenuDtOut;
import fr.sqli.cantine.service.food.exceptions.*;
import fr.sqli.cantine.service.food.impl.MenuService;
import fr.sqli.cantine.service.images.exception.ImagePathException;
import fr.sqli.cantine.service.images.exception.InvalidImageException;
import fr.sqli.cantine.service.images.exception.InvalidFormatImageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static fr.sqli.cantine.constants.ConstCantine.ADMIN_ROLE_LABEL;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@RequestMapping(value = MenuController.MENUS_BASIC_URL_ADMIN)
public class MenuController {

    final String ENDPOINT_UPDATE_MENU_URL = "/update";

    /*------------------ ENDPOINTS ------------------*/
    final static String MENUS_BASIC_URL_ADMIN = "/cantine/admin/api/menus";
    final String ENDPOINT_ADD_MENU_URL = "/add";
    final String ENDPOINT_DELETE_MENU_URL = "/delete";
    final String ENDPOINT_GET_ALL_MENU = "/getAll";
    final String ENDPOINT_GET_ONE_MENU_URL = "/get";
    final String ENDPOINT_GET_ONLY_AVAILABLE_MENUS = "/getAvailableMenus";
    final String ENDPOINT_GET_ONLY_UNAVAILABLE_MENUS = "/getUnavailableMenus";
    final String GET_ONLY_MENUS_IN_DELETION_PROCESS_URL = "/getMenusInDeletionProcess";

    /*------------------ MESSAGES ------------------*/
    final String MENU_UPDATED_SUCCESSFULLY = "MENU UPDATED SUCCESSFULLY";
    final String MENU_ADDED_SUCCESSFULLY = "MENU ADDED SUCCESSFULLY";
    final String MENU_DELETED_SUCCESSFULLY = "MENU DELETED SUCCESSFULLY";


    private final MenuService menuService;

    @Autowired
    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }


    @PreAuthorize("hasAuthority('" + ADMIN_ROLE_LABEL + "')")
    @PostMapping(value = ENDPOINT_UPDATE_MENU_URL, consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDtout> update(MenuDtoIn menuDtoIn) throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, InvalidFoodInformationException, ExistingFoodException, FoodNotFoundException, UnavailableFoodException {
        this.menuService.updateMenu(menuDtoIn);
        return ResponseEntity.ok(new ResponseDtout(MENU_UPDATED_SUCCESSFULLY));
    }

    @PreAuthorize("hasAuthority('" + ADMIN_ROLE_LABEL + "')")
    @PostMapping(value = ENDPOINT_DELETE_MENU_URL)
    public ResponseEntity<ResponseDtout> deleteMenu(@RequestParam("uuidMenu") String uuidMenu) throws ImagePathException, InvalidFoodInformationException, FoodNotFoundException, RemoveFoodException {
        this.menuService.removeMenu(uuidMenu);
        return ResponseEntity.ok(new ResponseDtout(MENU_DELETED_SUCCESSFULLY));
    }

    @PreAuthorize("hasAuthority('" + ADMIN_ROLE_LABEL + "')")
    @PostMapping(value = ENDPOINT_ADD_MENU_URL, consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDtout> addMenu(MenuDtoIn menuDtoIn) throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, UnavailableFoodException, InvalidFoodInformationException, ExistingFoodException, FoodNotFoundException {
        this.menuService.addMenu(menuDtoIn);
        return ResponseEntity.ok(new ResponseDtout(MENU_ADDED_SUCCESSFULLY));
    }

    @PreAuthorize("hasAuthority('" + ADMIN_ROLE_LABEL + "')")
    @GetMapping(value = ENDPOINT_GET_ONE_MENU_URL)
    public ResponseEntity<MenuDtOut> getMenuById(@RequestParam("uuidMenu") String uuidMenu) throws InvalidFoodInformationException, FoodNotFoundException {
        return ResponseEntity.ok(this.menuService.getMenuByUuId(uuidMenu));
    }

    @PreAuthorize("hasAuthority('" + ADMIN_ROLE_LABEL + "')")
    @GetMapping(value = ENDPOINT_GET_ALL_MENU)
    public ResponseEntity<List<MenuDtOut>> getAllMenu() {
        return ResponseEntity.ok().body(this.menuService.getAllMenus());
    }

    @GetMapping(value = ENDPOINT_GET_ONLY_AVAILABLE_MENUS)
    public ResponseEntity<List<MenuDtOut>> getAvailableMenus() {
        return ResponseEntity.ok(this.menuService.getAvailableMenu());
    }

    @GetMapping(value = ENDPOINT_GET_ONLY_UNAVAILABLE_MENUS)
    public ResponseEntity<List<MenuDtOut>> getUnavailableMenus() {
        return ResponseEntity.ok(this.menuService.getUnavailableMenus());
    }

    @PreAuthorize("hasRole(" + ADMIN_ROLE_LABEL + ")")
    @GetMapping(value = GET_ONLY_MENUS_IN_DELETION_PROCESS_URL)
    public ResponseEntity<List<MenuDtOut>> getMenusInDeletionProcess() {
        return ResponseEntity.ok(this.menuService.getMenusInDeletionProcess());
    }


}