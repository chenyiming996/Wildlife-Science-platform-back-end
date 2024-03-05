package com.example.animal.dto;

import com.example.animal.entity.Animal;
import lombok.Data;

import java.util.List;

@Data
public class AnimalPage {
    private Long total;
    private List<Animal> animals;

    public AnimalPage() {
    }

    public AnimalPage(Long total, List<Animal> animals) {
        this.total = total;
        this.animals = animals;
    }
}
