package fr.sqli.cantine.controller;


import fr.sqli.cantine.dto.out.ExceptionDtout;
import fr.sqli.cantine.service.superAdmin.exception.ExistingUserByEmail;
import jakarta.mail.MessagingException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class GlobalHandlerException {


    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ExceptionDtout> handleMailSendException(MessagingException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ExceptionDtout("A PROBLEM WAS OCCURRED THE  EMAIL  CAN NOT BE  SENDED "));
    }
    @ExceptionHandler(MailSendException.class)
    public ResponseEntity<ExceptionDtout> handleMailSendException(MailSendException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ExceptionDtout("A PROBLEM WAS OCCURRED THE  EMAIL  CAN NOT BE  SENDED "));
    }

    @ExceptionHandler(value = ExistingUserByEmail.class)
    public ResponseEntity<ExceptionDtout> handleExistingUserByEmailException(ExistingUserByEmail e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ExceptionDtout("EMAIL ALREADY EXIST"));
    }
    //  can  not   read  json  format  exception
    @ExceptionHandler  (HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionDtout> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionDtout("INVALID JSON FORMAT"));
    }
       //  Exception  For   Email    Handling
    @ExceptionHandler(value = MailAuthenticationException.class)
    public  ResponseEntity<ExceptionDtout> handleMailAuthenticationException(MailAuthenticationException e){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ExceptionDtout("SERVER ERROR  THE  VALIDATION TOKEN CAN'T BE SEND"));
    }


    /**
     * handle  MissingServletRequestParameterException exception when the server receive a request without a required parameter
     *
     * @param e MissingServletRequestParameterException exception
     * @return ResponseEntity<ExceptionDtout> with the message of the exception
     */

    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public ResponseEntity<ExceptionDtout> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ExceptionDtout("MISSING PARAMETER"));
    }


    /**
     * Handle MethodArgumentNotValidException exception when  one of the arguments is not valid  ex:  for Integer argument  rhe server receive a String
     * it's  made spring can not convert automatically the argument to the correct type
     *
     * @param e MealAlreadyExistAdminException exception
     * @return ResponseEntity<ExceptionDtout> with the message of the exception
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ExceptionDtout> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ExceptionDtout("ARGUMENT NOT VALID"));
    }

    /**
     * Handle MethodArgumentNotValidException exception when  one of the arguments is not valid  ex:  for Integer argument  rhe server receive a String
     * or when the argument is null it's  made spring can not convert automatically the argument to the correct type
     *
     * @param e MealAlreadyExistAdminException exception
     * @return ResponseEntity<ExceptionDtout> with the message of the exception
     */

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionDtout> handleMealAlreadyExistAdminException(MethodArgumentNotValidException e) {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ExceptionDtout("INVALID VALUE"));
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
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ExceptionDtout(e.getMessage()  ));
    }


}
