package fr.sqli.cantine.controller.users;

import fr.sqli.cantine.service.users.exceptions.AccountAlreadyActivatedException;
import fr.sqli.cantine.service.users.exceptions.RemovedAccountException;
import fr.sqli.cantine.service.users.exceptions.UserNotFoundException;
import jakarta.mail.MessagingException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

public interface IUserController {

    String USER_BASIC_URL = "/cantine/user";

    String USER_SEND_CONFIRMATION_LINK =  "/confirmation-link";


    @PostMapping(USER_SEND_CONFIRMATION_LINK)
    void  sendEmailConfirmationToken(@RequestParam("email") String email) throws UserNotFoundException, MessagingException, AccountAlreadyActivatedException, RemovedAccountException;

    }
