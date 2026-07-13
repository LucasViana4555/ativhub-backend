package com.example.AtivHub.AtivHub.domain.classroom.event;

import java.io.Serializable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentJoinedClassroomEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID studentId;
    private UUID classroomId;
    private String classroomName;
    private String classroomSubject;
    private String classroomCode;
}
