package fr.sqli.Cantine.controller.admin.meals.exceptions;

public class InvalidMealInformationAdminException extends  Exception {

    public InvalidMealInformationAdminException() {
    }

    public InvalidMealInformationAdminException(String message) {
        super(message);
    }

    public InvalidMealInformationAdminException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidMealInformationAdminException(Throwable cause) {
        super(cause);
    }

    public InvalidMealInformationAdminException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
