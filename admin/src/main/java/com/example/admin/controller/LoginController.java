package com.example.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.admin.common.EncryptComponent;
import com.example.admin.entity.Admin;
import com.example.admin.service.IAdminService;
import com.example.admin.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;


@RestController
@RequestMapping("/api/")
public class LoginController {
    @Autowired
    private EncryptComponent encryptComponent;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private IAdminService adminService;


    @PostMapping("login")
    public ResultVO login(@RequestBody Admin admin, HttpServletResponse response) {
        LambdaQueryWrapper<Admin> qw = new LambdaQueryWrapper<>();
        qw.eq(Admin::getUsername,admin.getUsername());
        Admin a = adminService.getOne(qw);
        if (a == null || !encoder.matches(admin.getPassword(), a.getPassword())) {
            return ResultVO.error(401, "用户名密码错误");
        }
        String token = encryptComponent.encrypt(Map.of("id", a.getId(), "role", a.getRole()));
        response.addHeader("token", token);
        return ResultVO.success(Map.of("admin",a,"role",a.getRole()));

    }

}
