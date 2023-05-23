package com.sondertara.mail;

import com.sondertara.common.model.ResultDTO;
import com.sondertara.notify.email.EmailClient;
import com.sondertara.notify.email.entity.EmailEntity;
import com.sun.mail.util.MailSSLSocketFactory;

import java.security.GeneralSecurityException;
import java.util.Properties;

public class TestMail {
    public static void main(String[] args) throws GeneralSecurityException {

        {
            EmailEntity emailEntity = EmailEntity.builder().to("yanhuihui.zh@ccb.com")
                    .content("这是测试")
                    .from("cpms.zh@ccb.com")
                    .subject("你好这是一个需要填写的")
                    .build();
            Properties properties = new Properties();
            properties.put("mail.transport.protocol", "smtp");
            properties.put("username", "cpms.zh@ccb.com");
            properties.put("password", "n9LjYBr6q2Vjq#H");
            properties.put("mail.smtp.host", "10.239.10.10");
            properties.put("mail.smtp.port", "465");

            properties.put("mail.smtp.auth", "true");
            MailSSLSocketFactory factory = new MailSSLSocketFactory("TLSv1.3");
            factory.setTrustAllHosts(true);
            properties.put("mail.smtp.socketFactory", factory);
            properties.put("mail.smtp.socketFactory.port", "465");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.starttls.required", "true");
            properties.put("mail.smtp.ssl.protocols", "TLSv1.3");
            properties.put("mail.smtp.ssl.trust", "*");
            EmailClient emailClient = new EmailClient(emailEntity, properties);
            ResultDTO<String> resultDTO = emailClient.sendMail(true);
            System.out.println(resultDTO);
        }
    }
}
