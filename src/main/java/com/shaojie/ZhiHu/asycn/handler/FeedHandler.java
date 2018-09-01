package com.shaojie.ZhiHu.asycn.handler;


import com.alibaba.fastjson.JSONObject;
import com.shaojie.ZhiHu.asycn.EventHandler;
import com.shaojie.ZhiHu.asycn.EventModel;
import com.shaojie.ZhiHu.asycn.EventType;
import com.shaojie.ZhiHu.model.*;
import com.shaojie.ZhiHu.service.*;
import com.shaojie.ZhiHu.util.JedisAdapter;
import com.shaojie.ZhiHu.util.RedisKeyUtil;
import com.shaojie.ZhiHu.util.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class FeedHandler implements EventHandler {

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Autowired
    QuestionService questionService;

    @Autowired
    FeedService feedService;

    @Autowired
    JedisAdapter jedisAdapter;

    @Autowired
    FollowService followService;


    public String getStringData(EventModel eventModel){

        Map<String, String> map = new HashMap<>();

        User actor = userService.getUser(eventModel.getActorId());

        if (actor == null) {
            return null;
        }

        map.put("userId", String.valueOf(actor.getId()));
        map.put("userName", actor.getName());
        map.put("userHead", actor.getHeadUrl());

        if (eventModel.getEventType() == EventType.COMMENT||
                (eventModel.getEventType() == EventType.FOLLOW && eventModel.getEntityType()==EntityType.ENTITY_QUESTION)) {

            Question question = questionService.selectQuestionById(eventModel.getEntityId());

            if (question == null) {

                return null;
            }

            map.put("questionId", String.valueOf(question.getId()));
            map.put("questionName", question.getTitle());
            return JSONObject.toJSONString(map);
        }
        return null;
    }


    @Override
    public void doHandle(EventModel eventModel) {

        Feed feed = new Feed();
        feed.setCreatedDate(new Date());
        feed.setType(eventModel.getEventType().getValue());
        feed.setUserId(eventModel.getActorId());
        feed.setData(getStringData(eventModel));
        if(feed.getData() == null){
            //如果过去Data失败（核心数据失败则直接返回）
            return;
        }
        feedService.addFeed(feed);

        //下面是推的模式

        List<Integer> followers = followService.getFollowers(EntityType.ENTITY_USER,eventModel.getActorId(),Integer.MAX_VALUE);

        //22是系统的id，当没有登录的时候就给系统看，也就是给游客看
        followers.add(22);

        for(Integer follow: followers){

            jedisAdapter.lpush(RedisKeyUtil.getPushFeedKey(follow),String.valueOf(feed.getId()));

        }



    }


    @Override
    public List<EventType> getSupportEventTypes() {


        return Arrays.asList(new EventType[]{EventType.FOLLOW,EventType.COMMENT});
    }



}
