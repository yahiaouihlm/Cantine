package fr.sqli.cantine.service.mailer;

import fr.sqli.cantine.entity.ConfirmationTokenEntity;
import fr.sqli.cantine.entity.StudentEntity;
import jakarta.mail.MessagingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class ForgotPasswordTokenSender  {
    private static final Logger LOG = LogManager.getLogger();
    private final String SERVER_ADDRESS;
    private  final String RESET_PASSWORD_TOKEN_URL;
    private EmailSenderService emailSenderService;

    private Environment environment;
    public ForgotPasswordTokenSender( EmailSenderService emailSenderService , Environment environment) {
        this.emailSenderService = emailSenderService;
        this.environment = environment;
        var protocol = environment.getProperty("sqli.cantine.server.protocol");
        var host = environment.getProperty("sqli.cantine.server.ip.address");
        var port = environment.getProperty("sali.cantine.server.port");
        this.SERVER_ADDRESS = protocol + host + ":" + port;

        this.RESET_PASSWORD_TOKEN_URL = this.SERVER_ADDRESS + this.environment.getProperty("sqli.canine.server.reset.password.url");
       this.emailSenderService = emailSenderService;

    }


    public void   sendStudentToken( ConfirmationTokenEntity confirmationTokenEntity) throws MessagingException {
        String  Url  =  this.RESET_PASSWORD_TOKEN_URL + confirmationTokenEntity.getToken();
        String confirmationOrderMessage =
                """
                   <!DOCTYPE html>
                   <html lang="en">
                   <head>
                       <meta charset="UTF-8">
                   </head>
                   <body>
                         """ +
                "<h2>Cher " + confirmationTokenEntity.getStudent().getFirstname() +"   "+ confirmationTokenEntity.getStudent().getLastname()  +",</h2>\n\n" +
                "Nous vous remercions d'avoir passé votre commande sur Cantière. Votre commande a été enregistrée avec succès et est maintenant en attente d'activation.\n\n" +

                        "Vous avez demandé à réinitialiser votre mot de passe sur Cantière. Veuillez cliquer sur le lien ci-dessous pour réinitialiser votre mot de passe.\n\n" +

                "Lien de réinitialisation du mot de passe : <a href= " + Url + ">\n\n" +
                """
                   </body>
                   </html>
                   """;
        this.emailSenderService.send(confirmationTokenEntity.getStudent().getEmail(), "Chabgement  de  mot  de  passe ", confirmationOrderMessage);

    }

    public void   sendAdminToken( ConfirmationTokenEntity confirmationTokenEntity) throws MessagingException {
        String  Url  =  this.RESET_PASSWORD_TOKEN_URL + confirmationTokenEntity.getToken();
        String confirmationOrderMessage =
                """
                   <!DOCTYPE html>
                   <html lang="en">
                   <head>
                       <meta charset="UTF-8">
                   </head>
                   <body>
                         """ +
                        "<h2>Cher " + confirmationTokenEntity.getAdmin().getFirstname() +"   "+ confirmationTokenEntity.getAdmin().getLastname()  +",</h2>\n\n" +
                        "Nous vous remercions d'avoir passé votre commande sur Cantière. Votre commande a été enregistrée avec succès et est maintenant en attente d'activation.\n\n" +

                        "Vous avez demandé à réinitialiser votre mot de passe sur Cantière. Veuillez cliquer sur le lien ci-dessous pour réinitialiser votre mot de passe.\n\n" +

                        "Lien de réinitialisation du mot de passe : <a href= " + Url + ">\n\n" +
                        """
                           </body>
                           </html>
                           """;
        this.emailSenderService.send(confirmationTokenEntity.getAdmin().getEmail(), "Chabgement  de  mot  de  passe ", confirmationOrderMessage);

    }
}
