package fr.sqli.cantine.service.order.exception;

public class UnavailableFoodException extends   Exception{
    public UnavailableFoodException(String message) {
        super(message);
    }
}
