package fr.sqli.cantine.service.food.exceptions;

public class FoodNotFoundException extends Exception {
    public FoodNotFoundException(String message) {
        super(message);
    }
}
