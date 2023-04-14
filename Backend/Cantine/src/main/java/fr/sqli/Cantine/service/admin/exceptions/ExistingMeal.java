package fr.sqli.Cantine.service.admin.exceptions;

public class ExistingMeal extends Exception {
    public ExistingMeal(String message) {
        super(message);
    }
}
