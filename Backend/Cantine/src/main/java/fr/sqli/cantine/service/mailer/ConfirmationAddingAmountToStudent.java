package fr.sqli.cantine.service.mailer;


import fr.sqli.cantine.entity.ConfirmationTokenEntity;
import fr.sqli.cantine.entity.StudentEntity;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class ConfirmationAddingAmountToStudent {

/*    private  final  String ORDER_QR_CODE_PATH;*/
    private EmailSenderService emailSenderService;
    private Environment environment;

    @Autowired
    public ConfirmationAddingAmountToStudent(EmailSenderService emailSenderService , Environment environment) {
        this.emailSenderService = emailSenderService;
        this.environment = environment;
    }

    public void sendConfirmationAmount(StudentEntity student , Double amount , ConfirmationTokenEntity confirmationToken) throws MessagingException {
        String confirmationAmountMessage =
                """
                    <!DOCTYPE html>
                    <html lang="en">
                    <head>
                        <meta charset="UTF-8">
                    </head>
                    <body>
                        <h2>Cher """ + student.getFirstname() +"  " + student.getLastname()+",</h2>\n\n" +
                        "Une Opération  d'ajout d'un  montant de " + amount + "  €  En  cours  sur  votre compte Cantière.\n\n" +
                        "Veuillez  S'il  vous plait  confirmer cette  opération  en  Communiquant  Le  Code  ci-dessous Au  personnel chargé  de  cette  Opération  :\n\n" +
                        "Code  :  " + confirmationToken.getUuid() + "<br>" +
                        "Merci de votre confiance et de votre soutien.<br>" +
                        "L'équipe Cantière" +
                        "En Cas De  problème  veuillez  contacter  le  service  client  au  06  00  00  00  00  .<br>" +
                        "</body>\n" +
                        "</html>";


        this.emailSenderService.send(student.getEmail(), "Confirmation d'ajout de montant", confirmationAmountMessage);
    } 
}
