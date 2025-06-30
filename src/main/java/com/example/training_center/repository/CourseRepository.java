package com.example.training_center.repository;

import com.example.training_center.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByTitle(String title); // For duplicate check or title fetch
    List<Course> findByTitleContainingIgnoreCase(String keyword); // For search bar/autocomplete
}
