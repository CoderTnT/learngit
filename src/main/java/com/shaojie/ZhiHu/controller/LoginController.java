package com.shaojie.ZhiHu.controller;


import com.shaojie.ZhiHu.asycn.EventModel;
import com.shaojie.ZhiHu.asycn.EventProducer;
import com.shaojie.ZhiHu.asycn.EventType;
import com.shaojie.ZhiHu.service.UserService;
import com.sun.deploy.net.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginController {


    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    UserService userService;

    @Autowired
    EventProducer eventProducer;

    //注册入口
    @RequestMapping(value = {"/reg/"},method = RequestMethod.POST)
    public String reg(Model model,
                      @RequestParam("username") String username,
                      @RequestParam("password") String password,
                      @RequestParam(value = "next",required = false) String next,
                      HttpServletResponse response){
        try {
            Map<String, String> map = userService.register(username, password);
            //这里只要判断ticket是否拥有，如果拥有则说明是登录成功的
            if (map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
                cookie.setPath("/");
                response.addCookie(cookie);
                if(!StringUtils.isEmpty(next)){
                    return "redirect:"+next;

                }
                return "redirect:/";

            }
            else{
                model.addAttribute("msg", map.get("msg"));
                return "login";
            }

        }
        catch (Exception e){
            logger.error("注册异常"+e.getMessage());
            return "login";
        }
    }
    //登录入口
    @RequestMapping(value = {"/login/"},method = RequestMethod.POST)
    public String login(Model model,
                        @RequestParam("password") String password,
                        @RequestParam("username") String username,
                        @RequestParam(value = "next",required = false) String next,
                        @RequestParam(value = "remeberme",defaultValue = "false") boolean remeberme,
                        HttpServletResponse response){

        try {
            Map<String, String> map = userService.login(username, password);

            if (map.containsKey("ticket")) {

                Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
                cookie.setPath("/");
                response.addCookie(cookie);

                eventProducer.fireEvent(new EventModel(EventType.LOGIN)
                        .setExt("username",username)
                .setExt("email","871090072@qq.com"));


                if(!StringUtils.isEmpty(next)){
                    return "redirect:"+next;

                }
                return "redirect:/";

            }
            else{
                model.addAttribute("msg", map.get("msg"));
                return "login";
            }


        }
        catch (Exception e){
            logger.error("登录异常"+e.getMessage());
            return "login";
        }
    }



    //跳转到登入注册页
    @RequestMapping(value = {"/reglogin"})
    public String reglogin(Model model,@RequestParam(value = "next",required = false) String next){

        model.addAttribute("next", next);
        return "login";

    }



    @RequestMapping(value={"/logout"})
    public String logout(@CookieValue("ticket") String ticket){

        userService.logout(ticket);
        return "redirect:/";
    }
}


