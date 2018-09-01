package com.shaojie.ZhiHu.service;

import com.shaojie.ZhiHu.util.JedisAdapter;
import com.shaojie.ZhiHu.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;


/**
 * follower有多少个粉丝
 *
 * followee相当于某个人的关注列表
 */
@Service
public class FollowService {


    @Autowired
    JedisAdapter jedisAdapter;

    private static Logger logger = LoggerFactory.getLogger(FollowService.class);


    public List<Integer> getIdsFromSet(Set<String> set){

        List<Integer> list = new ArrayList<>();

        for(String str :set){

            list.add(Integer.parseInt(str));
        }
        return list;

    }


    public List<Integer> getFollowers(int entityType, int entityId, int count) {

        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);

        return getIdsFromSet(jedisAdapter.zrevrange(followerKey, 0, count));

    }


    public List<Integer> getFollowers(int entityType, int entityId,int offset, int count) {

        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);

        return getIdsFromSet(jedisAdapter.zrevrange(followerKey, offset, count));

    }

    public List<Integer> getFollowees(int userId, int entityType, int count) {

        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);

        return getIdsFromSet(jedisAdapter.zrevrange(followeeKey, 0, count));

    }


    public List<Integer> getFollowees(int userId, int entityType,int offset, int count) {

        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);

        return getIdsFromSet(jedisAdapter.zrevrange(followeeKey, offset, count));

    }


    public long getFollowersCount(int entityType,int entityId){

        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);

        return jedisAdapter.zcard(followerKey);

    }


    public long getFolloweesCount(int userId,int entityType){

        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);

        return jedisAdapter.zcard(followeeKey);

    }


    public boolean isFollower(int userId, int entityType, int entityId) {

        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);

        return jedisAdapter.zscore(followerKey, String.valueOf(userId)) != null;


    }


    public boolean follow(int userId, int entityType, int entityId) {


        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        Date date = new Date();

        Jedis jedis = jedisAdapter.getJedis();

        Transaction tx = jedisAdapter.multi(jedis);

        tx.zadd(followerKey, date.getTime(), String.valueOf(userId));
        tx.zadd(followeeKey, date.getTime(), String.valueOf(entityId));

        List<Object> result = jedisAdapter.exec(tx, jedis);



        return result.size() == 2 && (long) result.get(0) > 0 && (long) result.get(1) > 0;
    }


    public boolean unfollow(int userId, int entityType, int entityId) {


        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);

        Jedis jedis = jedisAdapter.getJedis();

        Transaction tx = jedisAdapter.multi(jedis);

        tx.zrem(followerKey, String.valueOf(userId));
        tx.zrem(followeeKey, String.valueOf(entityId));

        List<Object> result = jedisAdapter.exec(tx, jedis);


        return result.size() == 2 && (long) result.get(0) > 0 && (long) result.get(1) > 0;
    }

}
