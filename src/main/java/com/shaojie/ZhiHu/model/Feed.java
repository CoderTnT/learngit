package com.shaojie.ZhiHu.model;

import com.alibaba.fastjson.JSONObject;

import java.util.Date;

//新鲜事
public class Feed {
    //每个新鲜事肯定要有个id
    private int id;
    //什么类型的新鲜事，比如点赞，或者评论
    private int type;
    //谁触发的这件新鲜事
    private int userId;
    //什么时候
    private Date createdDate;
    //JSON格式来存储的，比如谁给某个评论点了个赞，或者谁评论了xx某个问题
    private String data;


    private String userHead;
    private String questionId;
    private String questionName;
    private String userName;


    public String getUserHead() {
        return jsonData.getString("userHead");
    }

    public String getQuestionId() {
        return jsonData.getString("questionId");
    }

    public String getQuestionName() {
        return jsonData.getString("questionName");
    }

    public String getUserName() {
        return jsonData.getString("userName");
    }

    private JSONObject jsonData = null;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
        jsonData =JSONObject.parseObject(data);
    }


    //thymeleaf好像不支持该方法
    public String get(String key){

        return jsonData == null? null:jsonData.getString(key);

    }



}
