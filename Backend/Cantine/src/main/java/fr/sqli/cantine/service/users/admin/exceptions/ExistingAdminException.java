package fr.sqli.cantine.service.users.admin.exceptions;

public class ExistingAdminException extends Exception{
    public ExistingAdminException(String message) {
        super(message);
    }
}
