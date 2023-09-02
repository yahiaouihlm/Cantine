package fr.sqli.Cantine.controller.mailer;


import fr.sqli.Cantine.dto.out.ResponseDtout;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.AdminNotFound;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.InvalidPersonInformationException;
import fr.sqli.Cantine.service.mailer.TokenSender;
import fr.sqli.Cantine.service.student.exceptions.AccountAlreadyActivatedException;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(TokenSenderController.TOKEN_SENDER_BASIC_URL)
public class TokenSenderController  implements  ITokenSenderController{

 /// degas
    private TokenSender tokenSender;


    @Autowired
      public  TokenSenderController (TokenSender tokenSender){
          this.tokenSender = tokenSender;
      }


    @Override
    public ResponseEntity<ResponseDtout> sendTokenStudent( String email) throws InvalidPersonInformationException, MessagingException, AccountAlreadyActivatedException, AdminNotFound {
        this.tokenSender.sendToken(email);
        return ResponseEntity.ok(new ResponseDtout(TOKEN_SENT_SUCCESSFULLY));
    }
}
