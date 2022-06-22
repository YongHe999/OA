package com.seu.email.service.Impl;

import com.seu.email.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;

/**
 * @author Ajie
 * @Date 2020/11/1
 */

@Slf4j
@Service
public class MailServiceImpl implements MailService {

    @Resource
    private MailProperties mailProperties;
    @Resource
    private JavaMailSender javaMailSender;

    /**
     * 发送简单文本
     * @param to
     * @param subject
     * @param content
     * @return
     */
    @Override
    public boolean send(String to, String subject, String content) {

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        // 发送来源
        simpleMailMessage.setFrom(mailProperties.getUsername());
        // 发送目标
        simpleMailMessage.setTo(to);
        // 标题
        simpleMailMessage.setSubject(subject);
        // 内容
        simpleMailMessage.setText(content);

        try {
            javaMailSender.send(simpleMailMessage);
        }catch (Exception e){
            log.error("[email error]:"+ e);
            return false;
        }
        return true;
    }

    /**
     * 发送html邮件
     * @param to
     * @param subject
     * @param html
     * @return
     */
    @Override
    public boolean sendWithHtml(String to, String subject, String html) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;
        try {
            mimeMessageHelper = new MimeMessageHelper(mimeMessage,true);
            // 发送来源
            mimeMessageHelper.setFrom(mailProperties.getUsername());
            // 发送目标
            mimeMessageHelper.setTo(to);
            // 标题
            mimeMessageHelper.setSubject(subject);
            // html内容,设置html为true
            mimeMessageHelper.setText(html,true);
            javaMailSender.send(mimeMessage);
        }catch (Exception e){
            log.error("[email html error]:"+ e);
            return false;
        }
        return true;
    }

    /**
     * 发送带有图片的 html 邮件
     * @param to
     * @param subject
     * @param html
     * @param cids
     * @param filePaths
     * @return
     */
    @Override
    public boolean sendWithImgHtml(String to, String subject, String html, String[] cids, String[] filePaths) {

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;

        try {
            mimeMessageHelper = new MimeMessageHelper(mimeMessage,true);
            // 发送来源
            mimeMessageHelper.setFrom(mailProperties.getUsername());
            // 发送目标
            mimeMessageHelper.setTo(to);
            // 标题
            mimeMessageHelper.setSubject(subject);
            // html内容,设置 html为true
            mimeMessageHelper.setText(html,true);

            // 设置 html中的图片
            for(int i = 0; i <= cids.length; i++){
                FileSystemResource file = new FileSystemResource(filePaths[i]);
                mimeMessageHelper.addInline(cids[i],file);
            }

            javaMailSender.send(mimeMessage);
        }catch (Exception e){
            log.error("[email html error]:"+ e);
            return false;
        }
        return true;
    }

    /**
     * 发送带附件的邮件
     * @param to
     * @param subject
     * @param content
     * @param filePaths
     * @return
     */
    @Override
    public boolean sendWithEnclosure(String to, String subject, String content, String[] filePaths) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;

        try {
            mimeMessageHelper = new MimeMessageHelper(mimeMessage,true);
            // 发送来源
            mimeMessageHelper.setFrom(mailProperties.getUsername());
            // 发送目标
            mimeMessageHelper.setTo(to);
            // 标题
            mimeMessageHelper.setSubject(subject);
            // html内容,设置html为true
            mimeMessageHelper.setText(content,true);
            for(int i = 0; i <= filePaths.length; i++){
                FileSystemResource file = new FileSystemResource(filePaths[i]);
                String attachmentFileName= "附件" + (i+1);
                mimeMessageHelper.addAttachment(attachmentFileName,file);
            }

            javaMailSender.send(mimeMessage);
        }catch (Exception e){
            log.error("[html email error]:"+ e);
            return false;
        }
        return true;
    }
}
