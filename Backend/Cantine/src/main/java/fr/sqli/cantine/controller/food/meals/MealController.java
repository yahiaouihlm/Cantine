package fr.sqli.cantine.controller.food.meals;


import fr.sqli.cantine.dto.in.food.MealDtoIn;
import fr.sqli.cantine.dto.out.ResponseDtout;
import fr.sqli.cantine.dto.out.food.MealDtout;
import fr.sqli.cantine.service.food.exceptions.InvalidFoodInformationException;
import fr.sqli.cantine.service.food.meals.MealService;
import fr.sqli.cantine.service.food.meals.exceptions.ExistingMealException;
import fr.sqli.cantine.service.food.meals.exceptions.MealNotFoundException;
import fr.sqli.cantine.service.food.meals.exceptions.RemoveMealAdminException;
import fr.sqli.cantine.service.food.menus.exceptions.InvalidMenuInformationException;
import fr.sqli.cantine.service.images.exception.ImagePathException;
import fr.sqli.cantine.service.images.exception.InvalidImageException;
import fr.sqli.cantine.service.images.exception.InvalidFormatImageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static fr.sqli.cantine.controller.food.meals.IMealController.MEALS_BASIC_URL_ADMIN;


@RestController
@RequestMapping(value = MEALS_BASIC_URL_ADMIN)
@CrossOrigin(origins = "http://localhost:4200")
public class    MealController implements IMealController {


    private final MealService mealService;

    @Autowired
    public MealController(MealService mealService) {
        this.mealService = mealService;
    }



    @Override
    public ResponseEntity<ResponseDtout> updateMeal(@ModelAttribute MealDtoIn mealDtoIn) throws MealNotFoundException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, ExistingMealException, InvalidMenuInformationException, InvalidFoodInformationException {
        this.mealService.updateMeal(mealDtoIn);
        return ResponseEntity.ok().body(new ResponseDtout(MEAL_UPDATED_SUCCESSFULLY));
    }


    @Override
    public ResponseEntity<ResponseDtout> deleteMeal(String idMeal) throws MealNotFoundException,  RemoveMealAdminException, ImagePathException, InvalidFoodInformationException {
        this.mealService.removeMeal(idMeal);
        return ResponseEntity.ok().body ( new ResponseDtout(MEAL_DELETED_SUCCESSFULLY));
    }


    @Override
    public ResponseEntity<ResponseDtout> addMeal( MealDtoIn newMeal) throws  InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, ExistingMealException, InvalidMenuInformationException, InvalidFoodInformationException {
        this.mealService.addMeal(newMeal);
        return ResponseEntity.ok().body(new ResponseDtout(MEAL_ADDED_SUCCESSFULLY));
    }



    @Override
    public ResponseEntity<MealDtout> getMealByUUID(String idMeal) throws MealNotFoundException, InvalidFoodInformationException {
        var meal = this.mealService.getMealByUUID(idMeal);
        return ResponseEntity.ok(meal);
    }



}