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
import javax.xml.ws.Response;
import java.util.Date;

@Component
public class UserRequiredInterceptor implements HandlerInterceptor{


    @Autowired
    HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {


//            如果没有登录则跳转到登录页面，同时记录他现在想要访问的页面

        /*

         */
            if(hostHolder.getUser()==null){


                httpServletResponse.sendRedirect("/reglogin?next="+httpServletRequest.getRequestURL());
            }

            return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {


    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {



    }


}
