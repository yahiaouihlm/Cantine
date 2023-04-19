package fr.sqli.Cantine.service.admin.meals.exceptions;

public class ExistingMeal extends Exception {
    public ExistingMeal(String message) {
        super(message);
    }
}
