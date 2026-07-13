package com.example.gamification.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    @Builder.Default
    private Integer xp = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer level = 1;

    @Column(name = "submissions_count", nullable = false)
    @Builder.Default
    private Integer submissionsCount = 0;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "student_classrooms",
        joinColumns = @JoinColumn(name = "student_id"),
        inverseJoinColumns = @JoinColumn(name = "classroom_id")
    )
    @Builder.Default
    private List<Classroom> classrooms = new ArrayList<>();
}
