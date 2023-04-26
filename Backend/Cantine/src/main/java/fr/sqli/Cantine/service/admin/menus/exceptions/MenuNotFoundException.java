package fr.sqli.Cantine.service.admin.menus.exceptions;

public class MenuNotFoundException extends  Exception{
    public MenuNotFoundException(String message) {
        super(message);
    }
}
