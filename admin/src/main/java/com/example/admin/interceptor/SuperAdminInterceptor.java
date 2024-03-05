package com.example.admin.interceptor;

import com.example.admin.common.MyException;
import com.example.admin.common.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class SuperAdminInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!String.valueOf(request.getAttribute("role")).equals(Role.SUPER_ADMIN)) {
            throw new MyException(403, "无权限");
        }
        return true;
    }
}
