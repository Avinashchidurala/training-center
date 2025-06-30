package com.example.training_center.controller;

import com.example.training_center.dto.QuestionRequest;
import com.example.training_center.dto.SubmitExamRequest;
import com.example.training_center.service.ExamService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exams")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<?> createExam(@RequestParam String title,
                                        @RequestBody List<QuestionRequest> questions) {
        return ResponseEntity.ok(examService.createExam(title, questions));
    }

    @PostMapping("/submit")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> submitExam(@RequestHeader("email") String email,
                                        @RequestBody SubmitExamRequest request) {
        int score = examService.submitExam(email, request);
        return ResponseEntity.ok("Exam submitted. Score: " + score);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('STUDENT', 'TRAINER', 'ADMIN')")
    public ResponseEntity<?> getAllExams() {
        return ResponseEntity.ok(examService.getAllExams());
    }


    @GetMapping("/{examId}/questions")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> getExamQuestions(@PathVariable Long examId) {
        return ResponseEntity.ok(examService.getQuestionsByExamId(examId));
    }

}
