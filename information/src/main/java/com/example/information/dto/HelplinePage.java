package com.example.information.dto;

import com.example.information.entity.Helpline;
import lombok.Data;

import java.util.List;

@Data
public class HelplinePage {
    private Long total;
    private List<Helpline> helplines;

    public HelplinePage() {
    }

    public HelplinePage(Long total, List<Helpline> helplines) {
        this.total = total;
        this.helplines = helplines;
    }
}
