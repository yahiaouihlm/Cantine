package fr.sqli.cantine.security.exceptionHandler;


import fr.sqli.cantine.dto.out.ExceptionDtout;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class AuthExceptionHandler {


    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionDtout> handleAdminFunctionNotFound(BadCredentialsException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ExceptionDtout(e.getMessage()));
    }


    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ResponseEntity<ExceptionDtout> handleAdminFunctionNotFound(InsufficientAuthenticationException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ExceptionDtout("You are not authorized to access this resource"));
    }


}
