package com.shaojie.ZhiHu.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.util.Map;
import java.util.Properties;


@Service
public class MailSender implements InitializingBean{

    private static final Logger logger = LoggerFactory.getLogger(MailSender.class);

    private JavaMailSenderImpl mailSender;


    @Autowired
    private TemplateEngine templateEngine;

    //发送邮件接口
    public boolean sendWithHTMLTemplate(String to, String subject, String template,
                                        Context context) {

        try{

            //昵称
            String nick = MimeUtility.encodeText("廖少杰");
            InternetAddress from = new InternetAddress(nick+"<327668196@qq.com>");

            //文本信息
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

            String result = templateEngine.process(template,context);

            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setFrom(from);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(result,true);
            //发送
            mailSender.send(mimeMessage);

            return true;
        }catch (Exception e){

            logger.error("发送邮件失败"+e.getMessage());
            return false;
        }


    }



    @Override
    public void afterPropertiesSet() throws Exception {

        //构造好邮箱地址，密码，协议，端口，账号
        mailSender = new JavaMailSenderImpl();

        mailSender.setUsername("327668196@qq.com");
        mailSender.setPassword("bosoncvmipicbjci");
        mailSender.setHost("smtp.qq.com");
        mailSender.setPort(465);
        mailSender.setProtocol("smtps");
        mailSender.setDefaultEncoding("utf8");
        Properties javaMailProperties = new Properties();
        javaMailProperties.put("mail.smtp.ssl.enable", true);
        mailSender.setJavaMailProperties(javaMailProperties);

    }
}
