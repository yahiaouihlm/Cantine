package fr.sqli.cantine.service.users.exceptions;

public class UnknownUser extends Exception{
    public UnknownUser(String message) {
        super(message);
    }
}
