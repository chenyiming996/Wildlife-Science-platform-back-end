package com.example.question.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.question.entity.Question;
import com.example.question.mapper.QuestionMapper;
import com.example.question.service.IQuestionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class QuestionService extends ServiceImpl<QuestionMapper, Question> implements IQuestionService {
}
