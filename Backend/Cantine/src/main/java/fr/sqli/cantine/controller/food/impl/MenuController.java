package fr.sqli.cantine.controller.food.impl;


import fr.sqli.cantine.controller.food.IMenuController;
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
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static fr.sqli.cantine.controller.food.IMenuController.MENUS_BASIC_URL_ADMIN;

@RestController
@RequestMapping(value  = MENUS_BASIC_URL_ADMIN)
@CrossOrigin(origins = "http://localhost:4200")
public class MenuController implements IMenuController {

    private final MenuService menuService;

    @Autowired
    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }




    @Override
    public ResponseEntity<ResponseDtout> update(MenuDtoIn menuDtoIn) throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, InvalidFoodInformationException, ExistingFoodException, FoodNotFoundException, UnavailableFoodException {
         this.menuService.updateMenu( menuDtoIn);
        return ResponseEntity.ok(new ResponseDtout(MENU_UPDATED_SUCCESSFULLY));
    }



    @Override
    public ResponseEntity<ResponseDtout> deleteMenu(String uuidMenu) throws ImagePathException, InvalidFoodInformationException, FoodNotFoundException, RemoveFoodException {
        this.menuService.removeMenu(uuidMenu);
        return ResponseEntity.ok(new  ResponseDtout(MENU_DELETED_SUCCESSFULLY));
    }



    @Override
    public ResponseEntity<ResponseDtout>  addMenu(MenuDtoIn menuDtoIn) throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, UnavailableFoodException, InvalidFoodInformationException, ExistingFoodException, FoodNotFoundException {
             this.menuService.addMenu(menuDtoIn);
        return ResponseEntity.ok(new ResponseDtout(MENU_ADDED_SUCCESSFULLY));

    }



    @Override
    public ResponseEntity<MenuDtOut> getMenuById(String uuidMenu) throws InvalidFoodInformationException, FoodNotFoundException {
           return  ResponseEntity.ok(this.menuService.getMenuByUuId(uuidMenu));
    }

    @Override
    public ResponseEntity<List<MenuDtOut>> getAllMenu() {
        return ResponseEntity.ok().body(this.menuService.getAllMenus());
    }


}