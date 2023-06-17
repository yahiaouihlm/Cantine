package fr.sqli.Cantine.controller.order;


import fr.sqli.Cantine.dto.out.ExceptionDtout;
import fr.sqli.Cantine.service.order.exception.InsufficientBalanceException;
import fr.sqli.Cantine.service.order.exception.InvalidOrderException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class OrderHandlerException {



    @ExceptionHandler(InvalidOrderException.class)
    public ResponseEntity<ExceptionDtout> handleInvalidOrder(InvalidOrderException  e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionDtout(e.getMessage()));
    }


    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ExceptionDtout> handleInsufficientBalance(HttpMessageNotReadableException e) {
        return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(new ExceptionDtout(e.getMessage()));
    }




}
