package fr.sqli.Cantine.service.order.exception;

public class OrderNotFoundException  extends  Exception{
    public OrderNotFoundException(String message) {
        super(message);
    }
}
