package fr.sqli.cantine.service.food.menus.exceptions;

public class UnavailableMealException extends Exception{
    public UnavailableMealException(String message) {
        super(message);
    }
}
