package com.sondertara.notify.email;

import com.sondertara.common.model.ResultDTO;
import com.sondertara.notify.Notifier;
import com.sondertara.notify.common.NotifyPlatform;
import com.sondertara.notify.common.NotifyPlatformEnum;
import com.sondertara.notify.email.entity.EmailEntity;

import java.util.Properties;

/**
 * @author huangxiaohu
 */
public class EmailNotifier implements Notifier {

    private final EmailEntity emailEntity;
    private final Properties properties;

    public EmailNotifier(EmailEntity emailEntity, Properties properties) {
        this.emailEntity = emailEntity;
        this.properties = properties;
    }

    @Override
    public String platform() {
        return NotifyPlatformEnum.EMAIL.name().toLowerCase();
    }

    @Override
    public ResultDTO<String> send(NotifyPlatform platform, String content) {
        return new EmailClient(emailEntity, properties).sendMail(true);
    }
}
