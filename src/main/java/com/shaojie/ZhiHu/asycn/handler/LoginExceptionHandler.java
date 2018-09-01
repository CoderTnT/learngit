package com.shaojie.ZhiHu.asycn.handler;

import com.shaojie.ZhiHu.asycn.EventHandler;
import com.shaojie.ZhiHu.asycn.EventModel;
import com.shaojie.ZhiHu.asycn.EventType;
import com.shaojie.ZhiHu.util.MailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;

import java.util.Arrays;
import java.util.List;

@Component
public class LoginExceptionHandler implements EventHandler{


    @Autowired
    MailSender mailSender;

    @Override
    public void doHandle(EventModel eventModel) {


        //进行判断,假如登录异常

//        Context context = new Context();
//        context.setVariable("username",eventModel.getExt("username"));
//
//        mailSender.sendWithHTMLTemplate(eventModel.getExt("email"),
//                "登录IP异常","mails/login_exception",context);


    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LOGIN);
    }
}
