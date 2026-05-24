package com.example.AtivHub.AtivHub.repository;

import com.example.AtivHub.AtivHub.domain.activity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, UUID> {
    List<Submission> findAllByActivityId(UUID activityId);
    List<Submission> findAllByStudentId(UUID studentId);
    Optional<Submission> findByActivityIdAndStudentId(UUID activityId, UUID studentId);
}