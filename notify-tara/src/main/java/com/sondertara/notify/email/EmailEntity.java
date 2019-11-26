package com.sondertara.notify.email;

import lombok.Builder;
import lombok.Getter;

/**
 * @author huangxiaohu
 */
@Builder
@Getter
public class EmailEntity {
    /**
     * 接收邮箱
     */
    private String to;
    /**
     * 发送邮箱
     */
    private String from;
    /**
     * 邮件主题
     */
    private String subject = "空主题";
    /**
     * 邮件内容
     */
    private String content;

    private String host = "";
    private int port = 0;
    private String username = "";
    private String password = "";
    /**
     * 附件路径
     */
    private String attachFilePath;
}
