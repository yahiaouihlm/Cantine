package fr.sqli.cantine.service.food.meals.exceptions;

public class MealNotFoundException extends  Exception {
    public MealNotFoundException(String message) {
        super(message);
    }

}
