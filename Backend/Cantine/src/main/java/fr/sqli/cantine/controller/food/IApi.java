package fr.sqli.cantine.controller.food;

import fr.sqli.cantine.dto.out.food.MealDtOut;
import fr.sqli.cantine.dto.out.food.MenuDtOut;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


public interface IApi {

    String BASIC_API_URL = "/cantine/api/getAll";

    String ENDPOINT_GET_ALL_MEALS_BY_TYPE_URL = "/getMealsByType";
    String ENDPOINT_GET_ALL_MEALS_URL = "/meals";
    String ENDPOINT_GET_ALL_MENUS_URL = "/menus";
    String  ENDPOINT_GET_ALL_MENUS_CONTAINS_TERM_URL = "/menus/contains";
    String GET_MENU_BY_LABEL = "/getMenuByLabel";

    @GetMapping(value = GET_MENU_BY_LABEL)
    public ResponseEntity<List<MenuDtOut>> searchMenuByLabel(@RequestParam("label") String label);

    @GetMapping(value = ENDPOINT_GET_ALL_MENUS_CONTAINS_TERM_URL)
    ResponseEntity<List<String>> searchLabelsOfMenuContains(@RequestParam("term") String  term);

    @GetMapping(value = ENDPOINT_GET_ALL_MEALS_BY_TYPE_URL)
    ResponseEntity<List<MealDtOut>> getMealsByType(@RequestParam("mealType") String mealType);

    @GetMapping(value = ENDPOINT_GET_ALL_MEALS_URL)
    ResponseEntity<List<MealDtOut>> getOnlyAvailableMeals();


    @GetMapping(value = ENDPOINT_GET_ALL_MENUS_URL)
    ResponseEntity<List<MenuDtOut>> getAllMenus();

}
