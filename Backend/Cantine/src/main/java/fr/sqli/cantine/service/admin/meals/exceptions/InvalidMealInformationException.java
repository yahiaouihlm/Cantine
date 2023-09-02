package fr.sqli.cantine.service.admin.meals.exceptions;

public class InvalidMealInformationException extends  Exception {

    public InvalidMealInformationException() {
    }

    public InvalidMealInformationException(String message) {
        super(message);
    }

    public InvalidMealInformationException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidMealInformationException(Throwable cause) {
        super(cause);
    }

    public InvalidMealInformationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
