package fr.sqli.cantine.service.users.exceptions;

public class ExistingUserException extends Exception {
    public ExistingUserException(String message) {
        super(message);
    }
}
