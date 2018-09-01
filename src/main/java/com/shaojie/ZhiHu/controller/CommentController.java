package com.shaojie.ZhiHu.controller;


import com.shaojie.ZhiHu.asycn.EventModel;
import com.shaojie.ZhiHu.asycn.EventProducer;
import com.shaojie.ZhiHu.asycn.EventType;
import com.shaojie.ZhiHu.model.Comment;
import com.shaojie.ZhiHu.model.EntityType;
import com.shaojie.ZhiHu.model.HostHolder;
import com.shaojie.ZhiHu.service.CommentService;
import com.shaojie.ZhiHu.service.QuestionService;
import com.shaojie.ZhiHu.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;



@Controller
public class CommentController {

    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    QuestionService questionService;

    @Autowired
    HostHolder hostHolder;
    @Autowired
    CommentService commentService;

    @Autowired
    EventProducer eventProducer;

    @RequestMapping(value = "/addComment",method = RequestMethod.POST)
    public String addComment(@RequestParam("questionId") int questionId,
                             @RequestParam("content") String content){

        try{


        Comment comment = new Comment();
        comment.setContent(content);
        comment.setCreatedDate(new Date());

        comment.setStatus(0);

        if(hostHolder.getUser()==null){

        comment.setUserId(WendaUtil.ANONYMOUS_USERID);
        }else{

            comment.setUserId(hostHolder.getUser().getId());

        }

        comment.setEntityId(questionId);
        comment.setEntityType(EntityType.ENTITY_QUESTION);

        commentService.addComment(comment);

        int count = commentService.getCommentCount(comment.getEntityId(),EntityType.ENTITY_QUESTION);

        questionService.updateQuestionCommentCount(questionId,count);


        eventProducer.fireEvent(new EventModel(EventType.COMMENT).setActorId(comment.getUserId())
                                .setEntityType(EntityType.ENTITY_QUESTION)
                                .setEntityId(questionId));


        }catch (Exception e){

            logger.error("评论添加失败"+e.getMessage());
        }

        return "redirect:/question/"+String.valueOf(questionId);
    }
}
