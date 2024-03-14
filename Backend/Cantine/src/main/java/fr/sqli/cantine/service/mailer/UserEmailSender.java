package fr.sqli.cantine.service.mailer;

import fr.sqli.cantine.entity.UserEntity;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;

@Service
public class UserEmailSender {

    private final  EmailSenderService emailSenderService;

    private final SpringTemplateEngine templateEngine;

    private final Environment environment;

    @Autowired
    public UserEmailSender(EmailSenderService emailSenderService, SpringTemplateEngine templateEngine, Environment environment) {
        this.emailSenderService = emailSenderService;
        this.templateEngine = templateEngine;
        this.environment = environment;
    }


    public void sendNotificationAboutNewStudentAmount(UserEntity user, Double newSold , Double amount) throws MessagingException {
        Context context = new Context();
        context.setVariable("firstname", user.getFirstname());
        context.setVariable("lastname", user.getLastname());
        context.setVariable("amount", amount.toString());
        context.setVariable("sold", newSold.toString());
        context.setVariable("cantineLink", this.environment.getProperty("sqli.cantine.front.url"));

        context.setVariable("sqliImage", this.environment.getProperty("sqli.cantine.confirmation.email.sqli.image.url"));
        context.setVariable("cantineLogo", this.environment.getProperty("sqli.cantine.confirmation.email.cantiere.logo.url"));
        context.setVariable("astonLogo", this.environment.getProperty("sqli.cantine.confirmation.email.aston.logo.url"));
        context.setVariable("cantineContactNumber", this.environment.getProperty("sqli.cantine.administration.number.phone"));

        String body = templateEngine.process("student-AddAmount-Notification", context);
        this.emailSenderService.send(user.getEmail(), "Nouveau  Solde sur  votre  compte  cantière", body);
    }
    public void sendConfirmationCodeToCheckAddRemoveAmount (UserEntity user, Integer code , Double amount) throws MessagingException {
        Context context = new Context();
        context.setVariable("firstname", user.getFirstname());
        context.setVariable("lastname", user.getLastname());
        context.setVariable("amount", amount.toString());
        context.setVariable("confirmationCode", getCodeAndMakeSpaceBetweenDigits(code));

        context.setVariable("sqliImage", this.environment.getProperty("sqli.cantine.confirmation.email.sqli.image.url"));
        context.setVariable("cantineLogo", this.environment.getProperty("sqli.cantine.confirmation.email.cantiere.logo.url"));
        context.setVariable("astonLogo", this.environment.getProperty("sqli.cantine.confirmation.email.aston.logo.url"));
        context.setVariable("cantineContactNumber", this.environment.getProperty("sqli.cantine.administration.number.phone"));
        String body = templateEngine.process("confirmation-Adding-Amount-Student-wallet", context);
        this.emailSenderService.send(user.getEmail(), "Opération  sur votre wallet cantinière", body);
    }

    public void sendConfirmationLink(UserEntity user, String link) throws MessagingException {

        Context context = new Context();
        context.setVariable("firstname", user.getFirstname());
        context.setVariable("lastname", user.getLastname());
        context.setVariable("confirmationLink", link);

        context.setVariable("sqliImage", this.environment.getProperty("sqli.cantine.confirmation.email.sqli.image.url"));
        context.setVariable("cantineLogo", this.environment.getProperty("sqli.cantine.confirmation.email.cantiere.logo.url"));
        context.setVariable("astonLogo", this.environment.getProperty("sqli.cantine.confirmation.email.aston.logo.url"));


        String body = templateEngine.process("confirmation-email-template", context);
        this.emailSenderService.send(user.getEmail(), "Confirmation de votre compte", body);
    }


    public void sendLinkToResetPassword(UserEntity user, String link) throws MessagingException {
        Context context = new Context();
        context.setVariable("firstname", user.getFirstname());
        context.setVariable("lastname", user.getLastname());
        context.setVariable("resetPasswordLink", link);

        context.setVariable("sqliImage", this.environment.getProperty("sqli.cantine.confirmation.email.sqli.image.url"));
        context.setVariable("cantineLogo", this.environment.getProperty("sqli.cantine.confirmation.email.cantiere.logo.url"));
        context.setVariable("astonLogo", this.environment.getProperty("sqli.cantine.confirmation.email.aston.logo.url"));

        String body = templateEngine.process("forgot-password-email-template", context);
        this.emailSenderService.send(user.getEmail(), "initialisation  de  mot de passe", body);
    }


    public void sendNotificationToSuperAdminAboutAdminRegistration(UserEntity admin, String url) throws MessagingException {
        Context context = new Context();

        context.setVariable("superAdminName", this.environment.getProperty("sqli.cantine.superAdmin.firstName"));
        context.setVariable("adminFirstName", admin.getFirstname());
        context.setVariable("adminLastName", admin.getLastname());
        context.setVariable("NewAdminRegistrationURL", url);

        context.setVariable("sqliImage", this.environment.getProperty("sqli.cantine.confirmation.email.sqli.image.url"));
        context.setVariable("cantineLogo", this.environment.getProperty("sqli.cantine.confirmation.email.cantiere.logo.url"));
        context.setVariable("astonLogo", this.environment.getProperty("sqli.cantine.confirmation.email.aston.logo.url"));

        String body = templateEngine.process("confirmation-admin-registration", context);
        this.emailSenderService.send(this.environment.getProperty("sqli.cantine.superAdmin.email"), "A Nouveau  Admin s'enregistré ", body);

    }

    public  void sendNotificationTOStudentWhenEmailHasBeenChanged(UserEntity student) throws MessagingException {
        Context context = new Context();
        context.setVariable("firstname", student.getFirstname());
        context.setVariable("lastname", student.getLastname());
        context.setVariable("cantineLink", this.environment.getProperty("sqli.cantine.front.url"));

        context.setVariable("sqliImage", this.environment.getProperty("sqli.cantine.confirmation.email.sqli.image.url"));
        context.setVariable("cantineLogo", this.environment.getProperty("sqli.cantine.confirmation.email.cantiere.logo.url"));
        context.setVariable("astonLogo", this.environment.getProperty("sqli.cantine.confirmation.email.aston.logo.url"));

        String body = templateEngine.process("student-email-updated", context);
        this.emailSenderService.send(student.getEmail(), "Votre Email à été changer", body);
    }

    private  String getCodeAndMakeSpaceBetweenDigits(Integer code){
        String codeAsString = code.toString();
        StringBuilder codeWithSpace = new StringBuilder();
        for (int i = 0; i < codeAsString.length(); i++) {
            codeWithSpace.append(codeAsString.charAt(i)).append(" ");
        }
        return codeWithSpace.toString();

    }

}
