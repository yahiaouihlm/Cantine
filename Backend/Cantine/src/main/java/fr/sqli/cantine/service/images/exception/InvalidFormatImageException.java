package fr.sqli.cantine.service.images.exception;

public class InvalidFormatImageException extends Exception {

    /**
     * Exception will be  thrown  when  the  image  type  is  not supported
     * Image  type  supported :  jpg ,  jpeg ,  png
     *
     * @param message the detail message.
     */
    public InvalidFormatImageException(String message) {
        super(message);
    }
}
