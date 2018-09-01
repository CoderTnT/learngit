package com.shaojie.ZhiHu.asycn.handler;


import com.shaojie.ZhiHu.asycn.EventHandler;
import com.shaojie.ZhiHu.asycn.EventModel;
import com.shaojie.ZhiHu.asycn.EventType;
import com.shaojie.ZhiHu.model.Message;
import com.shaojie.ZhiHu.model.User;
import com.shaojie.ZhiHu.service.MessageService;
import com.shaojie.ZhiHu.service.UserService;
import com.shaojie.ZhiHu.util.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class LikeHandler implements EventHandler {

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;


    @Override
    public void doHandle(EventModel eventModel) {

        Message message = new Message();
        message.setToId(eventModel.getEntityOwnerId());
        message.setCreatedDate(new Date());
        message.setFromId(WendaUtil.SYSTEM_USERID);
        User user = userService.getUser(eventModel.getActorId());
        message.setContent("用户"+user.getName()+"给你的评论,http://localhost:8080/question/"+
                eventModel.getExt("questionId")+" 点了个赞");


        messageService.addMessage(message);

    }

    @Override
    public List<EventType> getSupportEventTypes() {


        return Arrays.asList(EventType.LIKE);
    }



}
