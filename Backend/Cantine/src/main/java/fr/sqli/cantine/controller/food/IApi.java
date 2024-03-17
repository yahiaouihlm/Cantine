package fr.sqli.cantine.controller.food;

import fr.sqli.cantine.dto.out.food.MealDtOut;
import fr.sqli.cantine.dto.out.food.MenuDtOut;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface IApi {

    String BASIC_API_URL = "/cantine/api/getAll";

    String ENDPOINT_GET_ALL_MEALS_BY_TYPE_URL = "/getMealsByType";
    String ENDPOINT_GET_ALL_MEALS_URL = "/meals";
    String ENDPOINT_GET_ALL_MENUS_URL = "/menus";


    @GetMapping(value = ENDPOINT_GET_ALL_MEALS_BY_TYPE_URL)
    ResponseEntity<List<MealDtOut>> getMealsByType(@RequestParam("mealType") String mealType);

    @GetMapping(value = ENDPOINT_GET_ALL_MEALS_URL)
    ResponseEntity<List<MealDtOut>> getOnlyAvailableMeals();


    @GetMapping(value = ENDPOINT_GET_ALL_MENUS_URL)
    ResponseEntity<List<MenuDtOut>> getAllMenus();

}
