package fr.sqli.cantine.controller.food.impl;


import fr.sqli.cantine.controller.food.IMealController;
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
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static fr.sqli.cantine.controller.food.IMealController.MEALS_BASIC_URL_ADMIN;


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
    public ResponseEntity<ResponseDtout> updateMeal(@ModelAttribute MealDtoIn mealDtoIn) throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, InvalidFoodInformationException, ExistingFoodException, FoodNotFoundException {
        this.mealService.updateMeal(mealDtoIn);
        return ResponseEntity.ok().body(new ResponseDtout(MEAL_UPDATED_SUCCESSFULLY));
    }


    @Override
    public ResponseEntity<ResponseDtout> deleteMeal(String uuidMeal) throws FoodNotFoundException, RemoveFoodException, ImagePathException, InvalidFoodInformationException {
        this.mealService.deleteMeal(uuidMeal);
        return ResponseEntity.ok().body(new ResponseDtout(MEAL_DELETED_SUCCESSFULLY));
    }


    @Override
    public ResponseEntity<ResponseDtout> addMeal(MealDtoIn newMeal) throws InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, InvalidFoodInformationException, ExistingFoodException {
        this.mealService.addMeal(newMeal);
        return ResponseEntity.ok().body(new ResponseDtout(MEAL_ADDED_SUCCESSFULLY));
    }

    @Override
    public ResponseEntity<List<MealDtOut>> getAllMeal() {
          return ResponseEntity.ok(this.mealService.getAllMeals());
    }


    @Override
    public ResponseEntity<MealDtOut> getMealByUUID(String uuidMeal) throws FoodNotFoundException, InvalidFoodInformationException {
        var mealdtout = this.mealService.getMealByUUID(uuidMeal);
        return ResponseEntity.ok(mealdtout);
    }


}