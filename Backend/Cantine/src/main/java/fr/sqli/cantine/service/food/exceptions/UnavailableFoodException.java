package fr.sqli.cantine.service.food.exceptions;

public class UnavailableFoodException extends Exception {
    public UnavailableFoodException(String message) {
        super(message);
    }
}
