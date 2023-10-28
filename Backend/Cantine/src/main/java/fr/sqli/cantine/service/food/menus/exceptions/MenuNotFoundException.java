package fr.sqli.cantine.service.food.menus.exceptions;

public class MenuNotFoundException extends  Exception{
    public MenuNotFoundException(String message) {
        super(message);
    }
}
