package com.shaojie.ZhiHu.service;


import com.shaojie.ZhiHu.util.JedisAdapter;
import com.shaojie.ZhiHu.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class  LikeService {


    @Autowired
    JedisAdapter jedisAdapter;




    public long getLikeCount(int entityType,int entityId){

        String key= RedisKeyUtil.getLikeKey(entityType, entityId);

        return jedisAdapter.scard(key);


    }


//喜欢的同时还要把踩给删除掉
    public long like(int userId,int entityId,int entityType){

        String key= RedisKeyUtil.getLikeKey(entityType, entityId);

        jedisAdapter.sadd(key,String.valueOf(userId));

        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);

        jedisAdapter.srem(disLikeKey,String.valueOf(userId));

        return jedisAdapter.scard(key);

    }

    public long disLike(int userId,int entityId,int entityType){

        String disLikeKey= RedisKeyUtil.getDisLikeKey(entityType, entityId);

        jedisAdapter.sadd(disLikeKey,String.valueOf(userId));

        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);

        jedisAdapter.srem(likeKey,String.valueOf(userId));

        return jedisAdapter.scard(likeKey);

    }

    public int getStatus(int userId,int entityType,int entityId){


        //如果喜欢返回一
        String key= RedisKeyUtil.getLikeKey(entityType, entityId);

        if(jedisAdapter.sismember(key,String.valueOf(userId))){
            return 1;
        }

        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);

        //如果不喜欢返回-1否则返回0
        return jedisAdapter.sismember(disLikeKey,String.valueOf(userId))?-1:0;



    }



}
