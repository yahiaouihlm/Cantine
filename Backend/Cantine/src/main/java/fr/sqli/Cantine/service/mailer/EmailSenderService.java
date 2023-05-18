package fr.sqli.Cantine.service.mailer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderService {


    private JavaMailSender javaMailSender ;

    @Autowired
    public  EmailSenderService ( JavaMailSender javaMailSender){
        this.javaMailSender =  javaMailSender;
    }


    @Async
    public void sendEmail (SimpleMailMessage message){
        javaMailSender.send(message);
    }


}
