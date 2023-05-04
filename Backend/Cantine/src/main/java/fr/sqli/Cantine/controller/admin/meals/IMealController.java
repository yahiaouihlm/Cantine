package fr.sqli.Cantine.controller.admin.meals;

import fr.sqli.Cantine.dto.in.food.MealDtoIn;
import fr.sqli.Cantine.dto.out.food.MealDtout;
import fr.sqli.Cantine.service.admin.meals.exceptions.ExistingMealException;
import fr.sqli.Cantine.service.admin.meals.exceptions.InvalidMealInformationException;
import fr.sqli.Cantine.service.admin.meals.exceptions.MealNotFoundAdminException;
import fr.sqli.Cantine.service.admin.meals.exceptions.RemoveMealAdminException;
import fr.sqli.Cantine.service.admin.menus.exceptions.InvalidMenuInformationException;
import fr.sqli.Cantine.service.images.exception.ImagePathException;
import fr.sqli.Cantine.service.images.exception.InvalidImageException;
import fr.sqli.Cantine.service.images.exception.InvalidFormatImageException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

public interface IMealController {
     /*------------------ ENDPOINTS ------------------*/
    String MEALS_URL_ADMIN = "/cantine/api/admin/meals";
    String ENDPOINT_ADD_MEAL_URL = "/add";
    String ENDPOINT_DELETE_MEAL_URL = "/delete";

    String ENDPOINT_UPDATE_MEAL_URL = "/update";
    String ENDPOINT_GET_ONE_MEAL_URL = "/get";
    String ENDPOINT_GET_ALL_MEALS_URL = "/getAll";




    /*------------------ MESSAGES ------------------*/
    String MEAL_ADDED_SUCCESSFULLY = "MEAL ADDED SUCCESSFULLY";
    String MEAL_DELETED_SUCCESSFULLY = "MEAL DELETED SUCCESSFULLY";
    String MEAL_UPDATED_SUCCESSFULLY = "MEAL UPDATED SUCCESSFULLY";




    /*------------------ METHODS ------------------*/

    public ResponseEntity<String> updateMeal(@ModelAttribute MealDtoIn mealDtoIn) throws InvalidMealInformationException, MealNotFoundAdminException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, ExistingMealException, InvalidMenuInformationException ;


    public ResponseEntity<String> deleteMeal(@RequestParam("idMeal") Integer idMeal) throws MealNotFoundAdminException, InvalidMealInformationException, RemoveMealAdminException, ImagePathException ;


    public ResponseEntity<String> addMeal(@ModelAttribute MealDtoIn newMeal) throws InvalidMealInformationException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, ExistingMealException, InvalidMenuInformationException ;



    public ResponseEntity<MealDtout> getMealByID(@RequestParam("idMeal") Integer idMeal) throws MealNotFoundAdminException, InvalidMealInformationException ;


    public ResponseEntity<List<MealDtout>> getAllMeals() ;

}
