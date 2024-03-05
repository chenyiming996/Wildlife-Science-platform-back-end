package com.example.animal.dto;

import com.example.animal.entity.Animal;
import lombok.Data;

import java.util.List;

@Data
public class AnimalDocPage {
    private Long total;
    private List<AnimalDoc> animals;

    public AnimalDocPage() {
    }

    public AnimalDocPage(Long total, List<AnimalDoc> animals) {
        this.total = total;
        this.animals = animals;
    }
}
