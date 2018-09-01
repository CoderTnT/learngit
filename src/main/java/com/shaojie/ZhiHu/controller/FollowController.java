package com.shaojie.ZhiHu.controller;

import com.shaojie.ZhiHu.asycn.EventModel;
import com.shaojie.ZhiHu.asycn.EventProducer;
import com.shaojie.ZhiHu.asycn.EventType;
import com.shaojie.ZhiHu.model.*;
import com.shaojie.ZhiHu.service.FollowService;
import com.shaojie.ZhiHu.service.QuestionService;
import com.shaojie.ZhiHu.service.UserService;
import com.shaojie.ZhiHu.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.View;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class FollowController {


    @Autowired
    QuestionService questionService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    FollowService followService;

    @Autowired
    UserService userService;

    @Autowired
    EventProducer eventProducer;


    private static Logger logger = LoggerFactory.getLogger(FollowController.class);

    @RequestMapping(path = {"/followUser"}, method = RequestMethod.POST)
    @ResponseBody
    public String follow(@RequestParam("userId") int userId) {



        if (hostHolder.getUser() == null) {

            return WendaUtil.getJSONString(999);
        }

        Boolean result = followService.follow(hostHolder.getUser().getId(), EntityType.ENTITY_USER, userId);
        eventProducer.fireEvent(new EventModel(EventType.FOLLOW)
                .setActorId(hostHolder.getUser().getId())
                .setEntityType(EntityType.ENTITY_USER)
                .setEntityId(userId)
                .setEntityOwnerId(userId));

        return WendaUtil.getJSONString(result ? 0 : 1,
                String.valueOf(followService.getFolloweesCount(hostHolder.getUser().getId(),
                EntityType.ENTITY_USER)));
    }



    @RequestMapping(path = {"/unfollowUser"}, method = RequestMethod.POST)
    @ResponseBody
    public String unfollow(@RequestParam("userId") int userId) {

        if (hostHolder.getUser() == null) {

            return WendaUtil.getJSONString(999);
        }

        Boolean result = followService.unfollow(hostHolder.getUser().getId(), EntityType.ENTITY_USER, userId);
        eventProducer.fireEvent(new EventModel(EventType.UNFOLLOW)
                .setActorId(hostHolder.getUser().getId())
                .setEntityType(EntityType.ENTITY_USER)
                .setEntityId(userId)
                .setEntityOwnerId(userId));

        return WendaUtil.getJSONString(result ? 0 : 1,
                String.valueOf(followService.getFolloweesCount(hostHolder.getUser().getId(),
                EntityType.ENTITY_USER)));
    }


    //-----------------------------------华丽的分割线-----------------------

    @RequestMapping(path = {"/followQuestion"}, method = RequestMethod.POST)
    @ResponseBody
    public String followQuestion(@RequestParam("questionId") int questionId) {

        if (hostHolder.getUser() == null) {

            return WendaUtil.getJSONString(999);
        }


        Question q = questionService.selectQuestionById(questionId);

        if(q==null){


            return WendaUtil.getJSONString(1,"问题不存在");
        }


        Boolean result = followService.follow(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION, questionId);

        eventProducer.fireEvent(new EventModel(EventType.FOLLOW)
                .setActorId(hostHolder.getUser().getId())
                .setEntityType(EntityType.ENTITY_QUESTION)
                .setEntityId(questionId)
                .setEntityOwnerId(q.getUserId()));

        Map<String,Object> map = new HashMap<>();

        map.put("headUrl",hostHolder.getUser().getHeadUrl());
        map.put("name",hostHolder.getUser().getName());
        map.put("id",hostHolder.getUser().getId());
        map.put("count",followService.getFollowersCount(EntityType.ENTITY_QUESTION,questionId));


        return WendaUtil.getJSONString(result ? 0 : 1,map);
    }


    @RequestMapping(path = {"/unfollowQuestion"}, method = RequestMethod.POST)
    @ResponseBody
    public String unfollowQuestion(@RequestParam("questionId") int questionId) {

        if (hostHolder.getUser() == null) {

            return WendaUtil.getJSONString(999);
        }


        Question q = questionService.selectQuestionById(questionId);

        if (q == null) {

            return WendaUtil.getJSONString(1, "问题不存在");
        }

        Boolean result = followService.unfollow(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION, questionId);
        eventProducer.fireEvent(new EventModel(EventType.UNFOLLOW)
                .setActorId(hostHolder.getUser().getId())
                .setEntityType(EntityType.ENTITY_QUESTION)
                .setEntityId(questionId)
                .setEntityOwnerId(q.getUserId()));

        Map<String, Object> map = new HashMap<>();

        map.put("headUrl", hostHolder.getUser().getHeadUrl());
        map.put("name", hostHolder.getUser().getName());
        map.put("id", hostHolder.getUser().getId());
        map.put("count", followService.getFollowersCount(EntityType.ENTITY_QUESTION, questionId));

        return WendaUtil.getJSONString(result ? 0 : 1, map);
    }

    //-------华丽的分割线----------某个人的关注列表



    @RequestMapping(path={"/user/{uid}/followees"},method =RequestMethod.GET)
    public String followee(@PathVariable("uid") int uid,Model model){

        List<Integer> lists = followService.getFollowees(uid, EntityType.ENTITY_USER,0,10);

        User user = hostHolder.getUser();

        if(user==null){

        model.addAttribute("viewObject",getUserInfo(22,lists));

        }else{

        model.addAttribute("viewObject",getUserInfo(user.getId(),lists));

        }

        model.addAttribute("followeeCount",followService.getFolloweesCount(uid,EntityType.ENTITY_USER));
        model.addAttribute("curUser",userService.getUser(uid));

        return "followees";
    }

    @RequestMapping(path={"/user/{uid}/followers"},method =RequestMethod.GET)
    public String follower(@PathVariable("uid") int uid,Model model){

        List<Integer> lists = followService.getFollowers(EntityType.ENTITY_USER, uid,0,10);

        User user = hostHolder.getUser();

        if(user==null){

            model.addAttribute("viewObject",getUserInfo(22,lists));

        }else{
            model.addAttribute("viewObject",getUserInfo(user.getId(),lists));
        }

        model.addAttribute("followerCount",followService.getFollowersCount(EntityType.ENTITY_USER,uid));
        model.addAttribute("curUser",userService.getUser(uid));

        return "followers";
    }


    //-------------


    @RequestMapping(path={"/user/{uid}/followeeQuestion"},method=RequestMethod.GET)
    public String followeeQuestion(@PathVariable("uid") int uid,Model model){

        List<Integer> questionIds = followService.getFollowees(uid,EntityType.ENTITY_QUESTION,0,10);

        model.addAttribute("viewObjects",getQuestionInfo(questionIds));

        //  followService.follow(22, EntityType.ENTITY_USER,41);

        return "index";
    }




    public List<ViewObject> getQuestionInfo(List<Integer> questionIds){

        List<ViewObject> viewObjects = new ArrayList<>();

        for(Integer questionId :questionIds){

            ViewObject viewObject = new ViewObject();

            Question question = questionService.selectQuestionById(questionId);

            if (question == null){
                continue;
            }

            viewObject.set("question",question);

            viewObject.set("user",userService.getUser(question.getUserId()));
            viewObject.set("followerCount",followService.getFollowersCount(EntityType.ENTITY_QUESTION,question.getId()));
            viewObjects.add(viewObject);

        }

        return viewObjects;
    }


    public List<ViewObject> getUserInfo(int localUserId, List<Integer> uids) {

        List<ViewObject> viewObjects = new ArrayList<>();

        for (Integer uid : uids) {

            ViewObject vo = new ViewObject();

            User user = userService.getUser(uid);
            if (user == null) {
                continue;
            }

            vo.set("user", user);
            vo.set("followeeCount", followService.getFolloweesCount(uid, EntityType.ENTITY_USER));
            vo.set("followerCount", followService.getFollowersCount(EntityType.ENTITY_USER, uid));

            if (localUserId != 22) {

                vo.set("followed", followService.isFollower(localUserId, EntityType.ENTITY_USER, uid));

            } else {
                vo.set("followed", false);

            }

            viewObjects.add(vo);
        }

        return viewObjects;
    }


}
