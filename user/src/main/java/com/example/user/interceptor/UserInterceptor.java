package com.example.user.interceptor;

import com.example.user.common.MyException;
import com.example.user.common.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class UserInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!String.valueOf(request.getAttribute("role")).equals(Role.USER)) {
            throw new MyException(403, "无权限");
        }
        return true;
    }
}
