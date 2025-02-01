package com.umc.ttt.domain.mail.service;

import com.umc.ttt.domain.mail.handler.EMailHandler;
import jakarta.mail.internet.MimeMessage;

public interface MailService {
    MimeMessage createMail(String mail, String code);

    void sendMail(String mail);

    void verifyCode(String email, String authCode) throws EMailHandler;
}
