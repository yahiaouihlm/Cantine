package fr.sqli.cantine.service.users.exceptions;

public class TokenNotFoundException extends Exception {
    public TokenNotFoundException(String message) {
        super(message);
    }
}
