package fr.sqli.Cantine.controller.admin.menus;


import fr.sqli.Cantine.dto.in.food.MenuDtoIn;
import fr.sqli.Cantine.dto.out.ResponseDtout;
import fr.sqli.Cantine.dto.out.food.MenuDtout;
import fr.sqli.Cantine.service.admin.meals.exceptions.InvalidMealInformationException;
import fr.sqli.Cantine.service.admin.meals.exceptions.MealNotFoundException;
import fr.sqli.Cantine.service.admin.menus.MenuService;
import fr.sqli.Cantine.service.admin.menus.exceptions.ExistingMenuException;
import fr.sqli.Cantine.service.admin.menus.exceptions.InvalidMenuInformationException;
import fr.sqli.Cantine.service.admin.menus.exceptions.MenuNotFoundException;
import fr.sqli.Cantine.service.admin.menus.exceptions.UnavailableMealException;
import fr.sqli.Cantine.service.images.exception.ImagePathException;
import fr.sqli.Cantine.service.images.exception.InvalidImageException;
import fr.sqli.Cantine.service.images.exception.InvalidFormatImageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static fr.sqli.Cantine.controller.admin.menus.IMenuController.MENUS_BASIC_URL_ADMIN;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

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
    public ResponseEntity<ResponseDtout> update(MenuDtoIn menuDtoIn) throws InvalidMenuInformationException, MealNotFoundException, InvalidMealInformationException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, ExistingMenuException, MenuNotFoundException {
         this.menuService.updateMenu( menuDtoIn);
        return ResponseEntity.ok(new ResponseDtout(MENU_UPDATED_SUCCESSFULLY));
    }


    @DeleteMapping(value = ENDPOINT_DELETE_MENU_URL)
    @Override
    public ResponseEntity<String> deleteMenu(Integer idMenu) throws InvalidMenuInformationException, MenuNotFoundException, ImagePathException {
        this.menuService.removeMenu(idMenu);
        return ResponseEntity.ok(MENU_DELETED_SUCCESSFULLY);
    }



    @Override
    public ResponseEntity<ResponseDtout>  addMenu(MenuDtoIn menuDtoIn) throws InvalidMenuInformationException, MealNotFoundException, InvalidMealInformationException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, ExistingMenuException, UnavailableMealException {
             this.menuService.addMenu(menuDtoIn);
        return ResponseEntity.ok(new ResponseDtout(MENU_ADDED_SUCCESSFULLY));

    }



    @Override
    public ResponseEntity<MenuDtout> getMenuById(@RequestParam("idMenu") Integer idMenu) throws InvalidMenuInformationException, MealNotFoundException {
           return  ResponseEntity.ok(this.menuService.getMenuById(idMenu));
    }




}