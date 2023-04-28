package fr.sqli.Cantine.controller.admin.menus;


import fr.sqli.Cantine.dto.in.MenuDtoIn;
import fr.sqli.Cantine.dto.out.MenuDtout;
import fr.sqli.Cantine.service.admin.meals.exceptions.InvalidMealInformationException;
import fr.sqli.Cantine.service.admin.meals.exceptions.MealNotFoundAdminException;
import fr.sqli.Cantine.service.admin.menus.MenuService;
import fr.sqli.Cantine.service.admin.menus.exceptions.ExistingMenuException;
import fr.sqli.Cantine.service.admin.menus.exceptions.InvalidMenuInformationException;
import fr.sqli.Cantine.service.images.exception.ImagePathException;
import fr.sqli.Cantine.service.images.exception.InvalidImageException;
import fr.sqli.Cantine.service.images.exception.InvalidFormatImageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static fr.sqli.Cantine.controller.admin.menus.IMenuController.MENUS_URL_ADMIN;

@RestController
@RequestMapping(value  = MENUS_URL_ADMIN)
public class MenuController implements   IMenuController {

    private final MenuService menuService;

    @Autowired
    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }


    @Override
    @PostMapping(value = ENDPOINT_ADD_MENU_URL)

    public ResponseEntity<String>  addMenu(MenuDtoIn menuDtoIn) throws InvalidMenuInformationException, MealNotFoundAdminException, InvalidMealInformationException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, ExistingMenuException {
         this.menuService.addMenu(menuDtoIn);
        return ResponseEntity.ok(MENU_ADDED_SUCCESSFULLY);
    }

    @Override
    @GetMapping(value = ENDPOINT_GET_ONE_MENU_URL)
    public ResponseEntity<MenuDtout> getMenuById(@RequestParam("idMenu") Integer idMenu) throws InvalidMenuInformationException, MealNotFoundAdminException {
           return  ResponseEntity.ok(this.menuService.getMenuById(idMenu));
    }

    @Override
    @GetMapping(value = ENDPOINT_GET_ALL_MENUS_URL)
    public ResponseEntity<List<MenuDtout>> getAllMenus() {
        return ResponseEntity.ok(this.menuService.getAllMenus());
    }


}