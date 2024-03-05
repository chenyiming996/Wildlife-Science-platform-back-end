package com.example.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.user.entity.User;
import com.example.user.mapper.UserMapper;
import com.example.user.service.IUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService extends ServiceImpl<UserMapper, User> implements IUserService {
}
