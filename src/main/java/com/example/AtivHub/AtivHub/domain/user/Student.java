package com.example.AtivHub.AtivHub.domain.user;

import com.example.AtivHub.AtivHub.domain.classroom.Classroom;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "Student")
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
@PrimaryKeyJoinColumn(name = "user_id")
public class Student extends User {

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "student_classrooms",
        joinColumns = @JoinColumn(name = "student_id"),
        inverseJoinColumns = @JoinColumn(name = "classroom_id")
    )
    private List<Classroom> classrooms = new ArrayList<>();

    public Student(String name, String email, String password) {
        super(name, email, password, Role.ALUNO);
    }
}