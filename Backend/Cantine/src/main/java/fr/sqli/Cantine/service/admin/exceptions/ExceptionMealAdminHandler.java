package fr.sqli.Cantine.service.admin.exceptions;

import fr.sqli.Cantine.dto.out.ExceptionDtout;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionMealAdminHandler {
    /**
     * Handle RemoveMealAdminException exception when the meal can not be deleted because it is present in a menu
     *
     * @param e RemoveMealAdminException
     * @return ResponseEntity<ExceptionDtout>
     */

    @ExceptionHandler(RemoveMealAdminException.class)
    public ResponseEntity<ExceptionDtout> handleRemoveMealAdminException(RemoveMealAdminException e) {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ExceptionDtout(e.getMessage()));
    }

    /**
     * Handle MealNotFoundAdminException exception when No meal found with this id
     *
     * @param e MealNotFoundAdminException
     * @return ResponseEntity<ExceptionDtout>
     */
    @ExceptionHandler(value = MealNotFoundAdminException.class)
    public ResponseEntity<ExceptionDtout> handleMealNotFoundAdminException(MealNotFoundAdminException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExceptionDtout(e.getMessage()));
    }


    /**
     * Handle InvalidMealInformationAdminException exception  when the meal information is invalid
     *
     * @param e InvalidMealInformationAdminException
     * @return ResponseEntity<ExceptionDtout>
     */
    @ExceptionHandler(value = InvalidMealInformationAdminException.class)
    public ResponseEntity<ExceptionDtout> handleInvalidMealInformationAdminException(InvalidMealInformationAdminException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionDtout(e.getMessage()));
    }


}
