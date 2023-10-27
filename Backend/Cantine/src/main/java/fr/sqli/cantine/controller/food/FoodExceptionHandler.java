package fr.sqli.cantine.controller.food;


import fr.sqli.cantine.dto.out.ExceptionDtout;
import fr.sqli.cantine.service.food.exceptions.InvalidFoodInformationException;
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

}
