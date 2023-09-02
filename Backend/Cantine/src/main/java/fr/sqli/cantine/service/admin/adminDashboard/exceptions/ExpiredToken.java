package fr.sqli.cantine.service.admin.adminDashboard.exceptions;

public class ExpiredToken  extends  Exception{
    public ExpiredToken(String message) {
        super(message);
    }
}
