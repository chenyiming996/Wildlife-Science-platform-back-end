package com.example.question.entity;

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
public class Practice {
    private Long id;
    private Long userId;
    private Long questionId;
    private Boolean answer;
    private String type;

    @TableField(updateStrategy = FieldStrategy.NEVER)
    private LocalDateTime createTime;

}
