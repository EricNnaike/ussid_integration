package com.example.mfb_ussd_process_flow.service.emailService;

import com.example.mfb_ussd_process_flow.dto.request.UserRequest;
import com.example.mfb_ussd_process_flow.entityUser.Users;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.exceptions.TemplateEngineException;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService{

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;


    @Override
    public void sendRegistrationNotification(Users user) throws MessagingException {
        Context ctx = new Context(Locale.getDefault());
        ctx.setVariable("req", user.getUsername());

        // Use HTML and CSS for a well-organized email template
        String html = "<html>" +
                "<head>" +
                "   <style>" +
                "       body { font-family: Arial, sans-serif; }" +
                "       .container { max-width: 600px; margin: 0 auto; }" +
                "       .header { background-color: #f8f8f8; padding: 20px; text-align: center; }" +
                "       .content { padding: 20px; }" +
                "   </style>" +
                "</head>" +
                "<body>" +
                "   <div class='container'>" +
                "       <div class='header'>" +
                "           <h2>Thank you for choosing Trust Banc MFB. An account has been created for you.</h2>" +
                "       </div>" +
                "       <div class='content'>" +
                "           <p>Your credentials are:</p>" +
                "           <ul>" +
                "               <li>Username: " + user.getUsername() + "</li>" +
                "               <li>Secret Key: " + user.getSecretKey() + "</li>" +
                "           </ul>" +
                "           <p>Kindly activate your account with these credentials to enjoy our services.</p>" +
                "       </div>" +
                "   </div>" +
                "</body>" +
                "</html>";

        getEmailResponse(user, html);
    }
    private void getEmailResponse(Users users, String html) throws MessagingException {
        EmailResponse response = new EmailResponse();
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

            helper.setFrom("apployster@gmail.com");
            helper.setText(html, true);
            helper.setTo(users.getUsername());
            helper.setSubject("Notification From Trust Banc MFB");

            mailSender.send(message);
            response.setMessage("mail send to: " + users.getUsername());
            response.setStatus(Boolean.TRUE);

        }catch(MessagingException | TemplateEngineException e){
            response.setMessage("Mail sending failure : " + e.getMessage());
            response.setStatus(Boolean.FALSE);

        }
    }

    @Override
    public void sendActivationNotification(Users user, String password) throws MessagingException {
        Context ctx = new Context(Locale.getDefault());
        ctx.setVariable("req", user.getUsername());

        // Use HTML and CSS for a well-organized email template
        String html = "<html>" +
                "<head>" +
                "   <style>" +
                "       body { font-family: Arial, sans-serif; }" +
                "       .container { max-width: 600px; margin: 0 auto; }" +
                "       .header { background-color: #f8f8f8; padding: 20px; text-align: center; }" +
                "       .content { padding: 20px; }" +
                "   </style>" +
                "</head>" +
                "<body>" +
                "   <div class='container'>" +
                "       <div class='header'>" +
                "           <h2>You have successfully activated your account.</h2>" +
                "       </div>" +
                "       <div class='content'>" +
                "           <p>Your credentials are:</p>" +
                "           <ul>" +
                "               <li>Username: " + user.getUsername() + "</li>" +
                "               <li>Password: " + password + "</li>" +
                "           </ul>" +
                "           <p>Kindly sign in into your account with these credentials to enjoy our services.</p>" +
                "       </div>" +
                "   </div>" +
                "</body>" +
                "</html>";

        getEmailResponse(user, html);
    }

    @Override
    public void sendMailForResetPassword(String email, String firstName, String token) {

    }

    @Override
    public void sendMailToSetTransactionPinToken(String email, String newToken) {

    }


}
