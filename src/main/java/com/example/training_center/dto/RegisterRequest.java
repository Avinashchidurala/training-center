package com.example.training_center.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String password;
    private String name;
    private String role; // STUDENT, ADMIN, etc.
}
