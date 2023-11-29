package fr.sqli.cantine.service.users.exceptions;

public class ExpiredToken  extends  Exception{
    public ExpiredToken(String message) {
        super(message);
    }
}
