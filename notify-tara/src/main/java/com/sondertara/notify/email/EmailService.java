package com.sondertara.notify.email;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * @author huangxiaohu
 * 邮件发送服务
 */
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private EmailEntity emailEntity;

    public EmailService(EmailEntity emailEntity) {
        this.emailEntity = emailEntity;
    }

    public void sendMail(boolean deleteAttachFile) {

        Properties prop = new Properties();
        prop.put("mail.smtp.auth", true);
//        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", emailEntity.getHost());
        prop.put("mail.smtp.port", emailEntity.getPort());
//        prop.put("mail.smtp.ssl.trust", host);
        prop.setProperty("mail.transport.protocol", "smtp");

        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailEntity.getUsername(), emailEntity.getPassword());
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailEntity.getFrom()));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailEntity.getTo()));
            message.setSubject(emailEntity.getSubject());
            Multipart multipart = new MimeMultipart();
            if (null != emailEntity.getContent()) {
                MimeBodyPart mimeBodyPart = new MimeBodyPart();
                mimeBodyPart.setText(emailEntity.getContent(), "UTF-8");
                multipart.addBodyPart(mimeBodyPart);
            }
            File file = null;
            if (null != emailEntity.getAttachFilePath()) {
                MimeBodyPart attachmentBodyPart = new MimeBodyPart();
                file = new File(emailEntity.getAttachFilePath());
                attachmentBodyPart.attachFile(file);
                multipart.addBodyPart(attachmentBodyPart);

            }
            message.setContent(multipart);
            Transport.send(message);
            logger.info("send email success");

            if (deleteAttachFile && null != file) {
                file.delete();
            }
        } catch (IOException e) {
            logger.error("file resolve error:", e);
        } catch (MessagingException e) {
            logger.error("send email error:", e);
        }
    }

}
