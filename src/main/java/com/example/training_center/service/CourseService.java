package com.example.training_center.service;

import com.example.training_center.model.*;
import com.example.training_center.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepo;
    private final BatchRepository batchRepo;
    private final EnrollmentRepository enrollmentRepo;

    public Course createCourse(String title, String description) {
        // Avoid duplicate titles
        courseRepo.findByTitle(title).ifPresent(c -> {
            throw new RuntimeException("Course with title already exists");
        });

        Course course = Course.builder()
                .title(title)
                .description(description)
                .build();

        return courseRepo.save(course);
    }

    public Batch createBatch(Long courseId, String trainerName,
                             LocalDate startDate, LocalDate endDate, int maxStudents) {

        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        Batch batch = Batch.builder()
                .trainerName(trainerName)
                .startDate(startDate)
                .endDate(endDate)
                .maxStudents(maxStudents)
                .course(course)
                .build();

        return batchRepo.save(batch);
    }

    public String enrollStudent(String email, Long batchId) {
        Batch batch = batchRepo.findById(batchId)
                .orElseThrow(() -> new RuntimeException("Batch not found"));

        // Already enrolled?
        enrollmentRepo.findByStudentEmailAndBatchId(email, batchId)
                .ifPresent(e -> {
                    throw new RuntimeException("Student already enrolled in this batch");
                });

        // Check capacity
        long enrolledCount = enrollmentRepo.countByBatchId(batchId);
        if (enrolledCount >= batch.getMaxStudents()) {
            throw new RuntimeException("Batch is full");
        }

        Enrollment enrollment = Enrollment.builder()
                .studentEmail(email)
                .batch(batch)
                .build();

        enrollmentRepo.save(enrollment);
        return "Enrollment successful for batch ID " + batchId;
    }

    public List<Course> getAllCourses() {
        return courseRepo.findAll();
    }

    public List<Batch> getBatchesByCourse(Long courseId) {
        return batchRepo.findByCourseId(courseId);
    }

    public List<Enrollment> getEnrollmentsByStudent(String email) {
        return enrollmentRepo.findByStudentEmail(email);
    }
}

