package com.example.training_center.dto;

import lombok.Data;

@Data
public class OtpRequest {
    private String email;
    private String otp;
}
