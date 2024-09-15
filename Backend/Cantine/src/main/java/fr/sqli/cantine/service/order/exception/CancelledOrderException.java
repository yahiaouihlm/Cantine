package fr.sqli.cantine.service.order.exception;

public class CancelledOrderException extends Exception {

    public CancelledOrderException(String message) {
        super(message);
    }
}
