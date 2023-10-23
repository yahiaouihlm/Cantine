package fr.sqli.cantine.service.forgottenPassword;


import fr.sqli.cantine.dao.IAdminDao;
import fr.sqli.cantine.dao.IConfirmationTokenDao;
import fr.sqli.cantine.dao.IStudentDao;
import fr.sqli.cantine.entity.ConfirmationTokenEntity;
import fr.sqli.cantine.service.admin.exceptions.InvalidPersonInformationException;
import fr.sqli.cantine.service.mailer.ForgotPasswordTokenSender;
import fr.sqli.cantine.service.student.exceptions.StudentNotFoundException;
import jakarta.mail.MessagingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class ForgottenPasswordService implements IForgottenPasswordService {
    private static final Logger LOG = LogManager.getLogger();
    private  String EMAIL8_REGEX;
    private IConfirmationTokenDao confirmationTokenDao;
    private IAdminDao adminDao;

    private IStudentDao studentDao;

    private Environment environment;

    private ForgotPasswordTokenSender forgotPasswordTokenSender;
    @Autowired
    public ForgottenPasswordService(IConfirmationTokenDao confirmationTokenDao, IAdminDao adminDao, IStudentDao studentDao , Environment environment, ForgotPasswordTokenSender forgotPasswordTokenSender) {
        this.confirmationTokenDao = confirmationTokenDao;
        this.adminDao = adminDao;
        this.studentDao = studentDao;
        this.environment = environment;
        this.EMAIL8_REGEX = this.environment.getProperty("sqli.cantine.email.regex");
        this.forgotPasswordTokenSender = forgotPasswordTokenSender;

    }


    @Override
    public void sendConfirmationTokenForPasswordForgotten(String email) throws InvalidPersonInformationException, StudentNotFoundException, MessagingException {

        if (email == null || email.isEmpty() || email.trim().isEmpty() || email.isBlank()) {
            ForgottenPasswordService.LOG.error("Email is required");
            throw new InvalidPersonInformationException("Email is required");
        }
        if (!email.matches(this.EMAIL8_REGEX)) {
            ForgottenPasswordService.LOG.error("Email is not valid");
            throw new InvalidPersonInformationException("Email is not valid");
        }
        var student = this.studentDao.findByEmail(email);

         if (student.isPresent()) {
             this.confirmationTokenDao.findByStudent(student.get()).ifPresent(confirmationToken -> this.confirmationTokenDao.delete(confirmationToken));
             ConfirmationTokenEntity confirmationTokenEntity = new ConfirmationTokenEntity(student.get());
             this.confirmationTokenDao.save(confirmationTokenEntity);
             this.forgotPasswordTokenSender.sendStudentToken(confirmationTokenEntity);
             return; //  end the  code  verry  important  to  the  end  of  the  code
         }


         var  admin = this.adminDao.findByEmail(email);
            if (admin.isPresent()) {
                this.confirmationTokenDao.findByAdmin(admin.get()).ifPresent(confirmationToken -> this.confirmationTokenDao.delete(confirmationToken));
                ConfirmationTokenEntity confirmationTokenEntity = new ConfirmationTokenEntity(admin.get());
                this.confirmationTokenDao.save(confirmationTokenEntity);
                this.forgotPasswordTokenSender.sendAdminToken(confirmationTokenEntity);
                return; //  end the  code  verry  important  to  the  end  of  the  code
            }

         throw  new StudentNotFoundException(" user not found"); //  if  the  user  not  found  throw  exception
    }



























}
