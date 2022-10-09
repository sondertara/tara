package com.sondertara.notify.email;

import com.sondertara.common.util.PropertiesUtils;
import com.sondertara.notify.email.entity.EmailEntity;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * test
 *
 * @author huangxiaohu
 * @version 1.0
 * @since 1.0
 * date 2019/11/7 3:58 下午
 **/
public class EmailClientTest {
    private static final Logger logger = LoggerFactory.getLogger(EmailClientTest.class);

    private EmailClient emailClient;

    private String to = "email address";
    private String from = "email address";

    private String subject = "test";


    @Test
    public void sendMail() {


        final EmailEntity excelEntity = EmailEntity.builder().to(to).subject(subject).from(from).content("这是一个测试").build();
        Properties properties = PropertiesUtils.getInstance("email").getProperties();

        emailClient = new EmailClient(excelEntity, properties);

        logger.info("send email start...");
        emailClient.sendMail(true);
        logger.info("send email end...");


    }

}