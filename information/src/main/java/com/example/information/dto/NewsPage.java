package com.example.information.dto;

import com.example.information.entity.News;
import lombok.Data;


import java.util.List;

@Data
public class NewsPage {
    private Long total;
    private List<News> news;

    public NewsPage() {
    }

    public NewsPage(Long total, List<News> news) {
        this.total = total;
        this.news = news;
    }
}
