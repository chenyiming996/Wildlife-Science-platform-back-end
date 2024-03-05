package com.example.animal.dto;

import lombok.Data;

@Data
public class RequestParams {
    //关键字
    private String key;
    //分页
    private Integer page;
    //过滤条件
    private String type;
    private String tag;
}
