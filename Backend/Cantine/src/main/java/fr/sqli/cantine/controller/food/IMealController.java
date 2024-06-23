package fr.sqli.cantine.controller.food;

import fr.sqli.cantine.dto.in.food.MealDtoIn;
import fr.sqli.cantine.dto.out.ResponseDtout;
import fr.sqli.cantine.dto.out.food.MealDtOut;
import fr.sqli.cantine.service.food.exceptions.ExistingFoodException;
import fr.sqli.cantine.service.food.exceptions.FoodNotFoundException;
import fr.sqli.cantine.service.food.exceptions.InvalidFoodInformationException;
import fr.sqli.cantine.service.food.exceptions.RemoveFoodException;
import fr.sqli.cantine.service.images.exception.ImagePathException;
import fr.sqli.cantine.service.images.exception.InvalidImageException;
import fr.sqli.cantine.service.images.exception.InvalidFormatImageException;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@CrossOrigin(origins = "http://localhost:4200")
public interface IMealController {
    /*------------------ ENDPOINTS ------------------*/
    String MEALS_BASIC_URL_ADMIN = "/cantine/admin/api/meals";
    String ENDPOINT_ADD_MEAL_URL = "/add";
    String ENDPOINT_DELETE_MEAL_URL = "/delete";
    String ENDPOINT_GET_ONE_MEAL_URL = "/get";
    String ENDPOINT_UPDATE_MEAL_URL = "/update";
    String  ENDPOINT_GET_ONLY_AVAILABLE_MEALS =  "/getAvailableMeals";

    String ENDPOINT_GET_ONLY_UNAVAILABLE_MEALS = "/getUnavailableMeals";

    String GET_ONLY_MEALS_IN_DELETION_PROCESS_URL = "/getMealsInDeletionProcess";
    String  ENDPOINT_GET_ALL_MEAL =  "/getAll";

    /*------------------ MESSAGES ------------------*/
    String MEAL_ADDED_SUCCESSFULLY   = "MEAL ADDED SUCCESSFULLY";
    String MEAL_DELETED_SUCCESSFULLY = "MEAL DELETED SUCCESSFULLY";
    String MEAL_UPDATED_SUCCESSFULLY = "MEAL UPDATED SUCCESSFULLY";


    /*------------------ METHODS ------------------*/
    @PostMapping(value = ENDPOINT_UPDATE_MEAL_URL, consumes = MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<ResponseDtout> updateMeal(@ModelAttribute MealDtoIn mealDtoIn) throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, InvalidFoodInformationException, ExistingFoodException, FoodNotFoundException;


    @PostMapping(value = ENDPOINT_DELETE_MEAL_URL)
    ResponseEntity<ResponseDtout> deleteMeal(@RequestParam("uuidMeal") String uuidMeal) throws FoodNotFoundException, RemoveFoodException, ImagePathException, InvalidFoodInformationException;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = ENDPOINT_ADD_MEAL_URL, consumes = MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<ResponseDtout> addMeal(@ModelAttribute MealDtoIn newMeal) throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, InvalidFoodInformationException, ExistingFoodException;


    @GetMapping(value = ENDPOINT_GET_ONLY_AVAILABLE_MEALS)
    ResponseEntity<List<MealDtOut>> getAvailableMeals();

    @GetMapping(value = ENDPOINT_GET_ONLY_UNAVAILABLE_MEALS)
    ResponseEntity<List<MealDtOut>> getUnavailableMeals();

    @GetMapping(value = GET_ONLY_MEALS_IN_DELETION_PROCESS_URL)
    ResponseEntity<List<MealDtOut>> getMealsInDeletionProcess();

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = ENDPOINT_GET_ALL_MEAL)
    ResponseEntity<List<MealDtOut>> getAllMeal();

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = ENDPOINT_GET_ONE_MEAL_URL)
    ResponseEntity<MealDtOut> getMealByUUID(@RequestParam("uuidMeal") String uuidMeal) throws FoodNotFoundException, InvalidFoodInformationException;


}
