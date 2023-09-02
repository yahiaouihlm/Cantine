package fr.sqli.Cantine.service.order.exception;

public class OrderLimitExceededException extends Exception{
    public OrderLimitExceededException(String message) {
        super(message);
    }
}
