package com.example.AtivHub.AtivHub.infra.messaging;

import com.example.AtivHub.AtivHub.domain.activity.event.SubmissionEvaluatedEvent;
import com.example.AtivHub.AtivHub.domain.classroom.event.ClassroomDeletedEvent;
import com.example.AtivHub.AtivHub.domain.classroom.event.StudentJoinedClassroomEvent;
import com.example.AtivHub.AtivHub.domain.user.event.StudentDeletedEvent;
import com.example.AtivHub.AtivHub.domain.user.event.StudentRegisteredEvent;
import com.example.AtivHub.AtivHub.domain.user.event.StudentUpdatedEvent;
import com.example.AtivHub.AtivHub.service.MessagePublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class MessagingEventListener {

    private final MessagePublisher messagePublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleStudentRegistered(StudentRegisteredEvent event) {
        messagePublisher.publishStudentRegistered(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleStudentUpdated(StudentUpdatedEvent event) {
        messagePublisher.publishStudentUpdated(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleStudentJoinedClassroom(StudentJoinedClassroomEvent event) {
        messagePublisher.publishStudentJoinedClassroom(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleSubmissionEvaluated(SubmissionEvaluatedEvent event) {
        messagePublisher.publishSubmissionEvaluated(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleStudentDeleted(StudentDeletedEvent event) {
        messagePublisher.publishStudentDeleted(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleClassroomDeleted(ClassroomDeletedEvent event) {
        messagePublisher.publishClassroomDeleted(event);
    }
}
