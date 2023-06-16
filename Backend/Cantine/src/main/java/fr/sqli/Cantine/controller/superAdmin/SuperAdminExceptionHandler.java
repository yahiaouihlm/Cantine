package fr.sqli.Cantine.controller.superAdmin;

import fr.sqli.Cantine.dto.out.ExceptionDtout;
import fr.sqli.Cantine.service.superAdmin.exception.ExistingTax;
import fr.sqli.Cantine.service.superAdmin.exception.InvalidTaxException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class SuperAdminExceptionHandler {


    @ExceptionHandler(InvalidTaxException.class)
    public ResponseEntity<ExceptionDtout> handleHttpMessageNotReadableException(InvalidTaxException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionDtout(e.getMessage().toUpperCase()));
    }

    @ExceptionHandler(ExistingTax.class)
    public ResponseEntity<ExceptionDtout> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ExceptionDtout(e.getMessage().toUpperCase()));
    }
}