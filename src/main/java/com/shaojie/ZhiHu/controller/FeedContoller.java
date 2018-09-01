package com.shaojie.ZhiHu.controller;


import com.shaojie.ZhiHu.model.EntityType;
import com.shaojie.ZhiHu.model.Feed;
import com.shaojie.ZhiHu.model.HostHolder;
import com.shaojie.ZhiHu.service.FeedService;
import com.shaojie.ZhiHu.service.FollowService;
import com.shaojie.ZhiHu.util.JedisAdapter;
import com.shaojie.ZhiHu.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;

@Controller
public class FeedContoller {


    @Autowired
    FeedService feedService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    FollowService followService;

    @Autowired
    JedisAdapter jedisAdapter;


    @RequestMapping(path = {"/pullfeeds"}, method = {RequestMethod.POST, RequestMethod.GET})
    public String getPullFeeds(Model model) {


        //登录就获取Id,否则为22
        int localUserId = hostHolder.getUser() == null ?  22 : hostHolder.getUser().getId();

        List<Integer> followees = new ArrayList<>();


        if (localUserId != 22) {
            //获取当前用户关注的所有人的ID
            followees = followService.getFollowees(localUserId, EntityType.ENTITY_USER, Integer.MAX_VALUE);

        }

        List<Feed> feeds = feedService.getUserFeeds(Integer.MAX_VALUE, followees, 10);

        model.addAttribute("feeds", feeds);

        return "feeds";
    }

    //推模式
    @RequestMapping(path = {"/pushfeeds"}, method = {RequestMethod.POST, RequestMethod.GET})
    public String getpushFeeds(Model model) {

        //登录就获取Id,否则为0
        int localUserId = hostHolder.getUser() == null ? 22 : hostHolder.getUser().getId();

        List<String> feedIds = jedisAdapter.lrang(RedisKeyUtil.getPushFeedKey(localUserId), 0, 10);

        List<Feed> feeds = new ArrayList<>();

        for (String feedId : feedIds) {

            Feed feed = feedService.getFeedById(Integer.parseInt(feedId));
            if (feed == null) {
                continue;
            }

            feeds.add(feed);
        }


        model.addAttribute("feeds", feeds);

        return "feeds";
    }




}
