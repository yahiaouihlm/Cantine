package fr.sqli.cantine.service.users.admin.exceptions;

public class AdminFunctionNotFoundException extends Exception{
    public AdminFunctionNotFoundException(String message) {
        super(message);
    }
}
