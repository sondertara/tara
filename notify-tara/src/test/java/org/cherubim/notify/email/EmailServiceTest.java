package org.cherubim.notify.email;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

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
@Slf4j
public class EmailServiceTest {

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
            log.info("send email start...");
            emailService.sendMail(true);
            log.info("send email end...");
        });


    }

}