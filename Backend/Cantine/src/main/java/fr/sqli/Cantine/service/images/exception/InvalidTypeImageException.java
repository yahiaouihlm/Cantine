package fr.sqli.Cantine.controller.images.exception;

public class InvalidTypeImageException  extends  Exception{

        /**
        *  Exception will be  thrown  when  the  image  type  is  not supported
        *  Image  type  supported :  jpg ,  jpeg ,  png
        *  @param message the detail message.
        */
        public InvalidTypeImageException(String message) {
            super(message);
        }
}
