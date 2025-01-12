package com.sportlink.sportlink.utils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


@Service
@AllArgsConstructor
@NoArgsConstructor
public class EmailSender {

    @Value("${email.isActive}")
    boolean emailSenderActive;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private TemplateEngine templateEngine;

    public void sendOtpRegistrationEmail(String to, String otp) throws MessagingException {
        String subject = "Your OTP for Registration";
        String body = generateTemplate("otp-registration-email", "otp", otp);
        sendHtmlEmail(to, subject, body);
    }

    public void sendOtpPasswordChangeEmail(String to, String otp) throws MessagingException {
        String subject = "Your OTP for Password Change";
        String body = generateTemplate("otp-password-change", "otp", otp);
        sendHtmlEmail(to, subject, body);
    }

    public void sendAdminCompanyRegistrationRequest(String to, String accountId) throws MessagingException {
        String subject = "Company Registration Request";
        String body = generateTemplate("admin-company-registration", "accountId", accountId);
        sendHtmlEmail(to, subject, body);
    }

    public void sendHtmlEmail(String to, String subject, String htmlBody) throws MessagingException {
        if (!emailSenderActive) {
            return;
        }
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        helper.setFrom("your-email@gmail.com");

        mailSender.send(mimeMessage);
    }

    private String generateTemplate(String templateName, String variableName, String variableValue) {
        Context context = new Context();
        context.setVariable(variableName, variableValue);
        return templateEngine.process(templateName, context);
    }

}
