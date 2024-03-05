package com.example.animal.listener;

import com.example.animal.constants.MqConstants;
import com.example.animal.service.IAnimalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AnimalListener {

    @Autowired
    private IAnimalService animalService;

    /**
     * 监听动物新增或修改业务
     * @param id 动物id
     */
    @RabbitListener(queues = MqConstants.ANIMAL_INSERT_QUEUE)
    public void listenHotelInsertOrUpdate(Long id){
        log.debug("{}",id);
        animalService.insertById(id);
    }

    /**
     * 监听动物删除业务
     * @param id 动物id
     */
    @RabbitListener(queues = MqConstants.ANIMAL_DELETE_QUEUE)
    public void listenHotelDelete(Long id){
        log.debug("{}",id);
        animalService.deleteById(id);
    }
}
