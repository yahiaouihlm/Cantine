package fr.sqli.cantine.controller.admin.meals;

import fr.sqli.cantine.dto.out.ExceptionDtout;
import fr.sqli.cantine.service.food.meals.exceptions.ExistingMealException;
import fr.sqli.cantine.service.food.meals.exceptions.InvalidMealInformationException;
import fr.sqli.cantine.service.food.meals.exceptions.MealNotFoundException;
import fr.sqli.cantine.service.food.meals.exceptions.RemoveMealAdminException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class MealExceptionsHandler {



    /**
     * Handle ExistingMeal exception when the meal with the same  label ,  category and description already exist
     *
     * @param e ExistingMeal exception when the meal with the same  label ,  category and description already exist
     * @return ResponseEntity<ExceptionDtout> with the message of the exception
     */
    @ExceptionHandler(ExistingMealException.class)
    public ResponseEntity<ExceptionDtout> handleExistingMeal(ExistingMealException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ExceptionDtout(e.getMessage()));
    }





    /**
     * Handle RemoveMealAdminException exception when the meal can not be deleted because it is present in a menu
     *
     * @param e RemoveMealAdminException
     * @return ResponseEntity<ExceptionDtout> with the message of the exception
     */


    @ExceptionHandler(RemoveMealAdminException.class)
    public ResponseEntity<ExceptionDtout> handleRemoveMealAdminException(RemoveMealAdminException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ExceptionDtout(e.getMessage()));
    }

    /**
     * Handle MealNotFoundAdminException exception when No meal found with this id
     *
     * @param e MealNotFoundAdminException
     * @return ResponseEntity<ExceptionDtout> with the message of the exception
     */
    @ExceptionHandler(value = MealNotFoundException.class)
    public ResponseEntity<ExceptionDtout> handleMealNotFoundAdminException(MealNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExceptionDtout(e.getMessage()));
    }


    /**
     * Handle InvalidMealInformationAdminException exception  when the meal information is invalid
     *
     * @param e InvalidMealInformationAdminException
     * @return ResponseEntity<ExceptionDtout> with the message of the exception
     */
    @ExceptionHandler(value = InvalidMealInformationException.class)
    public ResponseEntity<ExceptionDtout> handleInvalidMealInformationAdminException(InvalidMealInformationException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionDtout(e.getMessage().toUpperCase()));
    }



}