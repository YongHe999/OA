package com.seu.email.service;

public interface MailService {

    // 发送简单文本的邮件
    boolean send(String to, String subject, String content);

    boolean sendWithHtml(String to, String subject, String html);

    boolean sendWithImgHtml(String to, String subject, String html, String[] cids, String[] filePaths);

    boolean sendWithEnclosure(String to, String subject, String content, String[] filePaths);
}
