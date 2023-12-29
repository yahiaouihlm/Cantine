package fr.sqli.cantine.controller.users.impl;


import fr.sqli.cantine.controller.users.IUserController;
import fr.sqli.cantine.service.users.UserService;
import fr.sqli.cantine.service.users.exceptions.AccountAlreadyActivatedException;
import fr.sqli.cantine.service.users.exceptions.RemovedAccountException;
import fr.sqli.cantine.service.users.exceptions.UserNotFoundException;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;

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

    public void sendEmailConfirmationToken(@RequestParam("email") String email) throws UserNotFoundException, MessagingException, AccountAlreadyActivatedException, RemovedAccountException {
        this.userService.sendConfirmationLink(email);
    }

}
