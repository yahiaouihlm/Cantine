package fr.sqli.cantine.service.admin.menus.exceptions;

public class NoMealInMenuException extends Exception{

    public NoMealInMenuException(String message) {
        super(message);
    }
}
