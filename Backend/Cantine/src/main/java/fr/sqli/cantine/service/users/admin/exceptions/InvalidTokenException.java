package fr.sqli.cantine.service.users.admin.exceptions;

public class InvalidTokenException  extends Exception{
    public InvalidTokenException(String message) {
        super(message);
    }
}
