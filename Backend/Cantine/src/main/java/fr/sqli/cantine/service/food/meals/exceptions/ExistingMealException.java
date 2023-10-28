package fr.sqli.cantine.service.food.meals.exceptions;

public class ExistingMealException extends Exception {
    public ExistingMealException(String message) {
        super(message);
    }
}
