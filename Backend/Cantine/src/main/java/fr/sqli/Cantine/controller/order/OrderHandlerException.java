package fr.sqli.Cantine.controller.order;


import fr.sqli.Cantine.dto.out.ExceptionDtout;
import fr.sqli.Cantine.service.order.exception.InsufficientBalanceException;
import fr.sqli.Cantine.service.order.exception.InvalidOrderException;
import fr.sqli.Cantine.service.order.exception.UnavailableFoodException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class OrderHandlerException {



    @ExceptionHandler(UnavailableFoodException.class)
    public ResponseEntity<ExceptionDtout> UnavailableMealOrMenu(UnavailableFoodException e) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new ExceptionDtout(e.getMessage()));
    }

    @ExceptionHandler(InvalidOrderException.class)
    public ResponseEntity<ExceptionDtout> handleInvalidOrder(InvalidOrderException  e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionDtout(e.getMessage()));
    }


    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ExceptionDtout> handleInsufficientBalance(InsufficientBalanceException e) {
        return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(new ExceptionDtout(e.getMessage()));
    }




}
