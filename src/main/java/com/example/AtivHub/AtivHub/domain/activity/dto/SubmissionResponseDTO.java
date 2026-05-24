package com.example.AtivHub.AtivHub.domain.activity.dto;

import com.example.AtivHub.AtivHub.domain.activity.Submission;

import java.time.LocalDateTime;
import java.util.UUID;

public record SubmissionResponseDTO(
        UUID id,
        String activityTitle,
        String studentName,
        String answer,
        String feedback,
        Integer grade,
        LocalDateTime submittedAt
) {
    public SubmissionResponseDTO(Submission submission) {
        this(
                submission.getId(),
                submission.getActivity().getTitle(),
                submission.getStudent().getName(),
                submission.getAnswer(),
                submission.getFeedback(),
                submission.getGrade(),
                submission.getSubmittedAt()
        );
    }
}