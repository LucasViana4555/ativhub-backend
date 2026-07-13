package com.example.gamification.listener.event;

import java.io.Serializable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionEvaluatedEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID submissionId;
    private UUID studentId;
    private UUID activityId;
    private String activityTitle;
    private Integer xpReward;
    private Boolean approved;
    private Integer grade;
    private String feedback;
}
