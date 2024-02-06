package fr.sqli.cantine.service.mailer;

import fr.sqli.cantine.entity.UserEntity;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class UserEmailSender {

    private EmailSenderService emailSenderService;

    private SpringTemplateEngine templateEngine;

    private Environment environment;
    @Autowired
    public UserEmailSender(EmailSenderService emailSenderService, SpringTemplateEngine templateEngine , Environment environment) {
        this.emailSenderService = emailSenderService;
        this.templateEngine = templateEngine;
        this.environment = environment;
    }


    public  void sendConfirmationLink(UserEntity user  , String  link ) throws MessagingException {

        Context context = new Context();
        context.setVariable("firstname", user.getFirstname());
        context.setVariable("lastname", user.getLastname());
        context.setVariable("confirmationLink", link);

        context.setVariable("sqliImage", this.environment.getProperty("sqli.cantine.confirmation.email.sqli.image.url"));
        context.setVariable("cantineLogo",  this.environment.getProperty("sqli.cantine.confirmation.email.cantiere.logo.url"));
        context.setVariable("astonLogo", this.environment.getProperty("sqli.cantine.confirmation.email.aston.logo.url"));


        String  body = templateEngine.process("confirmation-email-template", context);
        this.emailSenderService.send(user.getEmail(), "Confirmation de votre compte", body);
    }


    public void  sendLinkToResetPassword(UserEntity user , String  link) throws MessagingException {
        Context context = new Context();
        context.setVariable("firstname", user.getFirstname());
        context.setVariable("lastname", user.getLastname());
        context.setVariable("resetPasswordLink", link);

        context.setVariable("sqliImage", this.environment.getProperty("sqli.cantine.confirmation.email.sqli.image.url"));
        context.setVariable("cantineLogo", this.environment.getProperty("sqli.cantine.confirmation.email.cantiere.logo.url"));
        context.setVariable("astonLogo", this.environment.getProperty("sqli.cantine.confirmation.email.aston.logo.url"));

        String  body = templateEngine.process("forgot-password-email-template", context);
        this.emailSenderService.send(user.getEmail(), "initialisation  de  mot de passe", body);
    }

}
