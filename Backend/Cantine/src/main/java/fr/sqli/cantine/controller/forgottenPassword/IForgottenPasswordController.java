package fr.sqli.cantine.controller.forgottenPassword;

import fr.sqli.cantine.dto.out.ResponseDtout;
import fr.sqli.cantine.service.admin.adminDashboard.exceptions.InvalidPersonInformationException;
import fr.sqli.cantine.service.student.exceptions.StudentNotFoundException;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

public interface IForgottenPasswordController {

    String SEND_CONFIRMATION_TOKEN_FOR_PASSWORD_FORGOTTEN = "/sendConfirmationTokenForPasswordForgotten";

    String  TOKEN_SENT_SUCCESSFULLY = "TOKEN_SENT_SUCCESSFULLY";
    @PostMapping(SEND_CONFIRMATION_TOKEN_FOR_PASSWORD_FORGOTTEN)
    ResponseEntity<ResponseDtout> sendConfirmationTokenForPasswordForgotten(@RequestParam("email") String email) throws InvalidPersonInformationException, StudentNotFoundException, MessagingException;
}
