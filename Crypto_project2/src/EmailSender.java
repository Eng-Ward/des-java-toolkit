import java.io.File;
import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.*;

public class EmailSender {

    private final String senderEmailAddress;
    private final String senderPassword;
    private final String smtpHost;
    private final String smtpPort;

    public EmailSender(String senderEmailAddress, String senderPassword) {
        this.senderEmailAddress = senderEmailAddress;
        this.senderPassword = senderPassword;
        this.smtpHost = "smtp.gmail.com";
        this.smtpPort = "587";
    }

    public boolean sendCiphertextEmail(String recipientEmail, String subjectLine, String ciphertextBody) {
        try {
            Session mailSession = createMailSession();
            Message emailMessage = buildEmailMessage(mailSession, recipientEmail, subjectLine, ciphertextBody);
            Transport.send(emailMessage);
            System.out.println("Email sent successfully to: " + recipientEmail);
            return true;
        } catch (MessagingException e) {
            System.out.println("Failed to send email: " + e.getMessage());
            return false;
        }
    }

    public boolean sendCiphertextEmailWithAttachment(String recipientEmail, String subjectLine,
                                                     String messageBody, String attachmentFilePath) {
        try {
            Session mailSession = createMailSession();

            MimeMessage emailMessage = new MimeMessage(mailSession);
            emailMessage.setFrom(new InternetAddress(senderEmailAddress));
            emailMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
            emailMessage.setSubject(subjectLine);

            MimeBodyPart textBodyPart = new MimeBodyPart();
            textBodyPart.setText(messageBody);

            MimeBodyPart fileAttachmentPart = new MimeBodyPart();
            fileAttachmentPart.attachFile(new File(attachmentFilePath));

            Multipart multipartContent = new MimeMultipart();
            multipartContent.addBodyPart(textBodyPart);
            multipartContent.addBodyPart(fileAttachmentPart);

            emailMessage.setContent(multipartContent);
            Transport.send(emailMessage);

            System.out.println("Email with attachment sent successfully to: " + recipientEmail);
            return true;
        } catch (Exception e) {
            System.out.println("Failed to send email with attachment: " + e.getMessage());
            return false;
        }
    }

    private Session createMailSession() {
        Properties smtpProperties = new Properties();
        smtpProperties.put("mail.smtp.auth", "true");
        smtpProperties.put("mail.smtp.starttls.enable", "true");
        smtpProperties.put("mail.smtp.host", smtpHost);
        smtpProperties.put("mail.smtp.port", smtpPort);

        return Session.getInstance(smtpProperties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmailAddress, senderPassword);
            }
        });
    }

    private Message buildEmailMessage(Session mailSession, String recipientEmail,
                                      String subjectLine, String messageBody) throws MessagingException {
        Message emailMessage = new MimeMessage(mailSession);
        emailMessage.setFrom(new InternetAddress(senderEmailAddress));
        emailMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
        emailMessage.setSubject(subjectLine);
        emailMessage.setText(messageBody);
        return emailMessage;
    }
}
