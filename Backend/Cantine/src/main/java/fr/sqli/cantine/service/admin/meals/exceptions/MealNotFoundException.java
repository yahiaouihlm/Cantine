package fr.sqli.cantine.service.admin.meals.exceptions;

public class MealNotFoundException extends  Exception {
    public MealNotFoundException(String message) {
        super(message);
    }

}
