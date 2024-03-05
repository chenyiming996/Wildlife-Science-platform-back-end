package com.example.information.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.information.entity.Helpline;
import com.example.information.mapper.HelplineMapper;
import com.example.information.service.IHelplineService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class HelplineService extends ServiceImpl<HelplineMapper, Helpline> implements IHelplineService {
}
