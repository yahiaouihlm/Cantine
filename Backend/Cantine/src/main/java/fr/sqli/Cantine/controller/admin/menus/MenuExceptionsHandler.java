package fr.sqli.Cantine.controller.admin.menus;

import fr.sqli.Cantine.dto.out.ExceptionDtout;
import fr.sqli.Cantine.service.admin.menus.exceptions.InvalidMenuInformationException;
import fr.sqli.Cantine.service.admin.menus.exceptions.NoMealInMenuException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class MenuExceptionsHandler {


    /**
     * Handle NoMealInMenuException exception when the menu doesn't contain any meal in it
     * @param e NoMealInMenuException exception when the menu doesn't contain any meal in it
     * @return ResponseEntity<ExceptionDtout> with the message of the exception
     */

    @ExceptionHandler(NoMealInMenuException.class)
    public ResponseEntity<ExceptionDtout> handleNoMealInMenu(NoMealInMenuException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionDtout(e.getMessage().toUpperCase()));
    }


    /**
     *  Handle InvalidMenuInformationException exception when the menu information is invalid
     *       it's  mean  empty or null or  invalid
     * @param e InvalidMenuInformationException exception when the menu information is invalid
     * @return ResponseEntity<ExceptionDtout> with the message of the exception
     */
    @ExceptionHandler(InvalidMenuInformationException.class)
    public ResponseEntity<ExceptionDtout> handleExistingMenu(InvalidMenuInformationException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionDtout(e.getMessage().toUpperCase()));
    }


}