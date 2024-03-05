package com.example.question.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.question.entity.Practice;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface PracticeMapper extends BaseMapper<Practice> {
}
