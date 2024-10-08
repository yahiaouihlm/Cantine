package fr.sqli.cantine.service.users.user.impl;


import fr.sqli.cantine.dao.IConfirmationTokenDao;
import fr.sqli.cantine.dao.IUserDao;
import fr.sqli.cantine.dto.out.ResponseDtout;
import fr.sqli.cantine.entity.ConfirmationTokenEntity;
import fr.sqli.cantine.entity.UserEntity;
import fr.sqli.cantine.service.mailer.UserEmailSender;
import fr.sqli.cantine.service.users.exceptions.*;
import fr.sqli.cantine.service.users.user.IUserService;
import jakarta.mail.MessagingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements IUserService {

    private static final Logger LOG = LogManager.getLogger();
    final String SERVER_ADDRESS;
    final String RESET_PASSWORD_URL;
    final String CONFIRMATION_EMAIL_URL;
    final Environment environment;
    private final IConfirmationTokenDao iConfirmationTokenDao;
    private final IUserDao iUserDao;

    private final UserEmailSender sendUserConfirmationEmail;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    public UserService(Environment environment, IUserDao iUserDao, IConfirmationTokenDao iConfirmationTokenDao, UserEmailSender sendUserConfirmationEmail, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.iUserDao = iUserDao;
        this.environment = environment;
        this.iConfirmationTokenDao = iConfirmationTokenDao;
        this.sendUserConfirmationEmail = sendUserConfirmationEmail;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        var protocol = environment.getProperty("sqli.cantine.server.protocol");
        var host = environment.getProperty("sqli.cantine.server.ip.address");
        var port = environment.getProperty("sali.cantine.server.port");
        this.SERVER_ADDRESS = protocol + host + ":" + port;
        this.RESET_PASSWORD_URL = environment.getProperty("sqli.canine.server.reset.password.url");
        this.CONFIRMATION_EMAIL_URL = environment.getProperty("sqli.cantine.server.confirmation.token.url");

    }

    @Override
    public void resetPassword(String userToken, String newPassword) throws InvalidTokenException, InvalidUserInformationException, TokenNotFoundException, ExpiredToken, UserNotFoundException {
        if (userToken == null || userToken.trim().isEmpty()) {
            UserService.LOG.error("INVALID TOKEN  IN CHECK  LINK  VALIDITY");
            throw new InvalidTokenException("INVALID TOKEN");
        }
        if (newPassword == null || newPassword.trim().isEmpty() || newPassword.length() < 6 || newPassword.length() > 20) {
            UserService.LOG.error("INVALID PASSWORD  IN CHANGE  PASSWORD");
            throw new InvalidUserInformationException("INVALID PASSWORD");
        }

        var confirmationTokenEntity = this.iConfirmationTokenDao.findByToken(userToken).orElseThrow(() -> {
            UserService.LOG.error("TOKEN  NOT  FOUND  IN CHECK  LINK  VALIDITY : token = {}", userToken);
            return new TokenNotFoundException("INVALID TOKEN");
        });

        UserEntity user = (confirmationTokenEntity.getStudent() != null) ? confirmationTokenEntity.getStudent() : confirmationTokenEntity.getAdmin();

        if (user == null) {
            UserService.LOG.error("USER  NOT  FOUND  IN CHECK  LINK  VALIDITY WITH  token = {}", userToken);
            throw new InvalidTokenException("INVALID TOKEN"); //  token  not  found
        }

        var expiredTime = System.currentTimeMillis() - confirmationTokenEntity.getCreatedDate().getTime();

        long fiveMinutesInMillis = 50 * 60 * 1000; // 5 minutes en millisecondes
        //  expired  token  ///
        if (expiredTime > fiveMinutesInMillis) {
            this.iConfirmationTokenDao.delete(confirmationTokenEntity);
            UserService.LOG.error("EXPIRED TOKEN  IN CHECK  LINK  VALIDITY WITH  token = {}", userToken);
            throw new ExpiredToken("EXPIRED TOKEN");
        }

        var userEntity = this.iUserDao.findUserByEmail(user.getEmail()).orElseThrow(() -> {
            UserService.LOG.error("USER  NOT  FOUND  IN CHECK  LINK  VALIDITY WITH  token = {}", userToken);
            return new UserNotFoundException("USER NOT FOUND");
        });


        userEntity.setPassword(this.bCryptPasswordEncoder.encode(newPassword));

        this.iUserDao.save(userEntity);


    }

    @Override
    public void resetPasswordLink(String email) throws UserNotFoundException, MessagingException, AccountActivatedException, RemovedAccountException {

        if (email == null || email.isEmpty() || email.isBlank()) {
            UserService.LOG.error("INVALID EMAIL TO SEND  CONFIRMATION LINK");
            throw new UserNotFoundException("INVALID EMAIL");
        }
        var user = this.iUserDao.findUserByEmail(email).orElseThrow(() -> {
            UserService.LOG.error("user  WITH  EMAIL  {} IS  NOT  FOUND TO SEND  CONFIRMATION LINK", email);
            return new UserNotFoundException("USER NOT FOUND");
        });
        this.checkUserStatusAndSendTokenForResetPassword(user);
    }

    @Override
    public void existingEmail(String email) throws ExistingEmailException {

        if (email == null || email.isEmpty()) {
            UserService.LOG.error("INVALID EMAIL  IN EXISTING EMAIL");
            throw new ExistingEmailException("EMAIL ALREADY EXISTS");
        }

        if (this.iUserDao.findUserByEmail(email).isPresent()) {
            UserService.LOG.error("EMAIL  {} ALREADY  EXISTS ", email);
            throw new ExistingEmailException("EMAIL ALREADY EXISTS");
        }
        ;

    }

    @Override
    public void sendConfirmationLink(String email) throws UserNotFoundException, RemovedAccountException, AccountActivatedException, MessagingException {

        if (email == null || email.isEmpty() || email.isBlank()) {
            UserService.LOG.error("INVALID EMAIL TO SEND  CONFIRMATION LINK");
            throw new UserNotFoundException("INVALID EMAIL");
        }

        var user = this.iUserDao.findUserByEmail(email).orElseThrow(() -> {
            UserService.LOG.error("user  WITH  EMAIL  {} IS  NOT  FOUND TO SEND  CONFIRMATION LINK", email);
            return new UserNotFoundException("USER NOT FOUND");
        });
        this.checkUserStatusAndSendTokenForConfirmationEmail(user);
    }

    @Override
    public ResponseDtout checkLinkValidity(String token) throws InvalidTokenException, TokenNotFoundException, ExpiredToken, UserNotFoundException, AccountActivatedException {

        if (token == null || token.trim().isEmpty()) {
            UserService.LOG.error("INVALID TOKEN  IN CHECK  LINK  VALIDITY");
            throw new InvalidTokenException("INVALID TOKEN");
        }

        var confirmationTokenEntity = this.iConfirmationTokenDao.findByToken(token).orElseThrow(() -> {
            UserService.LOG.error("TOKEN  NOT  FOUND  IN CHECK  LINK  VALIDITY : token = {}", token);
            return new TokenNotFoundException("INVALID TOKEN");
        });

        UserEntity user = (confirmationTokenEntity.getStudent() != null) ? confirmationTokenEntity.getStudent() : confirmationTokenEntity.getAdmin();

        if (user == null) {
            UserService.LOG.error("USER  NOT  FOUND  IN CHECK  LINK  VALIDITY WITH  token = {}", token);
            throw new InvalidTokenException("INVALID TOKEN"); //  token  not  found
        }
        if (user.getStatus() == 1) {
            UserService.LOG.error("ACCOUNT  ALREADY  ACTIVATED WITH  EMAIL  {} ", user.getEmail());
            throw new AccountActivatedException("ACCOUNT  ALREADY  ACTIVATED");
        }

        var expiredTime = System.currentTimeMillis() - confirmationTokenEntity.getCreatedDate().getTime();

        long fiveMinutesInMillis = 50 * 60 * 1000; // 5 minutes en millisecondes
        //  expired  token  ///
        if (expiredTime > fiveMinutesInMillis) {
            this.iConfirmationTokenDao.delete(confirmationTokenEntity);
            UserService.LOG.error("EXPIRED TOKEN  IN CHECK  LINK  VALIDITY WITH  token = {}", token);
            throw new ExpiredToken("EXPIRED TOKEN");
        }

        var userEntity = this.iUserDao.findUserByEmail(user.getEmail()).orElseThrow(() -> {
            UserService.LOG.error("USER  NOT  FOUND  IN CHECK  LINK  VALIDITY WITH  token = {}", token);
            return new UserNotFoundException("USER NOT FOUND");
        });


        userEntity.setStatus(1);
        userEntity.setDisableDate(null);

        this.iUserDao.save(userEntity);

        return new ResponseDtout("TOKEN_CHECKED_SUCCESSFULLY");

    }

    @Override
    public UserEntity findUser(String email) throws UserNotFoundException {
        return this.iUserDao.findUserByEmail(email).orElseThrow(() -> {
            UserService.LOG.error("USER  NOT  FOUND  WITH  EMAIL  {} ", email);
            return new UserNotFoundException("USER NOT FOUND");
        });
    }

    private void checkUserStatusAndSendTokenForConfirmationEmail(UserEntity user) throws RemovedAccountException, AccountActivatedException, MessagingException {
        // account already  removed
        if (user.getDisableDate() != null) {
            UserService.LOG.error("ACCOUNT  ALREADY  REMOVED WITH  EMAIL  {} ", user.getEmail());
            throw new RemovedAccountException("ACCOUNT  ALREADY  REMOVED");
        }
        // account already  activated
        if (user.getStatus() == 1) {
            UserService.LOG.error("ACCOUNT  ALREADY  ACTIVATED WITH  EMAIL  {} ", user.getEmail());
            throw new AccountActivatedException("ACCOUNT  ALREADY  ACTIVATED");
        }
        ConfirmationTokenEntity confirmationToken = null;
        Optional<ConfirmationTokenEntity> confirmationTokenEntity;

        this.iConfirmationTokenDao.findByAdmin(user).ifPresent(this.iConfirmationTokenDao::delete);

        confirmationToken = new ConfirmationTokenEntity(user);

        this.iConfirmationTokenDao.save(confirmationToken);

        var url = this.SERVER_ADDRESS + this.CONFIRMATION_EMAIL_URL + confirmationToken.getToken();

        this.sendUserConfirmationEmail.sendConfirmationLink(user, url);
    }

    private void checkUserStatusAndSendTokenForResetPassword(UserEntity user) throws RemovedAccountException, AccountActivatedException, MessagingException {
        // account already  removed
        if (user.getDisableDate() != null) {
            UserService.LOG.error("ACCOUNT  ALREADY  REMOVED WITH  EMAIL  {} ", user.getEmail());
            throw new RemovedAccountException("ACCOUNT  ALREADY  REMOVED");
        }
        // account already  activated
        if (user.getStatus() == 0) {
            UserService.LOG.error("ACCOUNT  NOT  ACTIVATED WITH  EMAIL  {} ", user.getEmail());
            throw new AccountActivatedException("ACCOUNT NOT ACTIVATED");
        }
        ConfirmationTokenEntity confirmationToken = null;
        Optional<ConfirmationTokenEntity> confirmationTokenEntity;

        confirmationTokenEntity = this.iConfirmationTokenDao.findByAdmin(user);
        confirmationTokenEntity.ifPresent(this.iConfirmationTokenDao::delete);

        confirmationToken = new ConfirmationTokenEntity(user);


        this.iConfirmationTokenDao.save(confirmationToken);

        var url = this.SERVER_ADDRESS + this.RESET_PASSWORD_URL + confirmationToken.getToken();

        this.sendUserConfirmationEmail.sendLinkToResetPassword(user, url);
    }

}
