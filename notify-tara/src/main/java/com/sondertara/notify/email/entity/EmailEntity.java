package com.sondertara.notify.email.entity;

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
    private String subject = "tara email";
    /**
     * 邮件内容
     */
    private String content;
    /**
     * 附件路径
     */
    private String attachFilePath;
}
