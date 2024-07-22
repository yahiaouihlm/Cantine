package fr.sqli.cantine.controller.users;


import fr.sqli.cantine.dto.out.ResponseDtout;
import fr.sqli.cantine.service.users.user.impl.UserService;
import fr.sqli.cantine.service.users.exceptions.*;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(UserController.USER_BASIC_URL)
public class UserController{

    public static final String USER_BASIC_URL = "/cantine/user";
    final String EXISTING_EMAIL_URL = "/existing-email";
    final String USER_SEND_CONFIRMATION_LINK_URL = "/send-confirmation-link";
    final String CHECK_CONFIRMATION_TOKEN_URL = "/check-confirmation-token";
    final String SEND_RESET_PASSWORD_LINK_URL = "/send-reset-password-link";
    final String RESET_PASSWORD_URL = "/reset-password";
    final String TOKEN_SENT_SUCCESSFULLY = "TOKEN_SENT_SUCCESSFULLY";
    final String EMAIL_DOES_NOT_EXISTS = "EMAIL_DOES_NOT_EXISTS";
    final String PASSWORD_RESET_SUCCESSFULLY = "PASSWORD_RESET_SUCCESSFULLY";

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping(RESET_PASSWORD_URL)
    public ResponseEntity<ResponseDtout> resetPassword(@RequestParam("token") String token, @RequestParam("newPassword") String password) throws UserNotFoundException, InvalidTokenException, ExpiredToken, InvalidUserInformationException, TokenNotFoundException {
        this.userService.resetPassword(token, password);
        return ResponseEntity.ok(new ResponseDtout(PASSWORD_RESET_SUCCESSFULLY));
    }


    @PostMapping(SEND_RESET_PASSWORD_LINK_URL)
    public ResponseEntity<ResponseDtout> resetPasswordLink(@RequestParam("email") String email) throws UserNotFoundException, MessagingException, AccountActivatedException, RemovedAccountException {
        this.userService.resetPasswordLink(email);
        return ResponseEntity.ok(new ResponseDtout(TOKEN_SENT_SUCCESSFULLY));
    }

    @GetMapping(EXISTING_EMAIL_URL)
    public ResponseEntity<ResponseDtout> existingEmail(@RequestParam("email") String email) throws ExistingEmailException {
        this.userService.existingEmail(email);
        return ResponseEntity.ok(new ResponseDtout(EMAIL_DOES_NOT_EXISTS));
    }

    @PostMapping(CHECK_CONFIRMATION_TOKEN_URL)
    public ResponseEntity<ResponseDtout> checkConfirmationToken(@RequestParam("token") String token) throws UserNotFoundException, InvalidTokenException, ExpiredToken, TokenNotFoundException, AccountActivatedException {
        return ResponseEntity.ok(this.userService.checkLinkValidity(token));
    }


    @PostMapping(USER_SEND_CONFIRMATION_LINK_URL)
    public ResponseEntity<ResponseDtout> sendEmailConfirmationToken(@RequestParam("email") String email) throws UserNotFoundException, MessagingException, AccountActivatedException, RemovedAccountException {
        this.userService.sendConfirmationLink(email);
        return ResponseEntity.ok(new ResponseDtout(TOKEN_SENT_SUCCESSFULLY));
    }
}
