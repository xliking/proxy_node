package com.easy.utils.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import java.util.Date;

/**
 * @author muchi
 */
@Component
public class EmailUtils {
    //定义邮件发送器
    @Autowired
    private JavaMailSender mailSender;


    @Async
    public void send(String to, String title, String content) {
        //创建一个简单文本邮件的对象
        SimpleMailMessage message = new SimpleMailMessage();
        //目标邮箱
        message.setTo(to);
        message.setSubject(title);
        message.setText(content);
        // 需要与配置文件发件人的值相同
        message.setFrom("linux@xlike.email");
        message.setSentDate(new Date());
        //将邮件对象赋予邮件发送器
        mailSender.send(message);
    }


    @Async
    public void sendHtmlEmail(String to, String title, String content) {
        // 创建一个MIME类型的邮件对象
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = null;
        try {
            helper = new MimeMessageHelper(message, true);
            // 设置目标邮箱
            helper.setTo(to);
            // 设置邮件标题
            helper.setSubject(title);
            // 设置发件人
            helper.setFrom("linux@xlike.email");
            // 设置发送日期
            helper.setSentDate(new Date());
        } catch (MessagingException e) {
            return;
        }
        // 构建HTML内容
        String htmlContent = "<html>" +
                "<body style='font-family: Arial, sans-serif; line-height: 1.6; background-color: #f9f9f9; padding: 20px;'>" +
                "<div style='max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); overflow: hidden;'>" +
                "<div style='background-color: #2E86C1; padding: 20px; text-align: center;'>" +
                "<h1 style='color: #ffffff; margin: 0;'>API-KEY捐赠</h1>" +
                "</div>" +
                "<div style='padding: 30px;'>" +
                "<div style='padding: 20px; background-color: #f0f0f0; border-radius: 5px; margin: 20px 0; text-align: center;'>" +
                "<p style='font-size: 24px; color: #444; font-weight: bold;'>" + content + "</p>" +
                "</div>" +
                "</div>" +
                "<div style='background-color: #f1f1f1; padding: 10px 20px; text-align: center; font-size: 12px; color: #777;'>" +
                "<p>此邮件为系统自动发送，请勿回复。</p>" +
                "<p> ? 2024 </p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
        try {
            helper.setText(htmlContent, true);
        } catch (MessagingException e) {
            return;
        }
        mailSender.send(message);
    }

}
