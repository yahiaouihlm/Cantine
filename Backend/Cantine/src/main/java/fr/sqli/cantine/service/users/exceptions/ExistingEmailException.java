package fr.sqli.cantine.service.users.exceptions;

public class ExistingEmailException extends  Exception{
    public ExistingEmailException(String message) {
        super(message);
    }
}
