package fr.sqli.Cantine.controller.admin.menus;


import fr.sqli.Cantine.dto.in.MenuDtoIn;
import fr.sqli.Cantine.dto.out.MenuDtout;
import fr.sqli.Cantine.service.admin.meals.exceptions.InvalidMealInformationException;
import fr.sqli.Cantine.service.admin.meals.exceptions.MealNotFoundAdminException;
import fr.sqli.Cantine.service.admin.menus.MenuService;
import fr.sqli.Cantine.service.admin.menus.exceptions.InvalidMenuInformationException;
import fr.sqli.Cantine.service.images.exception.ImagePathException;
import fr.sqli.Cantine.service.images.exception.InvalidImageException;
import fr.sqli.Cantine.service.images.exception.InvalidTypeImageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import static fr.sqli.Cantine.controller.admin.menus.IMenuController.MENUS_URL_ADMIN;

@RestController
@RequestMapping(value  = MENUS_URL_ADMIN)
public class MenuController implements   IMenuController {

    private final MenuService menuService;

    @Autowired
    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @PostMapping(value = ENDPOINT_ADD_MENU_URL)
    @Override
    public ResponseEntity<String>  addMenu(MenuDtoIn menuDtoIn) throws InvalidMenuInformationException, MealNotFoundAdminException, InvalidMealInformationException, InvalidTypeImageException, InvalidImageException, ImagePathException, IOException {
         this.menuService.addMenu(menuDtoIn);
        return ResponseEntity.ok(MENU_ADDED_SUCCESSFULLY);
    }


}