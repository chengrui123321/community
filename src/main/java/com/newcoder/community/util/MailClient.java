package com.newcoder.community.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * 邮件客户端
 */
@Component
@Slf4j
public class MailClient {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    /**
     * 发送邮件
     * @param to 邮件接收方
     * @param title 邮件标题
     * @param content 邮件内容
     */
    public void sendMail(String to, String title, String content) {
        try {
            // 创建MimeMessage
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            // 创建MimeMessageHelper
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
            // 设置发送者
            helper.setFrom(from);
            // 设置接收者
            helper.setTo(to);
            // 设置标题
            helper.setSubject(title);
            // 设置内容
            helper.setText(content);
            // 发送邮件
            mailSender.send(helper.getMimeMessage());
        } catch (MessagingException e) {
            log.error("发送邮件失败: " + e.getMessage());
        }
    }
}
