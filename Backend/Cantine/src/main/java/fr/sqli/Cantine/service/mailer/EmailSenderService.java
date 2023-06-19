package fr.sqli.Cantine.service.mailer;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;

@Service
public class EmailSenderService {


    private JavaMailSender javaMailSender ;

    @Autowired
    public  EmailSenderService ( JavaMailSender javaMailSender){
        this.javaMailSender =  javaMailSender;
    }


    @Async
    protected void sendEmail (MimeMessage message){
        javaMailSender.send(message);
    }


    /*public  void  send (String to , String subject , String text){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        this.sendEmail(message);
    }*/


      public  void  send (String to , String subject , String text) throws MessagingException {
       // SimpleMailMessage message = new SimpleMailMessage();
         MimeMessage message = this.javaMailSender.createMimeMessage();
          MimeMessageHelper helper = new MimeMessageHelper(message, true ,  "UTF-8");
      /*  message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setSubject(subject);
        message.setContent(text, "text/html" );*/

          helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);


          ClassPathResource classPathResource = new ClassPathResource("/static/logo-aston.png");
              helper.addInline("logo-aston", classPathResource);
        this.sendEmail(message);
    }


}
