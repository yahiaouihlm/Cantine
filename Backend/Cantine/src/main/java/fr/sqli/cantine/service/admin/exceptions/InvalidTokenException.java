package fr.sqli.cantine.service.admin.exceptions;

public class InvalidTokenException  extends Exception{
    public InvalidTokenException(String message) {
        super(message);
    }
}
