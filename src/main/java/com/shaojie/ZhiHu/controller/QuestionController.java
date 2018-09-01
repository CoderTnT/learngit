package com.shaojie.ZhiHu.controller;


import com.shaojie.ZhiHu.asycn.EventModel;
import com.shaojie.ZhiHu.asycn.EventProducer;
import com.shaojie.ZhiHu.asycn.EventType;
import com.shaojie.ZhiHu.model.*;
import com.shaojie.ZhiHu.service.*;
import com.shaojie.ZhiHu.util.WendaUtil;
import javassist.compiler.ast.MethodDecl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class QuestionController {

        private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);

    @Autowired
    HostHolder hostHolder;

    @Autowired
    QuestionService questionService;

    @Autowired
    EventProducer eventProducer;


    @Autowired
    UserService userService;

    @Autowired
    CommentService commentService;

    @Autowired
    LikeService likeService;

    @Autowired
    FollowService followService;

    //@ResponseBody不会走视图处理器。一般返回json或者xml文件格式
    @RequestMapping(value="/question/add",method = RequestMethod.POST)
    @ResponseBody
    public String addQuestion(@RequestParam("title") String title,
                              @RequestParam("content") String content){

        try{
            Question question = new Question();
            question.setTitle(title);
            question.setCreatedDate(new Date());
            question.setContent(content);
            question.setCommentCount(0);
            if(hostHolder.getUser() == null){
                //给未登录的用户也有发表问题的权限，默认系统id
                question.setUserId(WendaUtil.ANONYMOUS_USERID);

            }else{
                question.setUserId(hostHolder.getUser().getId());

            }


            if(questionService.addQuestion(question) >0){

                eventProducer.fireEvent(new EventModel(EventType.ADD_QUESTION).setActorId(question.getUserId())
                                                        .setEntityId(question.getId())
                                                        .setExt("title",question.getTitle())
                                                        .setExt("content",question.getContent()));

                return WendaUtil.getJSONString(0);
            }
        }catch (Exception e){

            logger.error("提交问题失败"+e.getMessage());
        }

        return WendaUtil.getJSONString(1,"提问失败");
    }


    @RequestMapping("/question/{qid}")
    public String questionDetail(Model model, @PathVariable("qid") int qid) {

        //是否关注

        if(hostHolder.getUser() !=null){
            model.addAttribute("followed", followService.isFollower(hostHolder.getUser().getId()
                    , EntityType.ENTITY_QUESTION, qid));
        }else{
            model.addAttribute("followed", false);
        }


        List<ViewObject> followUsers = new ArrayList<>();

        List<Integer> followersId = followService.getFollowers(EntityType.ENTITY_QUESTION, qid, 10);


        for (int follower : followersId) {

            ViewObject vo = new ViewObject();
            User user = userService.getUser(follower);
            vo.set("name", user.getName());
            vo.set("headUrl", user.getHeadUrl());
            vo.set("id", user.getId());
            followUsers.add(vo);

        }

        model.addAttribute("followUsers", followUsers);

        model.addAttribute("size",followersId.size());


        //----------------华丽分割线--------------

        Question question = questionService.selectQuestionById(qid);

        model.addAttribute("question", question);

        User user = userService.getUser(question.getUserId());

        model.addAttribute("user", user);

        List<ViewObject> viewObjects = new ArrayList<>();

        List<Comment> comments = commentService.selectCommentListById(qid, EntityType.ENTITY_QUESTION);

        for (Comment comment : comments) {

            ViewObject viewObject = new ViewObject();
            viewObject.set("user", userService.getUser(comment.getUserId()));

            if (hostHolder.getUser() == null) {

                viewObject.set("liked", 0);
            } else {


                viewObject.set("liked", likeService.getStatus(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, comment.getId()));

            }

            viewObject.set("likeCount", likeService.getLikeCount(EntityType.ENTITY_COMMENT, comment.getId()));


            viewObject.set("comment", comment);
            viewObjects.add(viewObject);

        }
        model.addAttribute("viewObjects", viewObjects);


        return "detail";
    }













}
