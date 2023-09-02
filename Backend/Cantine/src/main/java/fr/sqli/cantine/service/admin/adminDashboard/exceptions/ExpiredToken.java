package fr.sqli.Cantine.service.admin.adminDashboard.exceptions;

public class ExpiredToken  extends  Exception{
    public ExpiredToken(String message) {
        super(message);
    }
}
