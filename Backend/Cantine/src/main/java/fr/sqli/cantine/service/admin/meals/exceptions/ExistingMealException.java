package fr.sqli.Cantine.service.admin.meals.exceptions;

public class ExistingMealException extends Exception {
    public ExistingMealException(String message) {
        super(message);
    }
}
