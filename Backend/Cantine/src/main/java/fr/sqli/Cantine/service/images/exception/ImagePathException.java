package fr.sqli.Cantine.controller.images.exception;

public class ImagePathException  extends  Exception{


    /**
     *  Exception will be  thrown  when  the  path  of image  is  not valid
     *  @param message the detail message.
     */
    public ImagePathException(String message) {
        super(message);
    }
}
