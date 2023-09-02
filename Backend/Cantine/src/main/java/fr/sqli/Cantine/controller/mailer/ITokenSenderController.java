package fr.sqli.Cantine.controller.mailer;

import fr.sqli.Cantine.dto.out.ResponseDtout;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.AdminNotFound;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.InvalidPersonInformationException;
import fr.sqli.Cantine.service.student.exceptions.AccountAlreadyActivatedException;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

public interface ITokenSenderController {

     String TOKEN_SENDER_BASIC_URL = "cantine/user/v1/token-sender";


     String SEND_TOKEN_URL = "/send-token";


     String  TOKEN_SENT_SUCCESSFULLY = "TOKEN SENT SUCCESSFULLY" ;


     @PostMapping(SEND_TOKEN_URL)
     public ResponseEntity<ResponseDtout> sendTokenStudent(@RequestParam("email") String email) throws InvalidPersonInformationException, MessagingException, AccountAlreadyActivatedException, AdminNotFound;
}
