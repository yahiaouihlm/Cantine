package fr.sqli.Cantine.controller.admin.meals.exceptions;

public class ExistingMeal extends Exception {
    public ExistingMeal(String message) {
        super(message);
    }
}
