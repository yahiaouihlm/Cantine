package fr.sqli.cantine.controller.forgottenPassword;


import fr.sqli.cantine.dto.out.ResponseDtout;
import fr.sqli.cantine.service.admin.adminDashboard.exceptions.InvalidPersonInformationException;
import fr.sqli.cantine.service.forgottenPassword.ForgottenPasswordService;
import fr.sqli.cantine.service.student.exceptions.StudentNotFoundException;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("canitine/user")
@CrossOrigin(origins = "http://localhost:4200")
public class ForgottenPasswordController implements IForgottenPasswordController {

    private ForgottenPasswordService forgottenPassword;
    @Autowired
    public ForgottenPasswordController(ForgottenPasswordService forgottenPassword) {
        this.forgottenPassword = forgottenPassword;
    }


    @Override
    public ResponseEntity<ResponseDtout> sendConfirmationTokenForPasswordForgotten(String email) throws InvalidPersonInformationException, MessagingException, StudentNotFoundException {
        this.forgottenPassword.sendConfirmationTokenForPasswordForgotten(email);
        return ResponseEntity.ok(new ResponseDtout(TOKEN_SENT_SUCCESSFULLY));
    }
}
