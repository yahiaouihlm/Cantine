package fr.sqli.cantine.controller.student;

import fr.sqli.cantine.dto.out.ExceptionDtout;
import fr.sqli.cantine.service.student.exceptions.AccountAlreadyActivatedException;
import fr.sqli.cantine.service.student.exceptions.ExistingStudentException;
import fr.sqli.cantine.service.student.exceptions.StudentNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class StudentExceptionHandler  {


    @ExceptionHandler(StudentNotFoundException.class)
    public ResponseEntity<ExceptionDtout> handleStudentNotFound(StudentNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExceptionDtout(e.getMessage()));
    }


    @ExceptionHandler(AccountAlreadyActivatedException.class)
    public ResponseEntity<ExceptionDtout> handleAccountAlreadyActivated(AccountAlreadyActivatedException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ExceptionDtout(e.getMessage()));
    }

    @ExceptionHandler(ExistingStudentException.class)
  public ResponseEntity<ExceptionDtout> handleExistingStudent(ExistingStudentException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body(new ExceptionDtout(e.getMessage()));
  }


}
