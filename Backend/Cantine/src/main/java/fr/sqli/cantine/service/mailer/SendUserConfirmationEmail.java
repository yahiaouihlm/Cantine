package fr.sqli.cantine.service.mailer;

import fr.sqli.cantine.entity.AdminEntity;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class SendUserConfirmationEmail {

    private EmailSenderService emailSenderService;

    private SpringTemplateEngine templateEngine;

    private Environment environment;
    @Autowired
    public  SendUserConfirmationEmail(EmailSenderService emailSenderService,  SpringTemplateEngine templateEngine , Environment environment) {
        this.emailSenderService = emailSenderService;
        this.templateEngine = templateEngine;
        this.environment = environment;
    }


    public  void   sendConfirmationLink(AdminEntity admin  , String  link ) throws MessagingException {

        Context context = new Context();
        context.setVariable("firstname", admin.getFirstname());
        context.setVariable("lastname", admin.getLastname());
        context.setVariable("confirmationLink", link);

        context.setVariable("sqliImage", this.environment.getProperty("sqli.cantine.confirmation.email.sqli.image.url"));
        context.setVariable("cantineLogo",  this.environment.getProperty("sqli.cantine.confirmation.email.cantiere.logo.url"));
        context.setVariable("astonLogo", this.environment.getProperty("sqli.cantine.confirmation.email.aston.logo.url"));


        String  body = templateEngine.process("confirmation-email-template", context);
        this.emailSenderService.send(admin.getEmail(), "Confirmation de votre compte", body);
    }


}
