package com.shaojie.ZhiHu.asycn;

import java.util.List;

public interface EventHandler {

    void doHandle(EventModel eventModel);

    //登记一下自己可以处理哪些Event
    List<EventType> getSupportEventTypes();

}
