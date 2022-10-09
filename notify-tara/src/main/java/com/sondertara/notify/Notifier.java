package com.sondertara.notify;

import com.sondertara.common.model.ResultDTO;
import com.sondertara.notify.common.NotifyPlatform;

/**
 * Notifier related
 *
 * @author huangxiaohu
 * @since 1.0.8
 */
public interface Notifier {

    /**
     * Get the platform name.
     *
     * @return platform
     */
    String platform();

    /**
     * Send message.
     *
     * @param platform platform
     * @param content  content
     */
    ResultDTO<String> send(NotifyPlatform platform, String content);

}
