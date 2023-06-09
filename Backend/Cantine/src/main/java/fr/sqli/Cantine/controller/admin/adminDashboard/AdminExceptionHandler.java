package fr.sqli.Cantine.controller.admin.adminDashboard;


import fr.sqli.Cantine.dto.out.ExceptionDtout;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class AdminExceptionHandler {

    @ExceptionHandler(AdminFunctionNotFoundException.class)
    public ResponseEntity<ExceptionDtout> handleAdminFunctionNotFound(  AdminFunctionNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExceptionDtout(e.getMessage()));
    }


    @ExceptionHandler(StudentClassNotFoundException.class)
    public ResponseEntity<ExceptionDtout> handleAdminNotFound(StudentClassNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExceptionDtout(e.getMessage()));
    }

    @ExceptionHandler(ExistingStudentClassException.class)
    public ResponseEntity<ExceptionDtout> handleExistingStudentClass(ExistingStudentClassException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ExceptionDtout(e.getMessage()));
    }
    @ExceptionHandler(InvalidStudentClassException.class)
    public ResponseEntity<ExceptionDtout> handleInvalidStudentClass(InvalidStudentClassException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionDtout(e.getMessage()));
    }
    @ExceptionHandler(AdminNotFound.class)
    public ResponseEntity<ExceptionDtout> handleAdminNotFound(AdminNotFound e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExceptionDtout(e.getMessage()));
    }
    @ExceptionHandler(ExistingAdminException.class)
    public ResponseEntity<ExceptionDtout> handleExistingAdmin(ExistingAdminException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ExceptionDtout(e.getMessage()));
    }
    @ExceptionHandler(InvalidPersonInformationException.class)
    public ResponseEntity<ExceptionDtout> handleExistingMeal(InvalidPersonInformationException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionDtout(e.getMessage()));
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ExceptionDtout> handleInvalidToken(InvalidTokenException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionDtout(e.getMessage()));
    }
}
