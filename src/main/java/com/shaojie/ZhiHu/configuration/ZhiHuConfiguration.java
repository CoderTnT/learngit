package com.shaojie.ZhiHu.configuration;


import com.shaojie.ZhiHu.Interceptor.PassportInterceptor;
import com.shaojie.ZhiHu.Interceptor.UserRequiredInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Component
public class ZhiHuConfiguration extends WebMvcConfigurerAdapter{


    @Autowired
    PassportInterceptor passportInterceptor;

    @Autowired
    UserRequiredInterceptor userRequiredInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {


        //注意第二个拦截器要加载第一个之后，因为第二个是要通过HostHolder来判断是否登录。
        //访问用户界面的时候就需要走拦截器
        registry.addInterceptor(passportInterceptor);
        registry.addInterceptor(userRequiredInterceptor).addPathPatterns("/user/*");
        super.addInterceptors(registry);
    }
}
