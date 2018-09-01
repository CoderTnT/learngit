package com.shaojie.ZhiHu.controller;


import com.shaojie.ZhiHu.model.HostHolder;
import com.shaojie.ZhiHu.model.Message;
import com.shaojie.ZhiHu.model.User;
import com.shaojie.ZhiHu.model.ViewObject;
import com.shaojie.ZhiHu.service.MessageService;
import com.shaojie.ZhiHu.service.UserService;
import com.shaojie.ZhiHu.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.swing.text.View;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class MessageController {


    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);


    @Autowired
    MessageService messageService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    UserService userService;



    @RequestMapping(value = "/msg/list",method = RequestMethod.GET)
    public String conversationList(Model model){


        //如果没有登录则让他跳转去登录。
        if(hostHolder.getUser() == null){

            return "redirct:/relogin";
        }

        try{

        int localUserId = hostHolder.getUser().getId();


        List<Message>  messageList = messageService.selectMessageList(localUserId,0,10);


        List<ViewObject> viewObjectList = new ArrayList<>();


        for(Message message :messageList){

            ViewObject viewObject = new ViewObject();
            viewObject.set("message",message);

            //这里显示的是发给你信息的人的头像
            int targetId = message.getFromId()==localUserId?message.getToId():message.getFromId();

            viewObject.set("user",userService.getUser(targetId));

            //获取未读的数据
            viewObject.set("unread",messageService.getConversationUnReadCount(localUserId,message.getConversationId()));

            viewObjectList.add(viewObject);
        }

        model.addAttribute("viewObjectList",viewObjectList);

        }catch (Exception e){

            logger.error("获取私信列表失败"+e.getMessage());
        }

        return "letter";

    }


    @RequestMapping(value = "/msg/detail",method = RequestMethod.GET)
    public String conversationDetail(Model model,
                                     @RequestParam("conversationId") String conversationId ){
        try {


            int localUserId = hostHolder.getUser().getId();

            List<ViewObject> viewObjectList = new ArrayList<>();

            List<Message> messageList = messageService.selectMessageDetailList(conversationId, 0, 10);

            for (Message message : messageList) {
                ViewObject viewObject = new ViewObject();
                viewObject.set("message", message);
                viewObject.set("user", userService.getUser(message.getFromId()));
                viewObjectList.add(viewObject);

            }
            model.addAttribute("viewObjectList", viewObjectList);

            messageService.updateConversationHasRead(localUserId,conversationId,1);

        } catch (Exception e) {
            logger.error("获取详情信息失败" + e.getMessage());

        }
        return "letterDetail";

    }


    @RequestMapping("/msg/addMessage")
    @ResponseBody
    public String addMessage(@RequestParam("toName") String toName,
                             @RequestParam("content") String content) {

        try {

            Message message = new Message();

            if (hostHolder.getUser() == null) {
                return WendaUtil.getJSONString(999, "您未登录");
            }
            User user = userService.selectUserByUserName(toName);

            if (user == null) {

                return WendaUtil.getJSONString(1, "查无此人");
            }

            message.setFromId(hostHolder.getUser().getId());
            message.setToId(user.getId());
            message.setId(user.getId());
            message.setContent(content);
            message.setCreatedDate(new Date());

            messageService.addMessage(message);
            return WendaUtil.getJSONString(0);

        } catch (Exception e) {

            logger.error(e.getMessage() + "添加失败");

            return WendaUtil.getJSONString(1, "插入站内信失败");

        }


    }

}
