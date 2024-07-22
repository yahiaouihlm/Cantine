package fr.sqli.cantine.controller.food;



import fr.sqli.cantine.dto.out.food.MealDtOut;
import fr.sqli.cantine.dto.out.food.MenuDtOut;
import fr.sqli.cantine.service.food.impl.MealService;
import fr.sqli.cantine.service.food.impl.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(ApiController.BASIC_API_URL)
@CrossOrigin(origins = "http://localhost:4200")
public class ApiController{
    final static String BASIC_API_URL = "/cantine/api/getAll";

    final String ENDPOINT_GET_ALL_MEALS_BY_TYPE_URL = "/getMealsByType";
    final String ENDPOINT_GET_ALL_MEALS_URL = "/meals";
    final String ENDPOINT_GET_ALL_MENUS_URL = "/menus";
    final String  ENDPOINT_GET_ALL_MENUS_CONTAINS_TERM_URL = "/menus/contains";
    final String GET_MENU_BY_LABEL = "/getMenuByLabel";

    private final MealService mealService ;

    private final MenuService menuService ;


    @Autowired
    public  ApiController( MenuService menuService , MealService mealService ){
        this.menuService = menuService;
        this.mealService  = mealService;
    }


    @GetMapping(value = GET_MENU_BY_LABEL)
    public ResponseEntity<List<MenuDtOut>> searchMenuByLabel(@RequestParam("label") String label) {
        return ResponseEntity.ok(this.menuService.searchMenuByLabel(label));
    }
    @GetMapping(value = ENDPOINT_GET_ALL_MENUS_CONTAINS_TERM_URL)
    public ResponseEntity<List<String>> searchLabelsOfMenuContains(@RequestParam("term") String  term){
        return  ResponseEntity.ok(this.menuService.searchLablesOfMenuContainsTerm(term));
    }

    @GetMapping(value = ENDPOINT_GET_ALL_MEALS_BY_TYPE_URL)
    public ResponseEntity<List<MealDtOut>> getMealsByType(@RequestParam("mealType") String mealType) {
        return ResponseEntity.ok(this.mealService.getMealsByType(mealType));
    }

    @GetMapping(value = ENDPOINT_GET_ALL_MEALS_URL)
    public ResponseEntity<List<MealDtOut>> getOnlyAvailableMeals() {
        return ResponseEntity.ok(this.mealService.getOnlyAvailableMeals());
    }


    @GetMapping(value = ENDPOINT_GET_ALL_MENUS_URL)
    public ResponseEntity<List<MenuDtOut>> getAllMenus() {
        return ResponseEntity.ok(this.menuService.getAvailableMenu());
    }

}
