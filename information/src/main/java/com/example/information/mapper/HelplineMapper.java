package com.example.information.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.information.entity.Helpline;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;


@Repository
@Mapper
public interface HelplineMapper extends BaseMapper<Helpline> {
}
