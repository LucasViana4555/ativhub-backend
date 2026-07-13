package com.example.AtivHub.AtivHub.repository;

import com.example.AtivHub.AtivHub.domain.user.User;
import com.example.AtivHub.AtivHub.domain.user.dto.UserRankingProjectionDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    @Query("""
            SELECT new com.example.AtivHub.AtivHub.domain.user.dto.UserRankingProjectionDTO(s, COUNT(sub))
            FROM Student s
            JOIN s.classrooms c
            LEFT JOIN Submission sub ON sub.student = s
            WHERE c.id = :classroomId
            GROUP BY s
            ORDER BY s.xp DESC, COUNT(sub) DESC
            """)
    List<UserRankingProjectionDTO> findTopStudentsByClassroom(@Param("classroomId") UUID classroomId, Pageable pageable);

    @Query("""
            SELECT new com.example.AtivHub.AtivHub.domain.user.dto.UserRankingProjectionDTO(s, COUNT(sub))
            FROM Student s
            LEFT JOIN Submission sub ON sub.student = s
            GROUP BY s
            ORDER BY s.xp DESC, COUNT(sub) DESC
            """)
    List<UserRankingProjectionDTO> findTopStudents(Pageable pageable);
}