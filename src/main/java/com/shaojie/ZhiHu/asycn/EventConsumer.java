package com.shaojie.ZhiHu.asycn;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shaojie.ZhiHu.controller.CommentController;
import com.shaojie.ZhiHu.util.JedisAdapter;
import com.shaojie.ZhiHu.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//第一手接触到Event，将Event与handler链接起来
@Service
public class EventConsumer implements InitializingBean ,ApplicationContextAware{

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    JedisAdapter jedisAdapter;


    //对应的事件类型，会有一批的EventHandler去处理，所以要将其初始化
    private Map<EventType,List<EventHandler>> config = new HashMap<>();

    //Spring上下文，可以知道目前有多少个实现了EventHandler
    private ApplicationContext applicationContext;


    @Override
    public void afterPropertiesSet() throws Exception {

        //获取所有注册的EventHandler
        Map<String,EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);


        if(beans!=null){

            //循环Map
            for(Map.Entry<String,EventHandler> bean:beans.entrySet()){
               //把每个EventHandler所支持的EventType获取出来
                List<EventType> eventTypes = bean.getValue().getSupportEventTypes();

                for(EventType eventType :eventTypes){

                    if(!config.containsKey(eventType)){

                        config.put(eventType,new ArrayList<>());

                    }
                    //添加进去
                    config.get(eventType).add(bean.getValue());
                }
            }

        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String key = RedisKeyUtil.getEventQueueKey();

                while (true) {

                    List<String> events = jedisAdapter.brpop(0, key);

                    for (String message : events) {

                        //取出来的第一个值是key，所以过滤掉
                        if (message.equals(key)) {

                            continue;
                        }

                        EventModel eventModel = JSON.parseObject(message, EventModel.class);

                        if (!config.containsKey(eventModel.getEventType())) {

                            logger.error("不能识别的事件类型");
                            continue;

                        }

                        for (EventHandler eventHandler : config.get(eventModel.getEventType())) {

                            eventHandler.doHandle(eventModel);

                        }
                    }
                }
            }
        });

        thread.start();


    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
