package com.example.animal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.animal.dto.AnimalDoc;
import com.example.animal.dto.AnimalDocPage;
import com.example.animal.dto.AnimalPage;
import com.example.animal.dto.RequestParams;
import com.example.animal.entity.Animal;

import java.util.List;
import java.util.Map;

public interface IAnimalService extends IService<Animal> {

    AnimalDocPage search(RequestParams params);

    Map<String, List<String>> filters(RequestParams params);

    void deleteById(Long id);

    void insertById(Long id);


}
