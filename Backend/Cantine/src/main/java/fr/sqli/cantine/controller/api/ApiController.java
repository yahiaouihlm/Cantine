package fr.sqli.cantine.controller.api;


import fr.sqli.cantine.dto.out.food.MealDtout;
import fr.sqli.cantine.dto.out.food.MenuDtout;
import fr.sqli.cantine.service.food.meals.MealService;
import fr.sqli.cantine.service.food.menus.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static fr.sqli.cantine.controller.api.IApi.BASIC_API_URL;

@RestController
@RequestMapping(BASIC_API_URL)
@CrossOrigin(origins = "http://localhost:4200")
public class ApiController   implements   IApi {

    private MealService mealService ;

    private MenuService menuService ;
    @Autowired
    public  ApiController( MenuService menuService , MealService mealService ){
        this.menuService = menuService;
        this.mealService  = mealService;
    }


    @Override
    public ResponseEntity<List<MealDtout>> getAllMeals() {
        var meals = this.mealService.getAllMeals();
        return ResponseEntity.ok(meals);
    }


    @Override
    public ResponseEntity<List<MenuDtout>> getAllMenus() {
        return ResponseEntity.ok(this.menuService.getAllMenus());
    }

}
