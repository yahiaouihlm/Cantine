package fr.sqli.cantine.service.mailer;

import fr.sqli.cantine.entity.MealEntity;
import fr.sqli.cantine.entity.MenuEntity;
import fr.sqli.cantine.entity.OrderEntity;
import fr.sqli.cantine.entity.StudentEntity;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ConfirmationOrderSender {

    private  final  String ORDER_QR_CODE_PATH;
    private EmailSenderService emailSenderService;
    private Environment environment;


    @Autowired
    public  ConfirmationOrderSender(EmailSenderService emailSenderService , Environment environment) {
            this.emailSenderService = emailSenderService;
            this.environment = environment;
            this.ORDER_QR_CODE_PATH = environment.getProperty("sqli.cantine.order.qrcode.url");
    }





      public  void  sendSubmittedOrder (OrderEntity order ) throws MessagingException {
          String qrCodeUrl = this.ORDER_QR_CODE_PATH + order.getQRCode() ;
          String confirmationOrderMessage =
                  """
                     <!DOCTYPE html>
                     <html lang="en">
                     <head>
                         <meta charset="UTF-8">
                     </head>
                     <body>
                           """ +

                          "<h2>Cher " + order.getStudent().getFirstname() +"  " + order.getStudent().getLastname()+",</h2>\n\n" +
                          "Nous vous remercions d'avoir passé votre commande sur Cantière. Votre commande a été Valider avec succès et  Vous  Pouvez La  récupérer " +
                          "de  puis  les  locaux  de  Ecole  de 11h  jusqu'au 15h" +
                          "- Numéro de commande : " + order.getId() + "<br>" +
                          "- Montant total : "+ order.getPrice() +"<br>" +
                          "-Date   : "  +  order.getCreationDate()  + ": "  + order.getCreationTime() +
                          "Merci de votre confiance et de votre soutien.<br>" +
                          "Cordialement,<br>" +
                          "Administrateurs  Aston<br>" +
                          "Aston By SQLI" + """
                           <img  src ="
                          """ + qrCodeUrl +  """
                                ">                          
                          """

                          +
                          """
                          </body>
                          </html>
                          """+qrCodeUrl;

          this.emailSenderService.send(order.getStudent().getEmail(), "  Validation    De  votre  commande ", confirmationOrderMessage);


      }
}

/*
+ student.getFirstName() + " " + student.getLastName() +
 */


/*


Nous vous remercions d'avoir passé votre commande sur [Nom de votre site]. Votre commande a été enregistrée avec succès et est maintenant en attente d'activation.

Détails de la commande :
- Numéro de commande : [Numéro de commande]
- Produit commandé : [Nom du produit]
- Montant total : [Montant total de la commande]

Nous travaillons activement à traiter votre commande dans les plus brefs délais. Vous recevrez une confirmation une fois que votre commande aura été activée et expédiée.

Merci de votre confiance et de votre soutien.

Cordialement,
[Votre nom ou nom de l'entreprise]
[Nom de votre site]
 */