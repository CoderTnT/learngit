package com.shaojie.ZhiHu.Interceptor;


import com.shaojie.ZhiHu.dao.LoginTicketDAO;
import com.shaojie.ZhiHu.model.HostHolder;
import com.shaojie.ZhiHu.model.LoginTicket;
import com.shaojie.ZhiHu.model.User;
import com.shaojie.ZhiHu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class PassportInterceptor implements HandlerInterceptor{


    @Autowired
    LoginTicketDAO loginTicketDAO;

    @Autowired
    UserService userService;

    @Autowired
    HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
                    String ticket = null;

                    //从服务器请求里面寻找cookie，遍历cookie找到其中的ticket
                    if(httpServletRequest.getCookies()!=null) {
                        for (Cookie cookie : httpServletRequest.getCookies()) {

                            if (cookie.getName().equals("ticket")) {
                              ticket = cookie.getValue();
                              break;
                            }
                        }
                    }

                    if (ticket!=null){
                        LoginTicket loginTicket = loginTicketDAO.selectLoginTicket(ticket);

                        //这里需要注意的就是logeinTicket.getExpired().before是在什么日子之前，这样判断是否过期
                        if(loginTicket==null||loginTicket.getExpired().before(new Date())||loginTicket.getStatus()!=0){
                            //返回true代表着没有登录
                            return true;

                        }

                        //添加到全局变量hostHolder里面去
                        User user = userService.getUser(loginTicketDAO.selectLoginTicket(ticket).getUserId());
                        hostHolder.setUser(user);

                    }



            return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {


        //在模板之前导入user，这样就可以直接访问了。
            if(modelAndView!=null){

                modelAndView.addObject("user",hostHolder.getUser());

            }


    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

   //渲染完清除掉
        hostHolder.remove();

    }


}
