package com.example.AtivHub.AtivHub.repository;

import com.example.AtivHub.AtivHub.domain.user.User;
import com.example.AtivHub.AtivHub.domain.user.dto.UserRankingProjectionDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    @Query("""
            SELECT new com.example.AtivHub.AtivHub.domain.user.dto.UserRankingProjectionDTO(u, COUNT(s))
            FROM User u
            LEFT JOIN Submission s ON s.student = u
            WHERE u.role = 'ALUNO'
            GROUP BY u
            ORDER BY u.xp DESC, COUNT(s) DESC
            """)
    List<UserRankingProjectionDTO> findTopStudents(Pageable pageable);
}