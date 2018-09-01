package com.shaojie.ZhiHu.controller;


import com.alibaba.fastjson.JSONObject;
import com.shaojie.ZhiHu.model.*;
import com.shaojie.ZhiHu.service.CommentService;
import com.shaojie.ZhiHu.service.FollowService;
import com.shaojie.ZhiHu.service.QuestionService;
import com.shaojie.ZhiHu.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.swing.text.View;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);


    @Autowired
    HostHolder hostHolder;

    @Autowired
    QuestionService questionService;

    @Autowired
    UserService userService;

    @Autowired
    FollowService followService;

    @Autowired
    CommentService commentService;



    @RequestMapping(path={"/user/{userId}"})
    public String userIndex(Model model, @PathVariable("userId") int userId){

        model.addAttribute("viewObjects",getQuestions(userId,0,10));


        User user = userService.getUser(userId);
        ViewObject vo = new ViewObject();

        vo.set("user",user);

        vo.set("commentCount",commentService.getUserCommentCount(userId));

        vo.set("followerCount",followService.getFollowersCount(EntityType.ENTITY_USER,userId));
        vo.set("followeeCount",followService.getFolloweesCount(userId,EntityType.ENTITY_USER));

        if(hostHolder.getUser() !=null){

            vo.set("followed",followService.isFollower(hostHolder.getUser().getId(),EntityType.ENTITY_USER,userId));

        }else{
            vo.set("followed",false);
        }

        model.addAttribute("profileUser",vo);

        return "profile";

    }

    @RequestMapping(path={"/index","/"})
    public String index(Model model,@RequestParam(value="tab",required = false) String tab){


        if(tab != null ){

            model.addAttribute("viewObjects",getQuestionsByType(0,0,10,tab));

            return "index";
        }

        model.addAttribute("viewObjects",getQuestions(0,0,10));

      //  followService.follow(22, EntityType.ENTITY_USER,41);

        return "index";
    }


    @RequestMapping(path = {"/more"})
    @ResponseBody
    public String more(@RequestParam(value = "offset",defaultValue = "0") int offset,
                       @RequestParam(value="size",defaultValue = "10") int size,
            @RequestParam(value="tab",required = false) String tab){



        if(tab != null ){

            List<ViewObject> vos = getQuestionsByType(0,offset,size,tab);
            String jsonString = JSONObject.toJSONString(vos);
            return jsonString;
        }



        List<ViewObject> vos = getQuestions(0,offset,size);

        String jsonString = JSONObject.toJSONString(vos);


    //    logger.error("测试一下过来没有 "+offset+" , "+size+" , "+jsonString);


        return jsonString;
    }




    public List<ViewObject> getQuestions(int userId,int offset,int limit){

        List<Question> questionList = questionService.selectLatestQuestions(userId,offset,limit);

        List<ViewObject> viewObjects = new ArrayList<>();

        for(Question question:questionList){

            ViewObject viewObject = new ViewObject();

            viewObject.set("question",question);

            viewObject.set("user",userService.getUser(question.getUserId()));
            viewObject.set("followerCount",followService.getFollowersCount(EntityType.ENTITY_QUESTION,question.getId()));

            viewObjects.add(viewObject);

        }

        return  viewObjects;

    }

    public List<ViewObject> getQuestionsByType(int userId,int offset,int limit,String classification){

        List<Question> questionList = questionService.selectLatestQuestionsByType(userId,offset,limit,classification);

        List<ViewObject> viewObjects = new ArrayList<>();

        for(Question question:questionList){

            ViewObject viewObject = new ViewObject();

            viewObject.set("question",question);

            viewObject.set("user",userService.getUser(question.getUserId()));
            viewObject.set("followerCount",followService.getFollowersCount(EntityType.ENTITY_QUESTION,question.getId()));

            viewObjects.add(viewObject);

        }

        return  viewObjects;


    }







}
