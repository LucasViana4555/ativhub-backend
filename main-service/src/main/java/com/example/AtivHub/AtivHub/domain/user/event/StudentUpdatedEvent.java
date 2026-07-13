package com.example.AtivHub.AtivHub.domain.user.event;

import java.io.Serializable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentUpdatedEvent implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private UUID id;
    private String name;
}
