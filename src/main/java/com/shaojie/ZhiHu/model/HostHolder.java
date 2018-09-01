package com.shaojie.ZhiHu.model;


import org.springframework.stereotype.Component;


/**
 * 一个全局的HostHolder 维持一个ThreadLocal 每个线程都有自己对应的一个副本
 */
@Component
public class HostHolder {

    private static ThreadLocal<User> users = new ThreadLocal<User>();

    public User getUser(){
        return users.get();
    }

    public void setUser(User user){
        users.set(user);

    }

    public void remove(){
        users.remove();
    }


}
