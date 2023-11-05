package fr.sqli.cantine.controller.mailer;


import fr.sqli.cantine.dto.out.ResponseDtout;
import fr.sqli.cantine.service.users.admin.exceptions.AdminNotFound;
import fr.sqli.cantine.service.users.admin.exceptions.ExpiredToken;
import fr.sqli.cantine.service.users.exceptions.InvalidUserInformationException;
import fr.sqli.cantine.service.users.admin.exceptions.InvalidTokenException;
import fr.sqli.cantine.service.mailer.TokenSender;
import fr.sqli.cantine.service.users.student.exceptions.AccountAlreadyActivatedException;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(TokenSenderController.TOKEN_SENDER_BASIC_URL)
public class TokenSenderController implements ITokenSenderController {

    /// degas
    private TokenSender tokenSender;


    @Autowired
    public TokenSenderController(TokenSender tokenSender) {
        this.tokenSender = tokenSender;
    }


    @Override
    public ResponseEntity<ResponseDtout> checkTokenValidity(String token) throws InvalidTokenException, AccountAlreadyActivatedException, ExpiredToken, AdminNotFound {
        this.tokenSender.checkTokenValidity(token);
        return ResponseEntity.ok(new ResponseDtout(TOKEN_VALID));
    }

    @Override
    public ResponseEntity<ResponseDtout> sendTokenStudent(String email) throws InvalidUserInformationException, MessagingException, AccountAlreadyActivatedException, AdminNotFound {
        this.tokenSender.sendToken(email);
        return ResponseEntity.ok(new ResponseDtout(TOKEN_SENT_SUCCESSFULLY));
    }


}
