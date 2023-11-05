package fr.sqli.cantine.controller.users;

import fr.sqli.cantine.dto.out.ExceptionDtout;
import fr.sqli.cantine.service.users.admin.exceptions.*;
import fr.sqli.cantine.service.users.exceptions.ExistingUserException;
import fr.sqli.cantine.service.users.exceptions.InvalidUserInformationException;
import fr.sqli.cantine.service.users.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class UserExceptionHandler {


    @ExceptionHandler(InvalidUserInformationException.class)
    public ResponseEntity<ExceptionDtout> handleExistingMeal(InvalidUserInformationException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionDtout(e.getMessage()));
    }
    @ExceptionHandler(ExistingUserException.class)
    public ResponseEntity<ExceptionDtout> handleExistingUserException( ExistingUserException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ExceptionDtout(e.getMessage()));
    }


    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ExceptionDtout> handleUserNotFound(UserNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExceptionDtout(e.getMessage()));
    }













    /*---------------------------------------------------------------------*/

    @ExceptionHandler(ExistingStudentClassException.class)
    public ResponseEntity<ExceptionDtout> handleExistingStudentClass(ExistingStudentClassException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ExceptionDtout(e.getMessage()));
    }
    @ExceptionHandler(InvalidStudentClassException.class)
    public ResponseEntity<ExceptionDtout> handleInvalidStudentClass(InvalidStudentClassException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionDtout(e.getMessage()));
    }
    @ExceptionHandler(StudentClassNotFoundException.class)
    public ResponseEntity<ExceptionDtout> handleAdminNotFound(StudentClassNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExceptionDtout(e.getMessage()));
    }

    @ExceptionHandler(AdminFunctionNotFoundException.class)
    public ResponseEntity<ExceptionDtout> handleAdminFunctionNotFound(AdminFunctionNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExceptionDtout(e.getMessage()));
    }

}
