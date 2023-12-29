package com.example.mfb_ussd_process_flow.service.emailService;

import com.example.mfb_ussd_process_flow.entityUser.Users;
import jakarta.mail.MessagingException;

public interface EmailService {
    void sendRegistrationNotification(Users user) throws MessagingException;
    void sendActivationNotification(Users users, String password) throws MessagingException;
    void sendMailForResetPassword(String email, String firstName, String token);

    void sendMailToSetTransactionPinToken(String email, String newToken);
}
