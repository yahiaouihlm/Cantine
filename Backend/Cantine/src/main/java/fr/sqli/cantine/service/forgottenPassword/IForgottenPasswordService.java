package fr.sqli.cantine.service.forgottenPassword;

import fr.sqli.cantine.service.users.exceptions.InvalidUserInformationException;
import fr.sqli.cantine.service.users.exceptions.UserNotFoundException;

import jakarta.mail.MessagingException;

public interface IForgottenPasswordService {



    public  void  sendConfirmationTokenForPasswordForgotten(String email) throws InvalidUserInformationException, MessagingException, UserNotFoundException;
}
