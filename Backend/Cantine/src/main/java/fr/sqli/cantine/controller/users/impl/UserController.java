package fr.sqli.cantine.controller.users.impl;


import fr.sqli.cantine.controller.users.IUserController;
import fr.sqli.cantine.dto.out.ResponseDtout;
import fr.sqli.cantine.service.users.UserService;
import fr.sqli.cantine.service.users.exceptions.*;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static fr.sqli.cantine.controller.users.IUserController.USER_BASIC_URL;


@RestController
@RequestMapping(USER_BASIC_URL)
public class UserController implements IUserController {


    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Override
    public ResponseEntity<ResponseDtout> resetPasswordLink(String email) throws UserNotFoundException, MessagingException, AccountActivatedException, RemovedAccountException {
        this.userService.resetPasswordLink(email);
        return  ResponseEntity.ok(new ResponseDtout(TOKEN_SENT_SUCCESSFULLY));
    }

    @Override
    public ResponseEntity<ResponseDtout> existingEmail(String email) throws ExistingEmailException {
        this.userService.existingEmail(email);
        return ResponseEntity.ok(new ResponseDtout(EMAIL_DOES_NOT_EXISTS));
    }

    public ResponseEntity<ResponseDtout> checkConfirmationToken(@RequestParam("token") String token) throws UserNotFoundException, InvalidTokenException, ExpiredToken, TokenNotFoundException {
        this.userService.checkLinkValidity(token);
        return ResponseEntity.ok(new ResponseDtout(TOKEN_CHECKED_SUCCESSFULLY));
    }



    public ResponseEntity<ResponseDtout> sendEmailConfirmationToken(@RequestParam("email") String email) throws UserNotFoundException, MessagingException, AccountActivatedException, RemovedAccountException {
        this.userService.sendConfirmationLink(email);
        return ResponseEntity.ok(new ResponseDtout(TOKEN_SENT_SUCCESSFULLY));
    }
}
