package fr.sqli.cantine.controller.food.exceptionHandler;


import fr.sqli.cantine.dto.out.ExceptionDtout;
import fr.sqli.cantine.service.food.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class FoodExceptionHandler {

    @ExceptionHandler(value = InvalidFoodInformationException.class)
    public ResponseEntity<ExceptionDtout> handleInvalidMealInformationAdminException(InvalidFoodInformationException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionDtout(e.getMessage().toUpperCase()));
    }

    @ExceptionHandler(value = ExistingFoodException.class)
    public ResponseEntity<ExceptionDtout> handleExistingFoodException(ExistingFoodException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ExceptionDtout(e.getMessage().toUpperCase()));
    }


    @ExceptionHandler(UnavailableFoodException.class)
    public ResponseEntity<ExceptionDtout> handleExistingMenu(UnavailableFoodException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ExceptionDtout(e.getMessage().toUpperCase()));
    }

    @ExceptionHandler(value = FoodNotFoundException.class)
    public ResponseEntity<ExceptionDtout> handleFoodNotFoundException(FoodNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExceptionDtout(e.getMessage().toUpperCase()));
    }



    @ExceptionHandler(RemoveFoodException.class)
    public ResponseEntity<ExceptionDtout> handleRemoveMealAdminException(RemoveFoodException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ExceptionDtout(e.getMessage()));
    }

}
