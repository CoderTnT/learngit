package com.shaojie.ZhiHu.asycn;

import com.alibaba.fastjson.JSONObject;
import com.shaojie.ZhiHu.util.JedisAdapter;
import com.shaojie.ZhiHu.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventProducer {

    @Autowired
    JedisAdapter jedisAdapter;

//发布时间，将时间推进队列
    public boolean fireEvent(EventModel eventModel){

        try{

            String json = JSONObject.toJSONString(eventModel);

            String key = RedisKeyUtil.getEventQueueKey();

            jedisAdapter.lpush(key,json);


            return true;
        }catch (Exception e){


            return false;
        }

    }


}
