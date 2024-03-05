package com.example.admin.dto;

import com.example.admin.entity.Admin;
import lombok.Data;

import java.util.List;

@Data
public class AdminPage {
    private Long total;
    private List<Admin> admins;

    public AdminPage() {
    }

    public AdminPage(Long total, List<Admin> admins) {
        this.total = total;
        this.admins = admins;
    }
}
