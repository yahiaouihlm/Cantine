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



      public  void  sendConfirmationOrder (StudentEntity student , OrderEntity order) throws MessagingException {


           String food =  "" ;
          for (MealEntity meal : order.getMeals()) {
              food = food + meal.getLabel() + " , " + meal.getPrice() + "<br>" ;
          }
          for ( MenuEntity menu : order.getMenus() ) {
              food = food + menu.getLabel() + " , " + menu.getPrice() + "<br>      " ;
          }
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

                  "<h2>Cher " + student.getFirstname() +"  " + student.getLastname()+",</h2>\n\n" +
                  "Nous vous remercions d'avoir passé votre commande sur Cantière. Votre commande a été enregistrée avec succès et est maintenant en attente d'activation.\n\n" +
                  "Détails de la commande :<br>" +
                  "- Numéro de commande : " + order.getId() + "<br>" +
                  "- Détails : " + food+ "<br>" +
                  "- Montant total : "+ order.getPrice() +"<br>" +
                          "-Date   : "  +  order.getCreationDate()  + ": "  + order.getCreationTime() +
                  "Nous travaillons activement à traiter votre commande dans les plus brefs délais. Vous recevrez une confirmation une fois que votre commande aura été activée et expédiée.\n\n" +
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

          this.emailSenderService.send(student.getEmail(), "Confirmation  de  Reception  De  votre  commande ", confirmationOrderMessage);

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