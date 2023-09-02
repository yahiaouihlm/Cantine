package fr.sqli.cantine.service.admin.menus.exceptions;

public class UnavailableMealException extends Exception{
    public UnavailableMealException(String message) {
        super(message);
    }
}
