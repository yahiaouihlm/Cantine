package fr.sqli.Cantine.controller.mailer;

import fr.sqli.Cantine.dto.out.ResponseDtout;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.AdminNotFound;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.ExpiredToken;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.InvalidPersonInformationException;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.InvalidTokenException;
import fr.sqli.Cantine.service.student.exceptions.AccountAlreadyActivatedException;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

public interface ITokenSenderController {

     String TOKEN_SENDER_BASIC_URL = "cantine/user/v1/token-sender";


     String SEND_TOKEN_URL = "/send-token";

     String  CONFIRM_TOKEN_URL = "/confirm-token";

     String  TOKEN_SENT_SUCCESSFULLY = "TOKEN SENT SUCCESSFULLY" ;


     String  TOKEN_VALID  =  "TOKEN VALID" ;

     @PostMapping(SEND_TOKEN_URL)
     ResponseEntity<ResponseDtout> sendTokenStudent(@RequestParam("email") String email) throws InvalidPersonInformationException, MessagingException, AccountAlreadyActivatedException, AdminNotFound;


     @GetMapping(CONFIRM_TOKEN_URL)
     ResponseEntity<ResponseDtout>checkTokenValidity(@RequestParam("token") String  token) throws InvalidTokenException, AccountAlreadyActivatedException, ExpiredToken, AdminNotFound;


}
