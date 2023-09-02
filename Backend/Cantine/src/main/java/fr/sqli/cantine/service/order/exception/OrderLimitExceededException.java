package fr.sqli.cantine.service.order.exception;

public class OrderLimitExceededException extends Exception{
    public OrderLimitExceededException(String message) {
        super(message);
    }
}
