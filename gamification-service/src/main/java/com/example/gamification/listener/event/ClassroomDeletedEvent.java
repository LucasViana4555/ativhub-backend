package com.example.gamification.listener.event;

import java.io.Serializable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassroomDeletedEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
}
