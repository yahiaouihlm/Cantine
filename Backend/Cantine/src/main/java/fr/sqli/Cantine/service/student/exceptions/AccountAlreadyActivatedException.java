package fr.sqli.Cantine.service.student.exceptions;

public class AccountAlreadyActivatedException extends Exception{

    public AccountAlreadyActivatedException(String message) {
        super(message);
    }
}
