package fr.sqli.cantine.service.order.exception;

public class UnavailableFoodForOrderException extends Exception {
    public UnavailableFoodForOrderException(String message) {
        super(message);
    }
}
