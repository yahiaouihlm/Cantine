package fr.sqli.cantine.service.mailer;


import fr.sqli.cantine.entity.StudentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class WalletNotificationSender {


    private EmailSenderService emailSenderService;
    private Environment environment;

    @Autowired
    public  WalletNotificationSender( Environment environment ,  EmailSenderService  emailSenderService) {
        this.emailSenderService   =  emailSenderService;
        this.environment=environment;
    }


    public void  sendNotEnoughWallter (StudentEntity student) {
    String  notificationmessage = """
             hello wrold  """;



    }

}
