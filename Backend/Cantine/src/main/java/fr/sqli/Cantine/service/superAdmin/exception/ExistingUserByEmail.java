package fr.sqli.Cantine.service.superAdmin.exception;

public class ExistingUserByEmail  extends  Exception{
    public ExistingUserByEmail(String message) {
        super(message);
    }
}
