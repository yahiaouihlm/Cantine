package fr.sqli.cantine.service.order.exception;

public class UnableToCancelOrderException extends Exception {

    public UnableToCancelOrderException(String message) {
        super(message);
    }
}
