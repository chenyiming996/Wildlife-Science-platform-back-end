package com.example.information.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.information.entity.News;
import com.example.information.mapper.NewsMapper;
import com.example.information.service.INewsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class NewsService extends ServiceImpl<NewsMapper, News> implements INewsService {
}
