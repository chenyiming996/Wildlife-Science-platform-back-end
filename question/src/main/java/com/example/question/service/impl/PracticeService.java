package com.example.question.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.question.entity.Practice;
import com.example.question.mapper.PracticeMapper;
import com.example.question.service.IPracticeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PracticeService extends ServiceImpl<PracticeMapper, Practice> implements IPracticeService {
}
