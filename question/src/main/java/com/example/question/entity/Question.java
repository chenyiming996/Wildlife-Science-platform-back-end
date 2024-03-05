package com.example.question.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {
    private Long id;
    @NotNull(message = "问题描述不能为空")
    @NotEmpty(message = "问题描述不能为空")
    private String description;
    @NotNull(message = "问题答案不能为空")
    @NotEmpty(message = "问题答案不能为空")
    private String answer;
    @NotNull(message = "选项A不能为空")
    @NotEmpty(message = "选项A不能为空")
    private String a;
    @NotNull(message = "选项B不能为空")
    @NotEmpty(message = "选项B不能为空")
    private String b;
    @NotNull(message = "选项C不能为空")
    @NotEmpty(message = "选项C不能为空")
    private String c;
    @NotNull(message = "选项D不能为空")
    @NotEmpty(message = "选项D不能为空")
    private String d;
    @Min(value = 0,message = "您输入的正确次数为${validatedValue}，不能小于{value}")
    private Integer rightNum;
    @Min(value = 0,message = "您输入的总次数为${validatedValue}，不能小于{value}")
    private Integer num;
    private Double rate;
    private Integer allTime;
    private Double averageTime;
    @NotNull(message = "试题类型不能为空")
    @NotEmpty(message = "试题类型不能为空")
    private String type;

    @TableField(updateStrategy = FieldStrategy.NEVER)
    private LocalDateTime createTime;

    @TableField(updateStrategy = FieldStrategy.NEVER)
    private LocalDateTime updateTime;


}
