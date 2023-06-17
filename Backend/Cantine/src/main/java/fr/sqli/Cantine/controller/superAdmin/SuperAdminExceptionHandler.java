package fr.sqli.Cantine.controller.superAdmin;

import fr.sqli.Cantine.dto.out.ExceptionDtout;
import fr.sqli.Cantine.service.superAdmin.exception.ExistingTax;
import fr.sqli.Cantine.service.superAdmin.exception.InvalidTaxException;
import fr.sqli.Cantine.service.superAdmin.exception.TaxNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class SuperAdminExceptionHandler {

    @ExceptionHandler(TaxNotFoundException.class)
    public ResponseEntity<ExceptionDtout> handleTaxNotFound(TaxNotFoundException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ExceptionDtout(e.getMessage().toUpperCase()));
    }

    @ExceptionHandler(InvalidTaxException.class)
    public ResponseEntity<ExceptionDtout> handleInvalidTax(InvalidTaxException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionDtout(e.getMessage().toUpperCase()));
    }

    @ExceptionHandler(ExistingTax.class)
    public ResponseEntity<ExceptionDtout> handleExistingTax(ExistingTax e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ExceptionDtout(e.getMessage().toUpperCase()));
    }
}
