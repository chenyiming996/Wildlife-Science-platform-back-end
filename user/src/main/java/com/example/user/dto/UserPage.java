package com.example.user.dto;

import com.example.user.entity.User;
import lombok.Data;

import java.util.List;

@Data
public class UserPage {
    private Long total;
    private List<User> users;

    public UserPage() {
    }

    public UserPage(Long total, List<User> users) {
        this.total = total;
        this.users = users;
    }
}
