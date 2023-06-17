package fr.sqli.Cantine.controller.api;


import fr.sqli.Cantine.dto.out.food.MealDtout;
import fr.sqli.Cantine.service.admin.meals.MealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static fr.sqli.Cantine.controller.api.IApi.BASIC_MEALS_API;

@RestController
@RequestMapping(BASIC_MEALS_API)
public class ApiController   implements   IApi {

    private MealService mealService ;

    @Autowired
    public  ApiController( MealService mealService ){
        this.mealService  = mealService;
    }


    @Override
    public ResponseEntity<List<MealDtout>> getAllMeals() {
        var meals = this.mealService.getAllMeals();
        return ResponseEntity.ok(meals);
    }
}
