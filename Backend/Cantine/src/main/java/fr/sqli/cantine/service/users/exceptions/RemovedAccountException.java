package fr.sqli.cantine.service.users.exceptions;

public class RemovedAccountException extends Exception {
    public RemovedAccountException(String message) {
        super(message);
    }
}
