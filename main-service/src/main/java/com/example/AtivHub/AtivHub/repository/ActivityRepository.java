package com.example.AtivHub.AtivHub.repository;

import com.example.AtivHub.AtivHub.domain.activity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, UUID> {
    List<Activity> findAllByClassroomId(UUID classroomId);
    List<Activity> findAllByClassroomIdIn(List<UUID> classroomIds);
    List<Activity> findAllByProfessorId(UUID professorId);
}