package com.example.training_center.repository;

import com.example.training_center.model.StudentExam;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StudentExamRepository extends JpaRepository<StudentExam, Long> {
    Optional<StudentExam> findByStudentEmailAndExamId(String studentEmail, Long examId);
}
