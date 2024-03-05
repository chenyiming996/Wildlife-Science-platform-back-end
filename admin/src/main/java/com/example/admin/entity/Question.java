package com.example.admin.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {
    private Long id;
    private String description;
    private String answer;
    private String a;
    private String b;
    private String c;
    private String d;
    private Integer rightNum;
    private Integer num;
    private Double rate;
    private String type;
    private Integer version;
}
