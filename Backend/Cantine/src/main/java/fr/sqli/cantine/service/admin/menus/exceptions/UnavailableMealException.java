package fr.sqli.Cantine.service.admin.menus.exceptions;

public class UnavailableMealException extends Exception{
    public UnavailableMealException(String message) {
        super(message);
    }
}
