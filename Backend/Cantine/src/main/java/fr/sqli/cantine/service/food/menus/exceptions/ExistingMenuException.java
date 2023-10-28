package fr.sqli.cantine.service.food.menus.exceptions;

public class ExistingMenuException extends  Exception{

    public ExistingMenuException(String message) {
        super(message);
    }
}
