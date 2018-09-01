package com.shaojie.ZhiHu.service;


import com.shaojie.ZhiHu.dao.MessageDAO;
import com.shaojie.ZhiHu.model.Message;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    MessageDAO messageDAO;

    @Autowired
    SensitiveService sensitiveService;

    public int addMessage(Message message){

        message.setContent(sensitiveService.textFilter(message.getContent()));

        return messageDAO.addMessage(message)>0?message.getId():0;

    }

    public List<Message> selectMessageDetailList(String conversationId,int offset,int limit){

        return messageDAO.selectMessageDetailList(conversationId,offset,limit);

    }

    public List<Message> selectMessageList(int userId,int offset,int limit){

        return messageDAO.selectMessageList(userId, offset, limit);
    }




    public int getConversationUnReadCount(int userId,String conversationId){


        return messageDAO.getConversationUnReadCount(userId,conversationId);


    }

    public void updateConversationHasRead(int userId, String conversationId, int hasRead){

        messageDAO.updateConversationHasRead(userId,conversationId,hasRead);

    }










}
