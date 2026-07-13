package com.example.gamification.repository;

import com.example.gamification.domain.Student;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentRepository extends JpaRepository<Student, UUID> {

    @Query("SELECT s FROM Student s ORDER BY s.xp DESC, s.submissionsCount DESC")
    List<Student> findTopStudents(Pageable pageable);

    @Query("SELECT s FROM Student s JOIN s.classrooms c WHERE c.id = :classroomId ORDER BY s.xp DESC, s.submissionsCount DESC")
    List<Student> findTopStudentsByClassroom(@Param("classroomId") UUID classroomId, Pageable pageable);
}
