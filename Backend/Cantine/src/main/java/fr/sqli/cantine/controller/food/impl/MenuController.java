package fr.sqli.cantine.controller.food.impl;


import fr.sqli.cantine.controller.food.IMenuController;
import fr.sqli.cantine.dto.in.food.MenuDtoIn;
import fr.sqli.cantine.dto.out.ResponseDtout;
import fr.sqli.cantine.dto.out.food.MenuDtOut;
import fr.sqli.cantine.service.food.exceptions.ExistingFoodException;
import fr.sqli.cantine.service.food.exceptions.FoodNotFoundException;
import fr.sqli.cantine.service.food.exceptions.InvalidFoodInformationException;
import fr.sqli.cantine.service.food.impl.MenuService;
import fr.sqli.cantine.service.food.exceptions.UnavailableFoodException;
import fr.sqli.cantine.service.images.exception.ImagePathException;
import fr.sqli.cantine.service.images.exception.InvalidImageException;
import fr.sqli.cantine.service.images.exception.InvalidFormatImageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

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
    public ResponseEntity<ResponseDtout> update(MenuDtoIn menuDtoIn) throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, InvalidFoodInformationException, ExistingFoodException, FoodNotFoundException {
         this.menuService.updateMenu( menuDtoIn);
        return ResponseEntity.ok(new ResponseDtout(MENU_UPDATED_SUCCESSFULLY));
    }


    @DeleteMapping(value = ENDPOINT_DELETE_MENU_URL)
    @Override
    public ResponseEntity<String> deleteMenu(String idMenu) throws ImagePathException, InvalidFoodInformationException, FoodNotFoundException {
        this.menuService.removeMenu(idMenu);
        return ResponseEntity.ok(MENU_DELETED_SUCCESSFULLY);
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




}