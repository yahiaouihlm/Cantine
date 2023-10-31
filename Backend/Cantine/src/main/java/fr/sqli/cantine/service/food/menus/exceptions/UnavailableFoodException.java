package fr.sqli.cantine.service.food.menus.exceptions;

public class UnavailableFoodException extends Exception{
    public UnavailableFoodException(String message) {
        super(message);
    }
}
