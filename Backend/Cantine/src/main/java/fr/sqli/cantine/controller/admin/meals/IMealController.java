package fr.sqli.cantine.controller.admin.meals;

import fr.sqli.cantine.dto.in.food.MealDtoIn;
import fr.sqli.cantine.dto.out.ResponseDtout;
import fr.sqli.cantine.dto.out.food.MealDtout;
import fr.sqli.cantine.service.admin.meals.exceptions.ExistingMealException;
import fr.sqli.cantine.service.admin.meals.exceptions.InvalidMealInformationException;
import fr.sqli.cantine.service.admin.meals.exceptions.MealNotFoundException;
import fr.sqli.cantine.service.admin.meals.exceptions.RemoveMealAdminException;
import fr.sqli.cantine.service.admin.menus.exceptions.InvalidMenuInformationException;
import fr.sqli.cantine.service.images.exception.ImagePathException;
import fr.sqli.cantine.service.images.exception.InvalidImageException;
import fr.sqli.cantine.service.images.exception.InvalidFormatImageException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
@CrossOrigin(origins = "http://localhost:4200")
public interface IMealController {
     /*------------------ ENDPOINTS ------------------*/
    String MEALS_BASIC_URL_ADMIN = "/cantine/admin/api/meals";
    String ENDPOINT_ADD_MEAL_URL = "/add";
    String ENDPOINT_DELETE_MEAL_URL = "/delete";
    String ENDPOINT_GET_ONE_MEAL_URL = "/get" ;
    String ENDPOINT_UPDATE_MEAL_URL = "/update";





    /*------------------ MESSAGES ------------------*/
    String MEAL_ADDED_SUCCESSFULLY = "MEAL ADDED SUCCESSFULLY";
    String MEAL_DELETED_SUCCESSFULLY = "MEAL DELETED SUCCESSFULLY";
    String MEAL_UPDATED_SUCCESSFULLY = "MEAL UPDATED SUCCESSFULLY";



    /*------------------ METHODS ------------------*/
    @PutMapping(value = ENDPOINT_UPDATE_MEAL_URL, consumes = MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<ResponseDtout> updateMeal(@ModelAttribute MealDtoIn mealDtoIn) throws InvalidMealInformationException, MealNotFoundException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, ExistingMealException, InvalidMenuInformationException ;


    @DeleteMapping(value = ENDPOINT_DELETE_MEAL_URL)
    ResponseEntity<ResponseDtout> deleteMeal(@RequestParam("idMeal") Integer idMeal) throws MealNotFoundException, InvalidMealInformationException, RemoveMealAdminException, ImagePathException ;

    @PostMapping(value = ENDPOINT_ADD_MEAL_URL, consumes = MULTIPART_FORM_DATA_VALUE)
   ResponseEntity<ResponseDtout> addMeal(@ModelAttribute MealDtoIn newMeal) throws InvalidMealInformationException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, ExistingMealException, InvalidMenuInformationException ;


    @GetMapping(value = ENDPOINT_GET_ONE_MEAL_URL)
     ResponseEntity<MealDtout> getMealByID(@RequestParam("idMeal") Integer idMeal) throws MealNotFoundException, InvalidMealInformationException ;



}
