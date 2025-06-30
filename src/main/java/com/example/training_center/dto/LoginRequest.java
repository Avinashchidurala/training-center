package com.example.training_center.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
