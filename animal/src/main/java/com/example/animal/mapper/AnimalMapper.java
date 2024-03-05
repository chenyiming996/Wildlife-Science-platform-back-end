package com.example.animal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.animal.entity.Animal;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface AnimalMapper extends BaseMapper<Animal> {
}
