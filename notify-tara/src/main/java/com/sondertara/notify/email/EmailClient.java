package com.sondertara.notify.email;

import com.sondertara.common.model.ResultDTO;
import com.sondertara.notify.email.entity.EmailEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * the email service
 * <p>
 * first touch a properties file which should contain some config like this:
 * user.name=your email username
 * password=your password
 * mail.smtp.auth=true
 * #mail.smtp.starttls.enable=true
 * mail.smtp.host= your email services host.eg:smtp.qiye.aliyun.com
 * mail.smtp.port=your email services port,eg:25
 * #mail.smtp.ssl.trust=host
 * mail.transport.protocol=smtp
 * <p>
 * then this service can be work.
 * </p>
 *
 * @author huangxiaohu
 */
public class EmailClient {
    private static final Logger logger = LoggerFactory.getLogger(EmailClient.class);

    private final EmailEntity emailEntity;
    private final Properties prop;

    /**
     * @param emailEntity email config
     * @param prop        session property
     */
    public EmailClient(EmailEntity emailEntity, Properties prop) {
        this.emailEntity = emailEntity;
        this.prop = prop;
    }

    public ResultDTO<String> sendMail(boolean deleteAttachFile) {
        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(prop.getProperty("user.name", "user_name"),
                        prop.getProperty("password", "password"));
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailEntity.getFrom()));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailEntity.getTo()));
            message.setSubject(emailEntity.getSubject());
            Multipart multipart = null;
            if (null != emailEntity.getContent()) {
                multipart = new MimeMultipart();
                MimeBodyPart mimeBodyPart = new MimeBodyPart();
                mimeBodyPart.setText(emailEntity.getContent(), "UTF-8");
                multipart.addBodyPart(mimeBodyPart);
            }
            File file = null;
            if (null != emailEntity.getAttachFilePath()) {
                MimeBodyPart attachmentBodyPart = new MimeBodyPart();
                file = new File(emailEntity.getAttachFilePath());
                attachmentBodyPart.attachFile(file);
                if (null == multipart) {
                    multipart = new MimeMultipart();
                }
                multipart.addBodyPart(attachmentBodyPart);
            }
            if (null == multipart) {
                message.setText(emailEntity.getSubject());
            } else {

                message.setContent(multipart);
            }

            Transport.send(message);
            logger.info("send email success");

            if (deleteAttachFile && null != file) {
                file.delete();
            }
            return ResultDTO.success("Send email success");
        } catch (IOException e) {
            logger.error("file resolve error:", e);
            return ResultDTO.fail(e.getMessage());
        } catch (MessagingException e) {
            logger.error("send email error:", e);
            return ResultDTO.fail(e.getMessage());
        }
    }

}
