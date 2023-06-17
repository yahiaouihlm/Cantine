package fr.sqli.Cantine.controller.api;

import fr.sqli.Cantine.dto.out.food.MealDtout;
import fr.sqli.Cantine.dto.out.food.MenuDtout;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

public interface IApi {

    String  BASIC_MEALS_API = "/cantine/api/meals" ;

;
    String ENDPOINT_GET_ALL_MEALS_URL = "/getAll";
    String ENDPOINT_GET_ALL_MENUS_URL = "/getAll";

    @GetMapping(value = ENDPOINT_GET_ALL_MEALS_URL)
     ResponseEntity<List<MealDtout>> getAllMeals() ;


    @GetMapping(value = ENDPOINT_GET_ALL_MENUS_URL)
    ResponseEntity<List<MenuDtout>> getAllMenus();

}
