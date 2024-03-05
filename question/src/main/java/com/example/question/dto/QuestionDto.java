package com.example.question.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class QuestionDto {
    private String id;
    private String description;
    private String answer;
    private String A;
    private String B;
    private String C;
    private String D;
    private Integer rightNum;
    private Integer num;
    private Double rate;
    private Integer allTime;
    private Double averageTime;
    private String type;
    private Boolean flag;

}
