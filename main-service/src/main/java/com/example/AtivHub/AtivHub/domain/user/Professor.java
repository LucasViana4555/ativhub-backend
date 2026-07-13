package com.example.AtivHub.AtivHub.domain.user;

import com.example.AtivHub.AtivHub.domain.classroom.Classroom;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity(name = "Professor")
@Table(name = "professors")
@Getter
@Setter
@NoArgsConstructor
@PrimaryKeyJoinColumn(name = "user_id")
public class Professor extends User {

    @Column(name = "school_name", nullable = false)
    private String schoolName;

    @Column(nullable = false)
    private String subject;

    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Classroom> classrooms;

    public Professor(String name, String email, String password, String schoolName, String subject) {
        super(name, email, password, Role.PROFESSOR);
        this.schoolName = schoolName;
        this.subject = subject;
    }
}