package fr.sqli.cantine.service.order.exception;

public class OrderNotFoundException  extends  Exception{
    public OrderNotFoundException(String message) {
        super(message);
    }
}
