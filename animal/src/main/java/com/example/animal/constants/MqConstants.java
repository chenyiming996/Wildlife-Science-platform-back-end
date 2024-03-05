package com.example.animal.constants;

public class MqConstants {
    /**
     * 交换机
     */
    public final static String ANIMAL_EXCHANGE = "animal.topic";
    /**
     * 监听新增和修改的队列
     */
    public final static String ANIMAL_INSERT_QUEUE = "animal.insert.queue";
    /**
     * 监听删除的队列
     */
    public final static String ANIMAL_DELETE_QUEUE = "animal.delete.queue";
    /**
     * 新增或修改的RoutingKey
     */
    public final static String ANIMAL_INSERT_KEY = "animal.insert";
    /**
     * 删除的RoutingKey
     */
    public final static String ANIMAL_DELETE_KEY = "animal.delete";

}
