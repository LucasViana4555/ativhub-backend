package com.example.AtivHub.AtivHub.repository;

import com.example.AtivHub.AtivHub.domain.classroom.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClassroomRepository extends JpaRepository<Classroom, UUID> {
    Optional<Classroom> findByRoomCode(String roomCode);
    List<Classroom> findByTeacherId(UUID teacherId);
    boolean existsByRoomCode(String roomCode);
}