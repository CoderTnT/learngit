package com.shaojie.ZhiHu.controller;

import com.shaojie.ZhiHu.asycn.EventModel;
import com.shaojie.ZhiHu.asycn.EventProducer;
import com.shaojie.ZhiHu.asycn.EventType;
import com.shaojie.ZhiHu.model.Comment;
import com.shaojie.ZhiHu.model.EntityType;
import com.shaojie.ZhiHu.model.HostHolder;
import com.shaojie.ZhiHu.model.Question;
import com.shaojie.ZhiHu.service.CommentService;
import com.shaojie.ZhiHu.service.LikeService;
import com.shaojie.ZhiHu.service.QuestionService;
import com.shaojie.ZhiHu.util.RedisKeyUtil;
import com.shaojie.ZhiHu.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LikeController {

    @Autowired
    HostHolder hostHolder;

    @Autowired
    LikeService likeService;

    @Autowired
    EventProducer eventProducer;

    @Autowired
    CommentService commentService;

    @Autowired
    QuestionService questionService;

    private static final Logger logger = LoggerFactory.getLogger(LikeController.class);


    @RequestMapping("/like")
    @ResponseBody
    public String like(@RequestParam("commentId") int commentId) {

        if (hostHolder.getUser() == null) {

            return WendaUtil.getJSONString(999);
        }

        int userId = hostHolder.getUser().getId();

        Comment comment = commentService.getCommentById(commentId);

        eventProducer.fireEvent(new EventModel(EventType.LIKE).setActorId(userId)
                .setEntityId(commentId)
                .setEntityType(EntityType.ENTITY_COMMENT)
                .setExt("questionId", String.valueOf(comment.getEntityId()))
                .setEntityOwnerId(comment.getUserId()));

        long likecount = likeService.like(userId, commentId, EntityType.ENTITY_COMMENT);

        return WendaUtil.getJSONString(0, String.valueOf(likecount));

    }

    @RequestMapping("/dislike")
    @ResponseBody
    public String dislike(@RequestParam("commentId") int commentId){

        if(hostHolder.getUser() == null){

            return WendaUtil.getJSONString(999);
        }

        int userId = hostHolder.getUser().getId();

        long likecount = likeService.disLike(userId,commentId, EntityType.ENTITY_COMMENT);


        return WendaUtil.getJSONString(0,String.valueOf(likecount));


    }



}
