package com.example.training_center.repository;

import com.example.training_center.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByStudentEmail(String email); // Show all batches enrolled by a student
    Optional<Enrollment> findByStudentEmailAndBatchId(String email, Long batchId); // Prevent duplicate enrollments
    Long countByBatchId(Long batchId); // Count how many students joined a batch (for seat limit check)
}
