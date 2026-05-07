package com.timess.soundpulse.utils;

import jakarta.annotation.Resource;
import jakarta.mail.internet.MimeMessage;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;


/**
 * @author mijiupro
 */
@Component
@Slf4j
public class EmailApi {
    @Resource
    private  JavaMailSender mailSender; // Java邮件发送器

    @Autowired
    private  StringRedisTemplate redisTemplate; // Redis操作模板

    // 发件人
    @Value("${spring.mail.username}")
    private String from ; // 发件人邮箱地址

    /**
     * 发送纯文本的邮件
     * @param subject 主题
     * @return 是否成功
     */
    public   boolean sendGeneralEmail(String subject,String mail){
        //生成六位数字验证码
        // 生成范围：[10^(length-1), 10^length - 1]
        long min = (long) Math.pow(10,5); // 最小值100000
        long max = (long) Math.pow(10, 6) - 1; // 最大值999999
        long randomNum = ThreadLocalRandom.current().nextLong(min, max + 1); // 生成随机数
        String code = String.valueOf(randomNum); // 将随机数转为字符串作为验证码
        saveVerifyCodeOnRedis(mail, code); // 将验证码保存到Redis中
        // 创建邮件消息
        SimpleMailMessage message = new SimpleMailMessage(); // 创建简单邮件消息对象
        message.setFrom(from); // 设置发件人
        // 设置收件人
        message.setTo(mail); // 设置收件人
        // 设置邮件主题
        message.setSubject(subject); // 设置邮件主题
        // 设置邮件内容
        message.setText(code); // 设置邮件内容为验证码
        // 发送邮件
        mailSender.send(message); // 发送邮件
        return true; // 返回发送成功
    }
    /**
     * 发送html的邮件
     * @param to 收件人
     * @param subject 主题
     * @param content 内容
     * @return 是否成功
     */
    @SneakyThrows(Exception.class)
    public  boolean sendHtmlEmail(String subject, String content, String... to){
        // 创建邮件消息
        MimeMessage mimeMessage = mailSender.createMimeMessage(); // 创建MIME邮件消息对象
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true); // 创建MIME消息助手，支持HTML
        helper.setFrom(from); // 设置发件人
        // 设置收件人
        helper.setTo(to); // 设置收件人
        // 设置邮件主题
        helper.setSubject(subject); // 设置邮件主题
        // 设置邮件内容
        helper.setText(content, true); // 设置邮件内容，并指定为HTML格式
 
        // 发送邮件
        mailSender.send(mimeMessage); // 发送邮件
 
        log.info("发送邮件成功"); // 记录发送成功的日志
        return true; // 返回发送成功
 
    }
    /**
     * 发送带附件的邮件
     * @param to 收件人
     * @param subject 主题
     * @param content 内容
     * @param filePaths 附件路径
     * @return 是否成功
     */
    @SneakyThrows(Exception.class)
    public  boolean sendAttachmentsEmail(String subject, String content, String[] to, String[] filePaths) {
        // 创建邮件消息
        MimeMessage mimeMessage = mailSender.createMimeMessage(); // 创建MIME邮件消息对象
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true); // 创建MIME消息助手，支持附件
        helper.setFrom(from); // 设置发件人
        // 设置收件人
        helper.setTo(to); // 设置收件人
        // 设置邮件主题
        helper.setSubject(subject); // 设置邮件主题
        // 设置邮件内容
        helper.setText(content,true); // 设置邮件内容，并指定为HTML格式
 
        // 添加附件
        if (filePaths != null) { // 检查附件路径数组是否为空
            for (String filePath : filePaths) { // 遍历附件路径数组
                FileSystemResource file = new FileSystemResource(new File(filePath)); // 创建文件系统资源
                helper.addAttachment(Objects.requireNonNull(file.getFilename()), file); // 添加附件
 
            }
        }
        // 发送邮件
        mailSender.send(mimeMessage); // 发送邮件
        return true; // 返回发送成功
    }
 
    /**
     * 发送带静态资源的邮件
     * @param to 收件人
     * @param subject 主题
     * @param content 内容
     * @param rscPath 静态资源路径
     * @param rscId 静态资源id
     * @return 是否成功
     */
    @SneakyThrows(Exception.class)
    public  boolean sendInlineResourceEmail(String subject, String content, String to, String rscPath, String rscId) {
        // 创建邮件消息
        MimeMessage mimeMessage = mailSender.createMimeMessage(); // 创建MIME邮件消息对象
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true); // 创建MIME消息助手，支持内嵌资源
        // 设置发件人
        helper.setFrom(from); // 设置发件人
        // 设置收件人
        helper.setTo(to); // 设置收件人
        // 设置邮件主题
        helper.setSubject(subject); // 设置邮件主题
 
        //html内容图片
        String contentHtml = "<html><body>这是邮件的内容，包含一个图片：<img src=\'cid:" + rscId + "\'>"+content+"</body></html>"; // 构建HTML内容，包含内嵌图片
 
        helper.setText(contentHtml, true); // 设置邮件内容，并指定为HTML格式
        //指定讲资源地址
        FileSystemResource res = new FileSystemResource(new File(rscPath)); // 创建文件系统资源
        helper.addInline(rscId, res); // 添加内嵌资源
 
        mailSender.send(mimeMessage); // 发送邮件
        return true; // 返回发送成功
    }

    private  void saveVerifyCodeOnRedis(String mail, String code){
        String key = buildVerifyCodeKey(mail); // 构建Redis键
        redisTemplate.opsForValue().set(key, code, 5, TimeUnit.MINUTES); // 设置验证码到Redis，过期时间为5分钟
    }
    public static String buildVerifyCodeKey(String mail){
        return "verification_code:" + mail; // 构建验证码的Redis键
    }
}