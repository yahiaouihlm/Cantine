package fr.sqli.cantine.service.mailer;


import fr.sqli.cantine.entity.OrderEntity;
import fr.sqli.cantine.entity.StudentEntity;
import fr.sqli.cantine.entity.UserEntity;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.math.BigDecimal;

@Service
public class OrderEmailSender {


    private final EmailSenderService emailSenderService;

    private final SpringTemplateEngine templateEngine;

    private final Environment environment;

    @Autowired
    public OrderEmailSender(EmailSenderService emailSenderService, SpringTemplateEngine templateEngine, Environment environment) {
        this.emailSenderService = emailSenderService;
        this.templateEngine = templateEngine;
        this.environment = environment;
    }

    public void  cancelledOrder (UserEntity student, OrderEntity order) throws MessagingException {
        Context context = new Context();

        context.setVariable("firstname", student.getFirstname());
        context.setVariable("lastname", student.getLastname());
        context.setVariable("orderUuid", order.getUuid());
        context.setVariable("orderDate", order.getCreationDate());
        context.setVariable("orderTotalPrice", order.getPrice());

        context.setVariable("sqliImage", this.environment.getProperty("sqli.cantine.confirmation.email.sqli.image.url"));
        context.setVariable("cantineLogo", this.environment.getProperty("sqli.cantine.confirmation.email.cantiere.logo.url"));
        context.setVariable("astonLogo", this.environment.getProperty("sqli.cantine.confirmation.email.aston.logo.url"));


        String body = templateEngine.process("cancelled-order-notification", context);
        this.emailSenderService.send(student.getEmail(), "Votre  Commande été Annuler  avec succes", body);
    }

    public void  confirmOrder(UserEntity student, OrderEntity order ,  BigDecimal tax) throws MessagingException {
        Context context = new Context();
        context.setVariable("firstname", student.getFirstname());
        context.setVariable("lastname", student.getLastname());

        context.setVariable("orderUuid", order.getUuid());
        context.setVariable("orderDate", order.getCreationDate());
        context.setVariable("orderTotalPrice", order.getPrice());
        context.setVariable("orderItems", order);
        context.setVariable("totalItems", order.getMeals().size() + order.getMenus().size());
        context.setVariable("totalHT", (order.getPrice().subtract(tax)));
        context.setVariable("tva", tax);
        context.setVariable("cantineOrdersUrl", this.environment.getProperty("sqli.cantine.front.students.orders.url"));


        context.setVariable("sqliImage", this.environment.getProperty("sqli.cantine.confirmation.email.sqli.image.url"));
        context.setVariable("cantineLogo", this.environment.getProperty("sqli.cantine.confirmation.email.cantiere.logo.url"));
        context.setVariable("astonLogo", this.environment.getProperty("sqli.cantine.confirmation.email.aston.logo.url"));


        String body = templateEngine.process("confirmation-order-template", context);
        this.emailSenderService.send(student.getEmail(), "Confiramtion de  votre  commande", body);
    }

    public void sendNotificationForSmallStudentWallet(UserEntity user) throws MessagingException {
        Context context = new Context();
        context.setVariable("firstname", user.getFirstname());
        context.setVariable("lastname", user.getLastname());


        context.setVariable("sqliImage", this.environment.getProperty("sqli.cantine.confirmation.email.sqli.image.url"));
        context.setVariable("cantineLogo", this.environment.getProperty("sqli.cantine.confirmation.email.cantiere.logo.url"));
        context.setVariable("astonLogo", this.environment.getProperty("sqli.cantine.confirmation.email.aston.logo.url"));


        String body = templateEngine.process("small-student-wallet", context);
        this.emailSenderService.send(user.getEmail(), "Solde de votre portefeuille insuffisant", body);
    }

}
