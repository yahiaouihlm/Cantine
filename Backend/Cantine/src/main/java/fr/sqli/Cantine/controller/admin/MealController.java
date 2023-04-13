package fr.sqli.Cantine.controller.admin;


import fr.sqli.Cantine.dto.in.MealDtoIn;
import fr.sqli.Cantine.dto.out.MealDtout;
import fr.sqli.Cantine.service.admin.MealService;
import fr.sqli.Cantine.service.admin.exceptions.InvalidMealInformationAdminException;
import fr.sqli.Cantine.service.admin.exceptions.MealNotFoundAdminException;
import fr.sqli.Cantine.service.admin.exceptions.RemoveMealAdminException;
import fr.sqli.Cantine.service.images.exception.ImagePathException;
import fr.sqli.Cantine.service.images.exception.InvalidImageException;
import fr.sqli.Cantine.service.images.exception.InvalidTypeImageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static fr.sqli.Cantine.controller.admin.IAdminEndPoints.MEALS_URL_ADMIN;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@RequestMapping(value = MEALS_URL_ADMIN)
public class MealController implements IAdminEndPoints {


    private final MealService mealService;

    @Autowired
    public MealController(MealService mealService) {
        this.mealService = mealService;
    }


    @PutMapping(value = ENDPOINT_UPDATE_MEAL_URL, consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateMeal(@ModelAttribute MealDtoIn mealDtoIn, @RequestParam("idMeal") Integer idMeal) throws InvalidMealInformationAdminException, MealNotFoundAdminException, InvalidTypeImageException, InvalidImageException, ImagePathException, IOException {
        this.mealService.updateMeal(mealDtoIn, idMeal);
        return ResponseEntity.ok(MEAL_UPDATED_SUCCESSFULLY);
    }

    @DeleteMapping(value = ENDPOINT_DELETE_MEAL_URL)
    public ResponseEntity<String> deleteMeal(@RequestParam("idMeal") Integer idMeal) throws MealNotFoundAdminException, InvalidMealInformationAdminException, RemoveMealAdminException, ImagePathException {
        this.mealService.removeMeal(idMeal);
        return ResponseEntity.ok(MEAL_DELETED_SUCCESSFULLY);
    }

    @PostMapping(value = ENDPOINT_ADD_MEAL_URL, consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> addMeal(@ModelAttribute MealDtoIn newMeal) throws InvalidMealInformationAdminException, InvalidTypeImageException, InvalidImageException, ImagePathException, IOException {
        this.mealService.addMeal(newMeal);
        return ResponseEntity.ok(MEAL_ADDED_SUCCESSFULLY);
    }


    @GetMapping(value = ENDPOINT_GET_ONE_MEAL_URL)
    public ResponseEntity<MealDtout> getMealByID(@RequestParam("idMeal") Integer idMeal) throws MealNotFoundAdminException, InvalidMealInformationAdminException {
        var meal = this.mealService.getMealByID(idMeal);
        return ResponseEntity.ok(meal);
    }

    @GetMapping(value = ENDPOINT_GET_ALL_MEALS_URL)
    public ResponseEntity<List<MealDtout>> getAllMeals() {
        var meals = this.mealService.getAllMeals();
        return ResponseEntity.ok(meals);
    }

}