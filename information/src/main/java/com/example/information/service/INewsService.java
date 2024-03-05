package com.example.information.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.information.entity.News;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


public interface INewsService extends IService<News> {
}
