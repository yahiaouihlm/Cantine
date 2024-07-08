package fr.sqli.cantine.service.mailer;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class EmailSenderService {


    private JavaMailSender javaMailSender;

    @Autowired
    public EmailSenderService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }


    @Async
    protected void sendEmail(MimeMessage message) {
        javaMailSender.send(message);
    }

    public void send(String to, String subject, String text) throws MessagingException {
        MimeMessage message = this.javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text, true);

        this.sendEmail(message);
    }

    public void sendWithAttachedImage(String to, String subject, String text, String imagePath) throws MessagingException {
        MimeMessage message = this.javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text, true);

        FileSystemResource file = new FileSystemResource(new File(imagePath));
        helper.addAttachment("orderQRcode.jpg", file);
        this.sendEmail(message);
    }


}
