package fr.sqli.cantine.service.food.exceptions;

public class InvalidFoodInformationException extends Exception {
    public InvalidFoodInformationException(String message) {
        super(message);
    }

    public InvalidFoodInformationException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidFoodInformationException(Throwable cause) {
        super(cause);
    }

    public InvalidFoodInformationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
