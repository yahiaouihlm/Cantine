package fr.sqli.Cantine.controller.admin.meals;


import fr.sqli.Cantine.dto.in.food.MealDtoIn;
import fr.sqli.Cantine.dto.out.ResponseDtout;
import fr.sqli.Cantine.dto.out.food.MealDtout;
import fr.sqli.Cantine.service.admin.meals.MealService;
import fr.sqli.Cantine.service.admin.meals.exceptions.ExistingMealException;
import fr.sqli.Cantine.service.admin.meals.exceptions.InvalidMealInformationException;
import fr.sqli.Cantine.service.admin.meals.exceptions.MealNotFoundException;
import fr.sqli.Cantine.service.admin.meals.exceptions.RemoveMealAdminException;
import fr.sqli.Cantine.service.admin.menus.exceptions.InvalidMenuInformationException;
import fr.sqli.Cantine.service.images.exception.ImagePathException;
import fr.sqli.Cantine.service.images.exception.InvalidImageException;
import fr.sqli.Cantine.service.images.exception.InvalidFormatImageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static fr.sqli.Cantine.controller.admin.meals.IMealController.MEALS_BASIC_URL_ADMIN;


@RestController
@RequestMapping(value = MEALS_BASIC_URL_ADMIN)
@CrossOrigin(origins = "http://localhost:4200")
public class MealController implements IMealController {


    private final MealService mealService;

    @Autowired
    public MealController(MealService mealService) {
        this.mealService = mealService;
    }



    @Override
    public ResponseEntity<ResponseDtout> updateMeal(@ModelAttribute MealDtoIn mealDtoIn) throws InvalidMealInformationException, MealNotFoundException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, ExistingMealException, InvalidMenuInformationException {
        this.mealService.updateMeal(mealDtoIn);
        return ResponseEntity.ok().body(new ResponseDtout(MEAL_UPDATED_SUCCESSFULLY));
    }


    @Override
    public ResponseEntity<ResponseDtout> deleteMeal(@RequestParam("idMeal") Integer idMeal) throws MealNotFoundException, InvalidMealInformationException, RemoveMealAdminException, ImagePathException {
        this.mealService.removeMeal(idMeal);
        return ResponseEntity.ok(MEAL_DELETED_SUCCESSFULLY);
    }


    @Override
    public ResponseEntity<ResponseDtout> addMeal(@ModelAttribute MealDtoIn newMeal) throws InvalidMealInformationException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, ExistingMealException, InvalidMenuInformationException {
        this.mealService.addMeal(newMeal);
        return ResponseEntity.ok().body(new ResponseDtout(MEAL_ADDED_SUCCESSFULLY));
    }



    @Override
    public ResponseEntity<MealDtout> getMealByID(@RequestParam("idMeal") Integer idMeal) throws MealNotFoundException, InvalidMealInformationException {
        var meal = this.mealService.getMealByID(idMeal);
        return ResponseEntity.ok(meal);
    }



}