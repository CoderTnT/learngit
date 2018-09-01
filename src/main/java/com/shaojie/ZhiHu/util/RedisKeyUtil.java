package com.shaojie.ZhiHu.util;

public class RedisKeyUtil {


    /*
        这里有分隔符
        还有业务
     */
    public static String SPLIT =":";
    public static String BIZ_LIKE ="LIKE";
    public static String BIZ_DISLIKE ="DISLIKE";
    public static String BIZ_EVENTQUEUE ="EVENT_QUEUE";

    public static String BIZ_FOLLOWER = "FOLLOWER";
    public static String BIZ_FOLLOWEE = "FOLLOWEE";


    //推送新鲜事的key
    public static String BIZ_PUSHFEED = "PUSHFEED";


    //某个用户的所有新鲜事
    public static String getPushFeedKey(int userId){

        return BIZ_PUSHFEED+SPLIT+String.valueOf(userId);
    }





    public static String getLikeKey(int entityType,int entityId){

        return BIZ_LIKE+SPLIT+String.valueOf(entityType)+SPLIT+String.valueOf(entityId);

    }



    public static String getDisLikeKey(int entityType,int entityId){

        return BIZ_DISLIKE+SPLIT+String.valueOf(entityType)+SPLIT+String.valueOf(entityId);

    }

    public static String getEventQueueKey(){

        return BIZ_EVENTQUEUE;

    }

    public static String getFollowerKey(int entityType,int entityId){

        return BIZ_FOLLOWER+SPLIT+String.valueOf(entityType)+SPLIT+String.valueOf(entityId);

    }

    public static String getFolloweeKey(int userId,int entityType){

        return BIZ_FOLLOWEE+SPLIT+String.valueOf(userId)+SPLIT+String.valueOf(entityType);

    }






}
