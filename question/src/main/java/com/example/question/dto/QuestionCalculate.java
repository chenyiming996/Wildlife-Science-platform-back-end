package com.example.question.dto;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.example.question.entity.Question;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class QuestionCalculate {
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
    private Integer allTime;
    private Double averageTime;
    private String type;
    private Double matchValue;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public QuestionCalculate(Question question){
        this.id= question.getId();
        this.description= question.getDescription();
        this.a= question.getA();
        this.b= question.getB();
        this.c= question.getC();
        this.d= question.getD();
        this.answer= question.getAnswer();
        this.rightNum= question.getRightNum();
        this.num= question.getNum();
        this.rate= question.getRate();
        this.allTime= question.getAllTime();
        this.averageTime= question.getAverageTime();
        this.type= question.getType();
        this.createTime= question.getCreateTime();
        this.updateTime= question.getUpdateTime();
    }
}
