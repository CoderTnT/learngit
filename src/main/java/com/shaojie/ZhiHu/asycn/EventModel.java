package com.shaojie.ZhiHu.asycn;

import java.util.HashMap;
import java.util.Map;

public class EventModel {

    private EventType eventType;
    private int actorId;
    private int entityId;
    private int entityType;
    //触发对象所属于的Id
    private int entityOwnerId;


    //发生时间可能存在各种各样的事件，将他存储在 exts里面
    private Map<String,String> exts = new HashMap<>();


    //为了方便存储map
    public EventModel setExt(String key,String value){
        exts.put(key,value);
        return this;

    }


    public EventModel(){

    }


    public EventModel(EventType eventType){

        this.eventType = eventType;

    }

    public String getExt(String key){
        return exts.get(key);
    }


    public EventType getEventType() {
        return eventType;
    }

    public EventModel setEventType(EventType eventType) {
        this.eventType = eventType;
        return this;
    }

    public int getActorId() {
        return actorId;
    }


    //方便链式调用
    public EventModel setActorId(int actorId) {
        this.actorId = actorId;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public EventModel setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public EventModel setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityOwnerId() {
        return entityOwnerId;
    }

    public EventModel setEntityOwnerId(int entityOwnerId) {
        this.entityOwnerId = entityOwnerId;
        return this;
    }

    public Map<String, String> getExts() {
        return exts;
    }

    public EventModel setExts(Map<String, String> exts) {
        this.exts = exts;
        return this;
    }
}
