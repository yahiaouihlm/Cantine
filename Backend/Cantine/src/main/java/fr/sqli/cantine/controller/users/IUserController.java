package fr.sqli.cantine.controller.users;

import fr.sqli.cantine.dto.out.ResponseDtout;
import fr.sqli.cantine.service.users.exceptions.*;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

public interface IUserController {

    String USER_BASIC_URL = "/cantine/user";

    String USER_SEND_CONFIRMATION_LINK =  "/send-confirmation-link";

    String CHECK_CONFIRMATION_TOKEN = "/check-confirmation-token";

     String TOKEN_SENT_SUCCESSFULLY= "TOKEN_SENT_SUCCESSFULLY" ;
    String TOKEN_CHECKED_SUCCESSFULLY = "TOKEN_CHECKED_SUCCESSFULLY";

    @PostMapping(USER_SEND_CONFIRMATION_LINK)
    ResponseEntity<ResponseDtout> sendEmailConfirmationToken(@RequestParam("email") String email) throws UserNotFoundException, MessagingException, AccountAlreadyActivatedException, RemovedAccountException;


    @PostMapping(CHECK_CONFIRMATION_TOKEN)
    ResponseEntity<ResponseDtout> checkConfirmationToken(@RequestParam("token") String token) throws UserNotFoundException, RemovedAccountException, AccountAlreadyActivatedException, InvalidTokenException, ExpiredToken, TokenNotFoundException;

    }
