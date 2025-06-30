package com.example.training_center.dto;

import lombok.Data;
import java.util.Map;

@Data
public class SubmitExamRequest {
    private Long examId;
    private Map<Long, String> answers; // questionId → selected option
}

