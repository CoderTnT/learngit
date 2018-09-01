package com.shaojie.ZhiHu.service;


import com.shaojie.ZhiHu.dao.LoginTicketDAO;
import com.shaojie.ZhiHu.dao.UserDAO;
import com.shaojie.ZhiHu.model.LoginTicket;
import com.shaojie.ZhiHu.model.User;
import com.shaojie.ZhiHu.util.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.util.*;

@Service
public class UserService {

    @Autowired
    UserDAO userDAO;

    @Autowired
    LoginTicketDAO loginTicketDAO;


    public User selectUserByUserName(String name){

        return userDAO.selectByUsername(name);


    }


//注册功能，返回值是Map方便返回错误信息，如果直接返回空说明，注册成功
    public Map<String,String> register(String username,String password){

        Map<String,String> map = new HashMap<>();


        //首先判断注册时候的用户名和密码是否符合规范，这里只是简单的判断不为空，如果有时间进一步改进
        if(StringUtils.isEmptyOrWhitespace(username)){

            map.put("msg","用户名不能为空");
            return map;
        }
        if(StringUtils.isEmptyOrWhitespace(password)){
            map.put("msg","密码不能为空");
            return map;
        }
        //这里从数据库里面查询用户明是否被注册了
        User user = userDAO.selectByUsername(username);

        if(user!=null){

            map.put("msg","用户名已经存在");
            return map;
        }
        //如果没被注册那么就添加该用户，用uuid生成随机的烟，并且截取5个字符，再用MD5工具加密
        //再生成随机的头像
        user = new User();
        user.setName(username);
        user.setSalt(UUID.randomUUID().toString().substring(0,5));
        user.setPassword(WendaUtil.MD5(password+user.getSalt()));
        user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));

        userDAO.addUser(user);

        String ticket = addLoginTicket(user.getId());

        map.put("ticket",ticket);

        return map;

    }

//登录
    public Map<String,String> login(String username,String password){

        Map<String,String> map = new HashMap<>();

        //首先也是判断这个用户密码不为空
        if(StringUtils.isEmptyOrWhitespace(username)){

            map.put("msg","用户名不能为空");
            return map;
        }
        if(StringUtils.isEmptyOrWhitespace(password)){
            map.put("msg","密码不能为空");
            return map;
        }

        User user = userDAO.selectByUsername(username);

        if(user ==null){

            map.put("msg","用户不存在");
            return map;
        }
        //通过用户输入的密码和salt来判断 数据库存的是否一样
        if(!WendaUtil.MD5(password+user.getSalt()).equals(user.getPassword())){

            map.put("msg","密码错误");
            return map;
        }

        String ticket = addLoginTicket(user.getId());

        //下发ticket
        map.put("ticket",ticket);

        return map;

    }
//添加ticket
    public String addLoginTicket(int userId){

        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(userId);
        loginTicket.setStatus(0);
        Date now = new Date();
        now.setTime(3600*24*1000+now.getTime());
        loginTicket.setExpired(now);
        loginTicket.setTicket(UUID.randomUUID().toString().replaceAll("-",""));
        loginTicketDAO.addLoginTicket(loginTicket);

        return loginTicket.getTicket();

    }



    public void logout(String ticket){

        loginTicketDAO.updateStatus(ticket,1);


    }





    public User getUser(int id){

        return userDAO.selectById(id);

    }
}
