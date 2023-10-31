package fr.sqli.cantine.controller.food;

import fr.sqli.cantine.dto.out.food.MealDtOut;
import fr.sqli.cantine.dto.out.food.MenuDtOut;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

public interface IApi {

    String  BASIC_API_URL = "/cantine/api" ;

    String ENDPOINT_GET_ALL_MEALS_URL = "meals/getAll";
    String ENDPOINT_GET_ALL_MENUS_URL = "menus/getAll";

    @GetMapping(value = ENDPOINT_GET_ALL_MEALS_URL)
     ResponseEntity<List<MealDtOut>> getAllMeals() ;


    @GetMapping(value = ENDPOINT_GET_ALL_MENUS_URL)
    ResponseEntity<List<MenuDtOut>> getAllMenus();

}
