package com.example.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.user.common.EncryptComponent;
import com.example.user.common.MyException;
import com.example.user.common.Role;
import com.example.user.entity.User;
import com.example.user.service.IUserService;
import com.example.user.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping("/api/user/")
public class LoginController {
    @Autowired
    private IUserService userService;

    @Autowired
    private EncryptComponent encryptComponent;

    @PostMapping("login")
    @CacheEvict(value = "userPage",allEntries = true)
    public ResultVO Login(@RequestBody User user, HttpServletResponse response){
        LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<>();
        qw.eq(User::getOpenid,user.getOpenid());
        User u = userService.getOne(qw);
        if(u==null){
            user.setRole(Role.USER);
            boolean save = userService.save(user);
            if(save){
                String token = encryptComponent.encrypt(Map.of("id", user.getId(), "role", user.getRole()));
                response.addHeader("token", token);
                return ResultVO.success(Map.of("user",userService.getById(user.getId())));
            }
            else{
                throw new MyException(500,"服务器异常");
            }
        }
        else{
            if(u.getNickName().equals(user.getNickName())&&u.getAvatarUrl().equals(user.getAvatarUrl())){
                String token = encryptComponent.encrypt(Map.of("id", u.getId(), "role", u.getRole()));
                response.addHeader("token", token);
                return ResultVO.success(Map.of("user",u));
            }
            else{
                UpdateWrapper<User> uw = new UpdateWrapper<>();
                uw.eq("id",u.getId());
                uw.set("nick_name",user.getNickName());
                uw.set("avatar_url",user.getAvatarUrl());
                boolean save = userService.update(uw);
                if(save){
                    String token = encryptComponent.encrypt(Map.of("id", u.getId(), "role", u.getRole()));
                    response.addHeader("token", token);
                    return ResultVO.success(Map.of("user",userService.getById(u.getId())));
                }
                else{
                    throw new MyException(500,"服务器异常");
                }
            }
        }
    }

}
