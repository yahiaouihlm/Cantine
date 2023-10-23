package fr.sqli.cantine.service.admin.exceptions;

public class AdminFunctionNotFoundException extends Exception{
    public AdminFunctionNotFoundException(String message) {
        super(message);
    }
}
