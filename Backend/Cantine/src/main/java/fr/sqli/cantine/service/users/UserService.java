package fr.sqli.cantine.service.users;


import fr.sqli.cantine.dao.IAdminDao;
import fr.sqli.cantine.dao.IConfirmationTokenDao;
import fr.sqli.cantine.dao.IStudentDao;
import fr.sqli.cantine.entity.AdminEntity;
import fr.sqli.cantine.entity.ConfirmationTokenEntity;
import fr.sqli.cantine.entity.StudentEntity;
import fr.sqli.cantine.entity.UserEntity;
import fr.sqli.cantine.service.mailer.SendUserConfirmationEmail;
import fr.sqli.cantine.service.users.exceptions.AccountAlreadyActivatedException;
import fr.sqli.cantine.service.users.exceptions.RemovedAccountException;
import fr.sqli.cantine.service.users.exceptions.UserNotFoundException;
import jakarta.mail.MessagingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private static final Logger LOG = LogManager.getLogger();
    final String SERVER_ADDRESS;

    final String CONFIRMATION_TOKEN_URL;
    private final Environment environment ;
    private final IStudentDao iStudentDao ;
    private  final  IAdminDao iAdminDao ;
    private  final  IConfirmationTokenDao iConfirmationTokenDao;

    private final SendUserConfirmationEmail sendUserConfirmationEmail;


    public  UserService (Environment environment, IStudentDao iStudentDao , IAdminDao iAdminDao , IConfirmationTokenDao iConfirmationTokenDao, SendUserConfirmationEmail sendUserConfirmationEmail) {
        this.iAdminDao = iAdminDao;
        this.iStudentDao = iStudentDao;
        this.environment = environment;
        this.iConfirmationTokenDao = iConfirmationTokenDao;
        this.sendUserConfirmationEmail= sendUserConfirmationEmail;
        var protocol = environment.getProperty("sqli.cantine.server.protocol");
        var host = environment.getProperty("sqli.cantine.server.ip.address");
        var port = environment.getProperty("sali.cantine.server.port");
        this.SERVER_ADDRESS = protocol + host + ":" + port;
        this.CONFIRMATION_TOKEN_URL = environment.getProperty("sqli.cantine.server.confirmation.token.url");

    }


    public void sendConfirmationLink(String email) throws UserNotFoundException, RemovedAccountException, AccountAlreadyActivatedException, MessagingException {

        if (email == null || email.isEmpty() || email.isBlank()) {
            UserService.LOG.error("INVALID EMAIL TO SEND  CONFIRMATION LINK");
            throw new UserNotFoundException("INVALID EMAIL");
        }

       var student  = this.iStudentDao.findByEmail(email);
        if (student.isPresent()) {
            this.checkUserAccountAndSendEmail(student.get());
        }else {
            var admin = this.iAdminDao.findByEmail(email).orElseThrow(() -> {
                UserService.LOG.error("user  WITH  EMAIL  {} IS  NOT  FOUND TO SEND  CONFIRMATION LINK", email);
                return new UserNotFoundException("USER NOT FOUND");
            });
            this.checkUserAccountAndSendEmail(admin);
        }

    }

    private void checkUserAccountAndSendEmail(UserEntity user) throws RemovedAccountException, AccountAlreadyActivatedException, MessagingException {
        // account already  removed
        if (user.getDisableDate() != null) {
            UserService.LOG.error("ACCOUNT  ALREADY  REMOVED WITH  EMAIL  {} ", user.getEmail());
            throw new RemovedAccountException("ACCOUNT  ALREADY  REMOVED");
        }
        // account already  activated
        if (user.getStatus() == 1) {
            UserService.LOG.error("ACCOUNT  ALREADY  ACTIVATED WITH  EMAIL  {} ", user.getEmail());
            throw new AccountAlreadyActivatedException("ACCOUNT  ALREADY  ACTIVATED");
        }
        ConfirmationTokenEntity confirmationToken = null;
        Optional<ConfirmationTokenEntity> confirmationTokenEntity;

        if (user instanceof AdminEntity) {
            confirmationTokenEntity = this.iConfirmationTokenDao.findByAdmin((AdminEntity) user);
            confirmationTokenEntity.ifPresent(this.iConfirmationTokenDao::delete);

            confirmationToken = new ConfirmationTokenEntity((AdminEntity) user);

        } else {
            confirmationTokenEntity = this.iConfirmationTokenDao.findByStudent((StudentEntity) user);
            confirmationTokenEntity.ifPresent(this.iConfirmationTokenDao::delete);

            confirmationToken = new ConfirmationTokenEntity((StudentEntity) user);

        }
        this.iConfirmationTokenDao.save(confirmationToken);

        var url = this.SERVER_ADDRESS + this.CONFIRMATION_TOKEN_URL + confirmationToken.getToken();
        this.sendUserConfirmationEmail.sendConfirmationLink(user, url);


    }


}
