package com.example.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.admin.common.Role;
import com.example.admin.entity.Admin;
import com.example.admin.mapper.AdminMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
public class InitService implements InitializingBean {
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private AdminMapper adminMapper;

    @Override
    public void afterPropertiesSet() throws Exception {
        // id字段必然存在，数量为0表示用户名表为空。
        // 初始化管理员用户，赋权限值
        int count = adminMapper.selectCount(new QueryWrapper<Admin>().select("id"));
        if (count == 0) {
            Admin admin = Admin.builder()
                    .nickname("admin")
                    .username("admin")
                    .password(encoder.encode("admin"))
                    .role(Role.SUPER_ADMIN)
                    .build();
            adminMapper.insert(admin);
        }
    }
}
