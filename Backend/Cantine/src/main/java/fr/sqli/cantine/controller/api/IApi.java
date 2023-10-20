package fr.sqli.cantine.controller.api;

import fr.sqli.cantine.dto.out.food.MealDtout;
import fr.sqli.cantine.dto.out.food.MenuDtout;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

public interface IApi {

    String  BASIC_API_URL = "/cantine/user" ;

    String ENDPOINT_GET_ALL_MEALS_URL = "meals/getAll";
    String ENDPOINT_GET_ALL_MENUS_URL = "menus/getAll";

    @GetMapping(value = ENDPOINT_GET_ALL_MEALS_URL)
     ResponseEntity<List<MealDtout>> getAllMeals() ;


    @GetMapping(value = ENDPOINT_GET_ALL_MENUS_URL)
    ResponseEntity<List<MenuDtout>> getAllMenus();

}
