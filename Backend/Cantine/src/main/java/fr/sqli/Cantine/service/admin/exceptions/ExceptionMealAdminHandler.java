package fr.sqli.Cantine.service.admin.exceptions;

import fr.sqli.Cantine.dto.out.ExceptionDtout;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionMealAdminHandler {

    /**
     * Handle ExistingMeal exception when the meal with the same  label ,  category and description already exist
     *
     * @param e ExistingMeal exception when the meal with the same  label ,  category and description already exist
     * @return ResponseEntity<ExceptionDtout> with the message of the exception
     */
    @ExceptionHandler(ExistingMeal.class)
    public ResponseEntity<ExceptionDtout> handleExistingMeal(ExistingMeal e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ExceptionDtout(e.getMessage().toUpperCase()));
    }


    /**
     * Handle MethodArgumentNotValidException exception when  one of the arguments is not valid  ex:  for Integer argument  rhe server receive a String
     *
     * @param e MealAlreadyExistAdminException exception
     * @return ResponseEntity<ExceptionDtout> with the message of the exception
     */

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionDtout> handleMealAlreadyExistAdminException(MethodArgumentNotValidException e) {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ExceptionDtout("ARGUMENT NOT VALID "));
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

    /**
     * Handle RemoveMealAdminException exception when the meal can not be deleted because it is present in a menu
     *
     * @param e RemoveMealAdminException
     * @return ResponseEntity<ExceptionDtout> with the message of the exception
     */


    @ExceptionHandler(RemoveMealAdminException.class)
    public ResponseEntity<ExceptionDtout> handleRemoveMealAdminException(RemoveMealAdminException e) {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ExceptionDtout(e.getMessage().toUpperCase()));
    }

    /**
     * Handle MealNotFoundAdminException exception when No meal found with this id
     *
     * @param e MealNotFoundAdminException
     * @return ResponseEntity<ExceptionDtout> with the message of the exception
     */
    @ExceptionHandler(value = MealNotFoundAdminException.class)
    public ResponseEntity<ExceptionDtout> handleMealNotFoundAdminException(MealNotFoundAdminException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExceptionDtout(e.getMessage().toUpperCase()));
    }


    /**
     * Handle InvalidMealInformationAdminException exception  when the meal information is invalid
     *
     * @param e InvalidMealInformationAdminException
     * @return ResponseEntity<ExceptionDtout> with the message of the exception
     */
    @ExceptionHandler(value = InvalidMealInformationAdminException.class)
    public ResponseEntity<ExceptionDtout> handleInvalidMealInformationAdminException(InvalidMealInformationAdminException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionDtout(e.getMessage().toUpperCase()));
    }


    /**
     * <H3> ** DATA INTEGRITY VIOLATION EXCEPTION **</H3>
     * <h4>
     * THIS EXCEPTION MUST NOT BE THROWN IN THE SERVICE LAYER OR IN DATA ACCESS LAYER  BECAUSE WE HAVE ADD EXISTING MEAL EXCEPTION
     * TO  CHECK IF THE MEAL ALREADY EXIST IN THE DATABASE BEFORE ADDING IT
     * </h4>
     * <p>
     * <p>
     * <p>
     * Handle DataIntegrityViolationException exception when the meal  with same label , category , price and description already exist in the database
     *
     * @param e DataIntegrityViolationException exception
     * @return ResponseEntity<ExceptionDtout> with the message of the exception
     */

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ExceptionDtout> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ExceptionDtout("THE MEAL  WITH SAME LABEL , CATEGORY , PRICE AND DESCRIPTION ALREADY EXIST "));
    }


}
