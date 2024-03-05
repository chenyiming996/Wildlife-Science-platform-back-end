package com.example.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.admin.common.MyException;
import com.example.admin.common.Role;
import com.example.admin.dto.AdminPage;
import com.example.admin.entity.Admin;
import com.example.admin.service.IAdminService;
import com.example.admin.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/")
@Slf4j
public class SuperAdminController {
    @Autowired
    private IAdminService adminService;

    @Autowired
    private PasswordEncoder encoder;

    @GetMapping("super-admin/admin/search/{search}/list/{page}")
    @Cacheable(value = "adminPage",key = "#search+'-'+#page")
    public ResultVO getAdminsList(
            @PathVariable("search") String search,
            @PathVariable("page") String page
    ){
        int size=10;
        LambdaQueryWrapper<Admin> qw = new LambdaQueryWrapper<>();
        qw.eq(Admin::getRole, Role.ADMIN);
        if(!search.equals("all")){
            qw.like(Admin::getNickname,search);
        }
        Page<Admin> result = adminService.page(new Page<>(Integer.valueOf(page),size),qw);
        AdminPage adminPage = new AdminPage(result.getTotal(), result.getRecords());
        return ResultVO.success(Map.of("adminPage",adminPage));

    }

    @PostMapping("super-admin/admin")
    @CacheEvict(value = "adminPage",allEntries = true)
    public ResultVO addAdmin(@Valid @RequestBody Admin admin){
        QueryWrapper<Admin> qw = new QueryWrapper<>();
        qw.eq("username",admin.getUsername());
        Admin one = adminService.getOne(qw);
        if(one!=null){
            throw new MyException(400,"该管理员账号已存在");
        }
        Admin a = Admin.builder()
                .nickname(admin.getNickname())
                .username(admin.getUsername())
                .password(encoder.encode(admin.getPassword()))
                .role(Role.ADMIN)
                .build();
        adminService.save(a);
        return ResultVO.success(Map.of());

    }

    @DeleteMapping("super-admin/admin/{aid}")
    @CacheEvict(value = "adminPage",allEntries = true)
    public ResultVO deleteAdmin(@PathVariable("aid") String aid){
        Long id = Long.valueOf(aid);
        adminService.removeById(id);
        return ResultVO.success(Map.of());
    }

    @PutMapping("super-admin/admin/{aid}")
    @CacheEvict(value = "adminPage",allEntries = true)
    public ResultVO updateAdmin(@PathVariable("aid") String aid,@RequestBody Admin admin){
        admin.setId(Long.valueOf(aid));
        if(admin.getNickname().trim().equals("")){
            throw new MyException(400,"管理员名不能为空");
        }
        adminService.updateById(admin);
        return ResultVO.success(Map.of());
    }

    @PutMapping("super-admin/admin/{aid}/password")
    public ResultVO updateAdmin(@PathVariable("aid") String aid){
        Admin admin = adminService.getById(Long.valueOf(aid));
        admin.setPassword(encoder.encode(admin.getUsername()));
        log.debug("{}",admin.getUsername());
        adminService.updateById(admin);
        return ResultVO.success(Map.of());
    }



}
