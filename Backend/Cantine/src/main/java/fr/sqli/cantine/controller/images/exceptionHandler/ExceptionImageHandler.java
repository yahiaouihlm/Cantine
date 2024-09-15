package fr.sqli.cantine.controller.images.exceptionHandler;


import fr.sqli.cantine.dto.out.ExceptionDtout;
import fr.sqli.cantine.service.images.exception.ImagePathException;
import fr.sqli.cantine.service.images.exception.InvalidFormatImageException;
import fr.sqli.cantine.service.images.exception.InvalidImageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.io.FileNotFoundException;
import java.io.IOException;

@ControllerAdvice
public class ExceptionImageHandler {


    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<ExceptionDtout> exceptionHandler(FileNotFoundException exception) {
        return new ResponseEntity<ExceptionDtout>(new ExceptionDtout("IMAGE NOT FOUND "), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ExceptionDtout> exceptionHandler(MaxUploadSizeExceededException exception) {
        return new ResponseEntity<ExceptionDtout>(new ExceptionDtout("FILE SIZE  TOO BIG "), HttpStatus.NOT_ACCEPTABLE);

    }

    @ExceptionHandler(InvalidFormatImageException.class)
    public ResponseEntity<ExceptionDtout> exceptionHandler(InvalidFormatImageException exception) {
        return new ResponseEntity<ExceptionDtout>(new ExceptionDtout(exception.getMessage()), HttpStatus.NOT_ACCEPTABLE);
    }


    @ExceptionHandler(InvalidImageException.class)
    public ResponseEntity<ExceptionDtout> exceptionHandler(InvalidImageException exception) {
        return new ResponseEntity<ExceptionDtout>(new ExceptionDtout(exception.getMessage()), HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(ImagePathException.class)
    public ResponseEntity<ExceptionDtout> exceptionHandler(ImagePathException exception) {
        return new ResponseEntity<ExceptionDtout>(new ExceptionDtout(exception.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ExceptionDtout> exceptionHandler(IOException exception) {
        return new ResponseEntity<>(new ExceptionDtout(" A PROBLEM WAS APPEARED IN TRYING TO SAVE YOUR IMAGE "), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handle HttpMediaTypeNotSupportedException exception when the Image is not in the correct format unsupported media type
     *
     * @param e HttpMediaTypeNotSupportedException exception
     * @return ResponseEntity<ExceptionDtout>   with the message of the exception
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ExceptionDtout> handleRemoveMealAdminException(HttpMediaTypeNotSupportedException e) {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ExceptionDtout(e.getMessage().toUpperCase()));
    }


}
