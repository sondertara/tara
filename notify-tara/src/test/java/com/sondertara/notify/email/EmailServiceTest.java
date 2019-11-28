package com.sondertara.notify.email;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * test
 *
 * @author huangxiaohu
 * @version 1.0
 * @date 2019/11/7 3:58 下午
 * @since 1.0
 **/
public class EmailServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(EmailServiceTest.class);

    private static ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(8, 16, 120,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(32),
            new ThreadPoolExecutor.AbortPolicy());

    private static final String HOST = "smtp.qiye.aliyun.com";
    private static final int PORT = 25;
    private static final String USERNAME = "huangxiaohu@souche.com";
    private static final String PASSWORD = "hxh@qq0313";

    private EmailService emailService;

    private String to = "huangxiaohu@souche.com";

    private String subject = "测试";


    @Test
    public void sendMail() {


        final EmailEntity excelEntity = EmailEntity.builder().host(HOST).port(PORT).username(USERNAME).password(PASSWORD)
                .to(to).subject(subject).build();
        emailService = new EmailService(excelEntity);
        poolExecutor.execute(() -> {
            logger.info("send email start...");
            emailService.sendMail(true);
            logger.info("send email end...");
        });


    }

}