package fr.sqli.Cantine.service.admin.menus.exceptions;

public class ExistingMenuException extends  Exception{

    public ExistingMenuException(String message) {
        super(message);
    }
}
