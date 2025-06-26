package com.book.conf;

import com.book.vo.UserInfo;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class MyCustomInterceptor implements HandlerInterceptor {
    private ThreadLocal<String> currentUser = new ThreadLocal<>();
    @Resource
    RedisTemplate redisTemplate;
    // 获取当前登录用户信息
    public String getUserId() {
        return currentUser.get();
    }

    //unimplemented methods comes here. Define the following method so that it
    //will handle the request before it is passed to the controller.
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从请求头中获取 token
        String token = request.getParameter("token");
        if (token != null) {
            // 从 token 中解析出用户信息
            String userid = (String)redisTemplate.opsForValue().get(token);
            // 将用户信息存储到 ThreadLocal 变量中
            currentUser.set(userid);
        } else {
//            throw new Exception("Missing or invalid token");
        }
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清除 ThreadLocal 变量,防止内存泄漏
        currentUser.remove();
    }
}
