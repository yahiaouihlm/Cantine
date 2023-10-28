package fr.sqli.cantine.service.food.menus.exceptions;

public class NoMealInMenuException extends Exception{

    public NoMealInMenuException(String message) {
        super(message);
    }
}
