package fr.sqli.cantine.service.forgottenPassword;

import fr.sqli.cantine.service.admin.adminDashboard.exceptions.InvalidPersonInformationException;
import fr.sqli.cantine.service.student.exceptions.StudentNotFoundException;
import jakarta.mail.MessagingException;

public interface IForgottenPasswordService {



    public  void  sendConfirmationTokenForPasswordForgotten(String email) throws InvalidPersonInformationException, StudentNotFoundException, MessagingException;
}
