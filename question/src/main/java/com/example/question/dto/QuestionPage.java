package com.example.question.dto;

import com.example.question.entity.Question;
import lombok.Data;

import java.util.List;

@Data
public class QuestionPage {
    private Long total;
    private List<Question> questions;

    public QuestionPage() {
    }

    public QuestionPage(Long total, List<Question> questions) {
        this.total = total;
        this.questions = questions;
    }
}
