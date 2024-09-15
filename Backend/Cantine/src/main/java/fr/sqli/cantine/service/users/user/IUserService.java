package fr.sqli.cantine.service.users.user;


import fr.sqli.cantine.dto.out.ResponseDtout;
import fr.sqli.cantine.entity.UserEntity;
import fr.sqli.cantine.service.users.exceptions.*;
import jakarta.mail.MessagingException;


public interface IUserService {

    public void resetPassword(String userToken, String newPassword) throws InvalidTokenException, InvalidUserInformationException, TokenNotFoundException, ExpiredToken, UserNotFoundException;

    public void resetPasswordLink(String email) throws UserNotFoundException, MessagingException, AccountActivatedException, RemovedAccountException;

    public void existingEmail(String email) throws ExistingEmailException;

    public void sendConfirmationLink(String email) throws UserNotFoundException, RemovedAccountException, AccountActivatedException, MessagingException;

    public ResponseDtout checkLinkValidity(String token) throws InvalidTokenException, TokenNotFoundException, ExpiredToken, UserNotFoundException, AccountActivatedException;

    public UserEntity findUser(String email) throws UserNotFoundException;

}
