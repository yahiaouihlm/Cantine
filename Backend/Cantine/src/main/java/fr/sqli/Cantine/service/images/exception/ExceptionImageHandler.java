package fr.sqli.Cantine.service.images.exception;


import fr.sqli.Cantine.dto.out.ExceptionDtout;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.io.FileNotFoundException;
import java.io.IOException;

@ControllerAdvice
public class ExceptionImageHandler {

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity <ExceptionDtout> exceptionHandler (FileNotFoundException exception){
        return new ResponseEntity<ExceptionDtout>(new ExceptionDtout("IMAGE NOT FOUND ") , HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public  ResponseEntity<ExceptionDtout> exceptionHandler (MaxUploadSizeExceededException exception){
        return new ResponseEntity<ExceptionDtout>(new ExceptionDtout("IMAGE TO BIG ") , HttpStatus.NOT_ACCEPTABLE);

    }

    @ExceptionHandler(InvalidTypeImageException.class)
    public ResponseEntity <ExceptionDtout> exceptionHandler (InvalidTypeImageException exception){
        return new ResponseEntity<ExceptionDtout>(new ExceptionDtout(exception.getMessage()) , HttpStatus.NOT_ACCEPTABLE);
    }


    @ExceptionHandler(InvalidImageException.class)
    public ResponseEntity <ExceptionDtout> exceptionHandler (InvalidImageException exception){
        return new ResponseEntity<ExceptionDtout>(new ExceptionDtout(exception.getMessage()) , HttpStatus.NOT_ACCEPTABLE);
    }

   @ExceptionHandler(ImagePathException.class)
    public ResponseEntity <ExceptionDtout> exceptionHandler (ImagePathException exception){
        return new ResponseEntity<ExceptionDtout>(new ExceptionDtout(exception.getMessage()) , HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity <ExceptionDtout> exceptionHandler (IOException exception){
        return new ResponseEntity<ExceptionDtout>(new ExceptionDtout(" A PROBLEM WAS APPEARED IN TRYING TO SAVE YOUR IMAGE "   ) , HttpStatus.INTERNAL_SERVER_ERROR);
    }



}
