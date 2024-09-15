package fr.sqli.cantine.service.users.exceptions;

public class InvalidUserInformationException extends Exception {
    public InvalidUserInformationException(String message) {
        super(message);
    }
}
