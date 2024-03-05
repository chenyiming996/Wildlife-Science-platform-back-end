package com.example.animal.controller;

import cn.hutool.db.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.animal.dto.AnimalDocPage;
import com.example.animal.dto.AnimalPage;
import com.example.animal.dto.RequestParams;
import com.example.animal.entity.Animal;
import com.example.animal.service.IAnimalService;
import com.example.animal.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user/animal")
public class UserAnimalController {
    @Autowired
    private IAnimalService animalService;

    @PostMapping("/list")
    public ResultVO search(@RequestBody RequestParams params) {
        AnimalDocPage animalDocPage = animalService.search(params);
        return ResultVO.success(Map.of("animalPage",animalDocPage));
    }

    @PostMapping("/filters")
    public ResultVO getFilters(@RequestBody RequestParams params){
        Map<String, List<String>> filters = animalService.filters(params);
        return ResultVO.success(Map.of("filters",filters));
    }


}
