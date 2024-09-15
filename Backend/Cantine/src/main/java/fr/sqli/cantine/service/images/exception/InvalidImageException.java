package fr.sqli.cantine.service.images.exception;

public class InvalidImageException extends Exception {


    /**
     * Exception will be  thrown  when  the  image  is  not valid
     *
     * @param message the detail message.
     */
    public InvalidImageException(String message) {
        super(message);
    }
}
