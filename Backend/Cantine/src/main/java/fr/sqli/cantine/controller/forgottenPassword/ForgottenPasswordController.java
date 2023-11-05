package fr.sqli.cantine.controller.forgottenPassword;


import fr.sqli.cantine.dto.out.ResponseDtout;
import fr.sqli.cantine.service.users.exceptions.InvalidUserInformationException;
import fr.sqli.cantine.service.forgottenPassword.ForgottenPasswordService;
import fr.sqli.cantine.service.users.student.exceptions.StudentNotFoundException;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static fr.sqli.cantine.controller.forgottenPassword.IForgottenPasswordController.BASIC_FORGET_PASSWORD_URL;

@RestController
@RequestMapping(BASIC_FORGET_PASSWORD_URL)
@CrossOrigin(origins = "http://localhost:4200")
public class ForgottenPasswordController implements IForgottenPasswordController {

    private ForgottenPasswordService forgottenPassword;
    @Autowired
    public ForgottenPasswordController(ForgottenPasswordService forgottenPassword) {
        this.forgottenPassword = forgottenPassword;
    }


    @Override
    public ResponseEntity<ResponseDtout> sendConfirmationTokenForPasswordForgotten(String email) throws InvalidUserInformationException, MessagingException, StudentNotFoundException {
        this.forgottenPassword.sendConfirmationTokenForPasswordForgotten(email);
        return ResponseEntity.ok(new ResponseDtout(TOKEN_SENT_SUCCESSFULLY));
    }
}
