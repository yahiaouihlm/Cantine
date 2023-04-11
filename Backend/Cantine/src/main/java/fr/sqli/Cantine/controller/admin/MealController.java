package fr.sqli.Cantine.controller.admin;


import fr.sqli.Cantine.dto.out.MealDtout;
import fr.sqli.Cantine.service.admin.MealService;
import fr.sqli.Cantine.service.admin.exceptions.InvalidMealInformationAdminException;
import fr.sqli.Cantine.service.admin.exceptions.MealNotFoundAdminException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MealController  implements  IAdminEndPoints{


    private final  MealService mealService;

     @Autowired
     public MealController (MealService mealService) {
        this.mealService =  mealService;
    }

    @GetMapping(value =  ENDPOINT_GET_ONE_MEAL_URL )
    public MealDtout getMealByID(@RequestParam("id") Integer id) throws MealNotFoundAdminException, InvalidMealInformationAdminException {
        return this.mealService.getMealByID(id);
    }



}
