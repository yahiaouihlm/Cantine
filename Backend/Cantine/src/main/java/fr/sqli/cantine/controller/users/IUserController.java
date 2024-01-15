package fr.sqli.cantine.controller.users;

import fr.sqli.cantine.dto.out.ResponseDtout;
import fr.sqli.cantine.service.users.exceptions.*;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

public interface IUserController {

    String USER_BASIC_URL = "/cantine/user";
    String  EXISTING_EMAIL_URL = "/existing-email";
    String USER_SEND_CONFIRMATION_LINK_URL =  "/send-confirmation-link";
    String CHECK_CONFIRMATION_TOKEN_URL = "/check-confirmation-token";

    String  SEND_RESET_PASSWORD_LINK_URL = "/send-reset-password-link";


    String TOKEN_SENT_SUCCESSFULLY= "TOKEN_SENT_SUCCESSFULLY" ;
    String TOKEN_CHECKED_SUCCESSFULLY = "TOKEN_CHECKED_SUCCESSFULLY";
    String EMAIL_DOES_NOT_EXISTS = "EMAIL_DOES_NOT_EXISTS";


    @PostMapping(SEND_RESET_PASSWORD_LINK_URL)
    ResponseEntity<ResponseDtout> resetPasswordLink(@RequestParam("email") String email) throws UserNotFoundException, MessagingException, AccountActivatedException, RemovedAccountException;

    @GetMapping(EXISTING_EMAIL_URL)
    ResponseEntity<ResponseDtout> existingEmail(@RequestParam("email") String email) throws ExistingEmailException;


    @PostMapping(USER_SEND_CONFIRMATION_LINK_URL)
    ResponseEntity<ResponseDtout> sendEmailConfirmationToken(@RequestParam("email") String email) throws UserNotFoundException, MessagingException, AccountActivatedException, RemovedAccountException;


    @PostMapping(CHECK_CONFIRMATION_TOKEN_URL)
    ResponseEntity<ResponseDtout> checkConfirmationToken(@RequestParam("token") String token) throws UserNotFoundException, RemovedAccountException, AccountActivatedException, InvalidTokenException, ExpiredToken, TokenNotFoundException;

    }
