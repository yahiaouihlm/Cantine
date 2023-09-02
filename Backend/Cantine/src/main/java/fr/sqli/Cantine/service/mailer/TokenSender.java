package fr.sqli.Cantine.service.mailer;


import fr.sqli.Cantine.dao.IAdminDao;
import fr.sqli.Cantine.dao.IConfirmationTokenDao;
import fr.sqli.Cantine.dao.IStudentDao;
import fr.sqli.Cantine.entity.ConfirmationTokenEntity;
import fr.sqli.Cantine.service.admin.adminDashboard.account.AdminService;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.AdminNotFound;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.InvalidPersonInformationException;
import fr.sqli.Cantine.service.student.StudentService;
import fr.sqli.Cantine.service.student.exceptions.AccountAlreadyActivatedException;
import jakarta.mail.MessagingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class TokenSender  {

    private static final Logger LOG = LogManager.getLogger();
    private final String SERVER_ADDRESS;
    private  final String CONFIRMATION_TOKEN_URL;
    private IAdminDao adminDao;
    private IStudentDao studentDao;
    private Environment   environment;

    private IConfirmationTokenDao confirmationTokenDao;

    private EmailSenderService emailSenderService;


    @Autowired
    public TokenSender(IAdminDao adminDao, Environment environment , IConfirmationTokenDao confirmationTokenDao
            , IStudentDao studentDao, EmailSenderService emailSenderService) {
        this.adminDao = adminDao;
        this.studentDao = studentDao;
        this.environment = environment;
        this.confirmationTokenDao = confirmationTokenDao;
        this.emailSenderService = emailSenderService;
        var protocol = environment.getProperty("sqli.cantine.server.protocol");
        var host = environment.getProperty("sqli.cantine.server.ip.address");
        var port = environment.getProperty("sali.cantine.server.port");
        this.SERVER_ADDRESS = protocol + host + ":" + port;
        this.CONFIRMATION_TOKEN_URL = environment.getProperty("sqli.cantine.server.confirmation.token.url");
    }

    public void sendToken(String email) throws AdminNotFound, InvalidPersonInformationException, MessagingException, AccountAlreadyActivatedException {

        if (email == null || email.trim().isEmpty()) {
            TokenSender.LOG.error("email  is  not  valid");
            throw new InvalidPersonInformationException("YOUR EMAIL IS NOT VALID");
        }

        // check  if there is  admin  with  this  email    or  just student

        var student  =  this.studentDao.findByEmail(email.trim());
        if  (student.isPresent()) {
            if (student.get().getStatus() == 1) {
                throw new AccountAlreadyActivatedException("YOUR ACCOUNT IS ALREADY ENABLED");
            }
            if (student.get().getStatus() == 1) {
                TokenSender.LOG.error("student  is  already  confirmed");
                throw new AccountAlreadyActivatedException("STUDENT IS ALREADY CONFIRMED");
            }

            var  confirmationTokenEntity =  this.confirmationTokenDao.findByStudent(student.get());
            confirmationTokenEntity.ifPresent(tokenEntity -> this.confirmationTokenDao.delete(tokenEntity));


            var  confirmationToken =  new ConfirmationTokenEntity(student.get());
            this.confirmationTokenDao.save(confirmationToken);

            var url = this.SERVER_ADDRESS+this.CONFIRMATION_TOKEN_URL + confirmationToken.getToken();

            String text = """
                     <!DOCTYPE html>
                     <html lang="en">
                     <head>
                         <meta charset="UTF-8">
                     </head>
                     <body>
                         <h1>Confirmation d'inscription</h1>
                           <p> Bonjour 
                           """
                    + student.get().getFirstname() +"  " + student.get().getLastname() +
                    """ 
                    </p>
                    <P>
                        Merci de cliquer sur le lien ci-dessous pour confirmer votre adresse Email et activer votre compte.
                       
                    </P>
                      <p> Nous  vous  Remercions  Votre  Compréhention </p>
                        <p> Cordialement </p>
                              
                </body>
                </html>
           """ + "<a href="+ url + '>' + "Confirmer votre compte" + "</a>";


            this.emailSenderService.send(student.get().getEmail(), "Confirmation de votre compte", text);
             return;
        }

        var admin = this.adminDao.findByEmail(email.trim());
        if (admin.isPresent()){
            if (admin.get().getStatus() == 1) {
                throw new AccountAlreadyActivatedException("YOUR ACCOUNT IS ALREADY ENABLED");
            }

            var confirmationTokenEntity = this.confirmationTokenDao.findByAdmin(admin.get());
            confirmationTokenEntity.ifPresent(tokenEntity -> this.confirmationTokenDao.delete(tokenEntity));

            ConfirmationTokenEntity confirmationToken = new ConfirmationTokenEntity(admin.get());
            this.confirmationTokenDao.save(confirmationToken);

            var url = this.SERVER_ADDRESS + this.CONFIRMATION_TOKEN_URL + confirmationToken.getToken();
            String text = """
                     <!DOCTYPE html>
                     <html lang="en">
                     <head>
                         <meta charset="UTF-8">
                     </head>
                     <body>
                         <h1>Confirmation d'inscription</h1>
                           <p> Bonjour 
                           """
                    + admin.get().getFirstname() +"  " + admin.get().getLastname() +
                    """ 
                    </p>
                    <P>
                        Merci de cliquer sur le lien ci-dessous pour confirmer votre adresse Email et activer votre compte.
                       
                    </P>
                      <p> Nous  vous  Remercions  Votre  Compréhention </p>
                        <p> Cordialement </p>
                              
                </body>
                </html>
           """ + "<a href="+ url + '>' + "Confirmer votre compte" + "</a>";



            this.emailSenderService.send(email, "Complete Registration!", text);
            return;
        }


        TokenSender.LOG.error("email  is  not  valid");
        throw new AdminNotFound("EMAIl : "+ email + " is not  Found ");

    }




}
