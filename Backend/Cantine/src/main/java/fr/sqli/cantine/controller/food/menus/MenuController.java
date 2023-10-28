package fr.sqli.cantine.controller.food.menus;


import fr.sqli.cantine.dto.in.food.MenuDtoIn;
import fr.sqli.cantine.dto.out.ResponseDtout;
import fr.sqli.cantine.dto.out.food.MenuDtout;
import fr.sqli.cantine.service.food.exceptions.InvalidFoodInformationException;
import fr.sqli.cantine.service.food.meals.exceptions.MealNotFoundException;
import fr.sqli.cantine.service.food.menus.MenuService;
import fr.sqli.cantine.service.food.menus.exceptions.ExistingMenuException;
import fr.sqli.cantine.service.food.menus.exceptions.InvalidMenuInformationException;
import fr.sqli.cantine.service.food.menus.exceptions.MenuNotFoundException;
import fr.sqli.cantine.service.food.menus.exceptions.UnavailableMealException;
import fr.sqli.cantine.service.images.exception.ImagePathException;
import fr.sqli.cantine.service.images.exception.InvalidImageException;
import fr.sqli.cantine.service.images.exception.InvalidFormatImageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static fr.sqli.cantine.controller.food.menus.IMenuController.MENUS_BASIC_URL_ADMIN;

@RestController
@RequestMapping(value  = MENUS_BASIC_URL_ADMIN)
@CrossOrigin(origins = "http://localhost:4200")
public class MenuController implements   IMenuController {

    private final MenuService menuService;

    @Autowired
    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }




    @Override
    public ResponseEntity<ResponseDtout> update(MenuDtoIn menuDtoIn) throws InvalidMenuInformationException, MealNotFoundException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, ExistingMenuException, MenuNotFoundException, InvalidFoodInformationException {
         this.menuService.updateMenu( menuDtoIn);
        return ResponseEntity.ok(new ResponseDtout(MENU_UPDATED_SUCCESSFULLY));
    }


    @DeleteMapping(value = ENDPOINT_DELETE_MENU_URL)
    @Override
    public ResponseEntity<String> deleteMenu(String idMenu) throws InvalidMenuInformationException, MenuNotFoundException, ImagePathException {
        this.menuService.removeMenu(idMenu);
        return ResponseEntity.ok(MENU_DELETED_SUCCESSFULLY);
    }



    @Override
    public ResponseEntity<ResponseDtout>  addMenu(MenuDtoIn menuDtoIn) throws InvalidMenuInformationException, MealNotFoundException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, ExistingMenuException, UnavailableMealException, InvalidFoodInformationException {
             this.menuService.addMenu(menuDtoIn);
        return ResponseEntity.ok(new ResponseDtout(MENU_ADDED_SUCCESSFULLY));

    }



    @Override
    public ResponseEntity<MenuDtout> getMenuById( String idMenu) throws InvalidMenuInformationException, MealNotFoundException {
           return  ResponseEntity.ok(this.menuService.getMenuById(idMenu));
    }




}