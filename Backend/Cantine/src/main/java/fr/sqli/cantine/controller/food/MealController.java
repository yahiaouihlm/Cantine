package fr.sqli.cantine.controller.food;


import fr.sqli.cantine.dto.in.food.MealDtoIn;
import fr.sqli.cantine.dto.out.ResponseDtout;
import fr.sqli.cantine.dto.out.food.MealDtOut;
import fr.sqli.cantine.service.food.exceptions.ExistingFoodException;
import fr.sqli.cantine.service.food.exceptions.FoodNotFoundException;
import fr.sqli.cantine.service.food.exceptions.InvalidFoodInformationException;
import fr.sqli.cantine.service.food.impl.MealService;
import fr.sqli.cantine.service.food.exceptions.RemoveFoodException;
import fr.sqli.cantine.service.images.exception.ImagePathException;
import fr.sqli.cantine.service.images.exception.InvalidImageException;
import fr.sqli.cantine.service.images.exception.InvalidFormatImageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static fr.sqli.cantine.constants.ConstCantine.ADMIN_ROLE_LABEL;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@RequestMapping(value = MealController.MEALS_BASIC_URL_ADMIN)
public class MealController {

    final static String MEALS_BASIC_URL_ADMIN = "/cantine/admin/api/meals";

    /*------------------ ENDPOINTS ------------------*/
    final String ENDPOINT_ADD_MEAL_URL = "/add";
    final String ENDPOINT_DELETE_MEAL_URL = "/delete";
    final String ENDPOINT_GET_ONE_MEAL_URL = "/get";
    final String ENDPOINT_UPDATE_MEAL_URL = "/update";
    final String ENDPOINT_GET_ONLY_AVAILABLE_MEALS = "/getAvailableMeals";
    final String ENDPOINT_GET_ONLY_UNAVAILABLE_MEALS = "/getUnavailableMeals";
    final String GET_ONLY_MEALS_IN_DELETION_PROCESS_URL = "/getMealsInDeletionProcess";
    final String ENDPOINT_GET_ALL_MEAL = "/getAll";


    /*------------------ MESSAGES ------------------*/
    final String MEAL_ADDED_SUCCESSFULLY = "MEAL ADDED SUCCESSFULLY";
    final String MEAL_DELETED_SUCCESSFULLY = "MEAL DELETED SUCCESSFULLY";
    final String MEAL_UPDATED_SUCCESSFULLY = "MEAL UPDATED SUCCESSFULLY";
    private final MealService mealService;

    @Autowired
    public MealController(MealService mealService) {
        this.mealService = mealService;
    }


    @PreAuthorize("hasAuthority('" + ADMIN_ROLE_LABEL + "')")
    @PostMapping(value = ENDPOINT_UPDATE_MEAL_URL, consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDtout> updateMeal(@ModelAttribute MealDtoIn mealDtoIn) throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, InvalidFoodInformationException, ExistingFoodException, FoodNotFoundException {
        this.mealService.updateMeal(mealDtoIn);
        return ResponseEntity.ok().body(new ResponseDtout(MEAL_UPDATED_SUCCESSFULLY));
    }

    @PreAuthorize("hasAuthority('" + ADMIN_ROLE_LABEL + "')")
    @PostMapping(value = ENDPOINT_DELETE_MEAL_URL)
    public ResponseEntity<ResponseDtout> deleteMeal(@RequestParam("uuidMeal") String uuidMeal) throws FoodNotFoundException, RemoveFoodException, ImagePathException, InvalidFoodInformationException {
        this.mealService.deleteMeal(uuidMeal);
        return ResponseEntity.ok().body(new ResponseDtout(MEAL_DELETED_SUCCESSFULLY));
    }

    @PreAuthorize("hasAuthority('" + ADMIN_ROLE_LABEL + "')")
    @PostMapping(value = ENDPOINT_ADD_MEAL_URL, consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDtout> addMeal(@ModelAttribute MealDtoIn newMeal) throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, InvalidFoodInformationException, ExistingFoodException {
        this.mealService.addMeal(newMeal);
        return ResponseEntity.ok().body(new ResponseDtout(MEAL_ADDED_SUCCESSFULLY));
    }

    @GetMapping(value = ENDPOINT_GET_ONLY_AVAILABLE_MEALS)
    public ResponseEntity<List<MealDtOut>> getAvailableMeals() {
        return ResponseEntity.ok(this.mealService.getAvailableMeals());
    }

    @GetMapping(value = ENDPOINT_GET_ONLY_UNAVAILABLE_MEALS)
    public ResponseEntity<List<MealDtOut>> getUnavailableMeals() {
        return ResponseEntity.ok(this.mealService.getUnavailableMeals());
    }

    @GetMapping(value = GET_ONLY_MEALS_IN_DELETION_PROCESS_URL)
    public ResponseEntity<List<MealDtOut>> getMealsInDeletionProcess() {
        return ResponseEntity.ok(this.mealService.getMealsInDeletionProcess());
    }

    @PreAuthorize("hasAuthority('" + ADMIN_ROLE_LABEL + "')")
    @GetMapping(value = ENDPOINT_GET_ALL_MEAL)
    public ResponseEntity<List<MealDtOut>> getAllMeal() {
        return ResponseEntity.ok(this.mealService.getAllMeals());
    }

    @PreAuthorize("hasAuthority('" + ADMIN_ROLE_LABEL + "')")
    @GetMapping(value = ENDPOINT_GET_ONE_MEAL_URL)
    public ResponseEntity<MealDtOut> getMealByUUID(@RequestParam("uuidMeal") String uuidMeal) throws FoodNotFoundException, InvalidFoodInformationException {
        var mealdtout = this.mealService.getMealByUUID(uuidMeal);
        return ResponseEntity.ok(mealdtout);
    }


}