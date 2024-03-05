package com.example.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.admin.entity.Admin;
import com.example.admin.mapper.AdminMapper;
import com.example.admin.service.IAdminService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class AdminService extends ServiceImpl<AdminMapper, Admin> implements IAdminService {
}
