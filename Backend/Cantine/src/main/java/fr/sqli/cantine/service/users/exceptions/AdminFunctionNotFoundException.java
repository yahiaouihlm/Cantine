package fr.sqli.cantine.service.users.exceptions;

public class AdminFunctionNotFoundException extends Exception{
    public AdminFunctionNotFoundException(String message) {
        super(message);
    }
}
