package com.example.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.user.dto.UserPage;
import com.example.user.entity.User;
import com.example.user.service.IUserService;
import com.example.user.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminUserController {
    @Autowired
    private IUserService userService;

    @GetMapping("/user/search/{search}/list/{page}")
    @Cacheable(value = "userPage",key = "#search+'-'+#page")
    public ResultVO getUserList(
            @PathVariable("search") String search,
            @PathVariable("page") String page
    ){
        int size=10;
        QueryWrapper<User> qw=new QueryWrapper<>();
        if(!search.equals("all")){
            qw.like("nick_name",search);
        }
        Page<User> result = userService.page(new Page<>(Integer.valueOf(page),size),qw);
        UserPage userPage = new UserPage(result.getTotal(), result.getRecords());
        return ResultVO.success(Map.of("userPage",userPage));
    }

    @DeleteMapping("/user/{uid}")
    @CacheEvict(value = "userPage",allEntries = true)
    public ResultVO deleteUser(@PathVariable("uid") String uid){
        userService.removeById(Long.valueOf(uid));
        return ResultVO.success(Map.of());
    }
}
