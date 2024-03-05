package com.example.admin.interceptor;


import com.example.admin.common.EncryptComponent;
import com.example.admin.common.MyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    private EncryptComponent encryptComponent;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        if (token == null) {
            throw new MyException(401, "未登录");
        }
        Map<String, Object> result = encryptComponent.decrypt(token);
        // 拦截解密出用户真实数据后，置于request供后续使用
        request.setAttribute("id", result.get("id"));
        request.setAttribute("role", result.get("role"));
        return true;
    }
}
