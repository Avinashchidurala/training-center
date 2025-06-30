package com.example.training_center.repository;

import com.example.training_center.model.Batch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BatchRepository extends JpaRepository<Batch, Long> {
    List<Batch> findByCourseId(Long courseId); // Fetch batches under a specific course
    List<Batch> findByTrainerName(String trainerName); // Useful for trainer dashboards
}
