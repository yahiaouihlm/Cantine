package fr.sqli.cantine.service.users.admin.exceptions;

public class ExpiredToken  extends  Exception{
    public ExpiredToken(String message) {
        super(message);
    }
}
