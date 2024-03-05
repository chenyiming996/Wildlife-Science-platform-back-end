package com.example.animal.dto;

import com.example.animal.entity.Animal;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AnimalDoc {
    private Long id;
    private String name;
    private String type;
    private String description;
    private String area;
    private String url;
    private String tag;

    public AnimalDoc(Animal animal){
        this.id=animal.getId();
        this.name=animal.getName();
        this.type=animal.getType();
        this.description=animal.getDescription();
        this.area=animal.getArea();
        this.url=animal.getUrl();
        this.tag=animal.getTag();
    }

}
