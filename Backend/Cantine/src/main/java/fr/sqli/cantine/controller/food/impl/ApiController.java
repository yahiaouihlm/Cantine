package fr.sqli.cantine.controller.food.impl;


import fr.sqli.cantine.controller.food.IApi;
import fr.sqli.cantine.dto.out.food.MealDtOut;
import fr.sqli.cantine.dto.out.food.MenuDtOut;
import fr.sqli.cantine.service.food.impl.MealService;
import fr.sqli.cantine.service.food.impl.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static fr.sqli.cantine.controller.food.IApi.BASIC_API_URL;

@RestController
@RequestMapping(BASIC_API_URL)
@CrossOrigin(origins = "http://localhost:4200")
public class ApiController   implements IApi {

    private MealService mealService ;

    private MenuService menuService ;
    @Autowired
    public  ApiController( MenuService menuService , MealService mealService ){
        this.menuService = menuService;
        this.mealService  = mealService;
    }


    @Override
    public ResponseEntity<List<MealDtOut>> getOnlyAvailableMeals() {
        return ResponseEntity.ok(this.mealService.getOnlyAvailableMeals());
    }


    @Override
    public ResponseEntity<List<MenuDtOut>> getAllMenus() {
        return ResponseEntity.ok(this.menuService.getAvailableMenu());
    }

}
