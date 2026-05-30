package com.example.AtivHub.AtivHub.service;

import com.example.AtivHub.AtivHub.domain.activity.Activity;
import com.example.AtivHub.AtivHub.domain.activity.Submission;
import com.example.AtivHub.AtivHub.domain.activity.dto.EvaluationRequestDTO;
import com.example.AtivHub.AtivHub.domain.activity.dto.SubmissionRequestDTO;
import com.example.AtivHub.AtivHub.domain.activity.dto.SubmissionResponseDTO;
import com.example.AtivHub.AtivHub.domain.user.Professor;
import com.example.AtivHub.AtivHub.domain.user.Student;
import com.example.AtivHub.AtivHub.domain.user.User;
import com.example.AtivHub.AtivHub.repository.ActivityRepository;
import com.example.AtivHub.AtivHub.repository.SubmissionRepository;
import com.example.AtivHub.AtivHub.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

// Recriando o arquivo para forçar a reindexação pela IDE
@Service
@RequiredArgsConstructor
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;

    @Transactional
    public SubmissionResponseDTO submitActivity(UUID activityId, SubmissionRequestDTO data) {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Object unproxied = org.hibernate.Hibernate.unproxy(principal);
        if (!(unproxied instanceof Student student)) {
            throw new IllegalStateException("Apenas alunos podem submeter atividades.");
        }

        User userObj = userRepository.findById(student.getId())
                .orElseThrow(() -> new EntityNotFoundException("Aluno não encontrado."));
        Student managedStudent = (Student) org.hibernate.Hibernate.unproxy(userObj);

        if (submissionRepository.findByActivityIdAndStudentId(activityId, managedStudent.getId()).isPresent()) {
            throw new IllegalArgumentException("Você já submeteu uma resposta para esta atividade.");
        }

        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new EntityNotFoundException("Atividade não encontrada."));

        Submission submission = Submission.builder()
                .activity(activity)
                .student(managedStudent)
                .answer(data.answer())
                .build();

        submissionRepository.save(submission);
        return new SubmissionResponseDTO(submission);
    }

    @Transactional
    public SubmissionResponseDTO evaluateSubmission(UUID submissionId, EvaluationRequestDTO data) {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Object unproxied = org.hibernate.Hibernate.unproxy(principal);
        if (!(unproxied instanceof Professor professor)) {
            throw new IllegalStateException("Apenas professores podem avaliar atividades.");
        }

        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new EntityNotFoundException("Submissão não encontrada."));

        if (!submission.getActivity().getProfessor().getId().equals(professor.getId())) {
            throw new IllegalStateException("Você só pode avaliar submissões das suas próprias atividades.");
        }
        
        if (submission.getGrade() != null) {
            throw new IllegalArgumentException("Esta submissão já foi avaliada.");
        }

        submission.setFeedback(data.feedback());
        submission.setGrade(data.grade());

        if (data.approved()) {
            Student student = submission.getStudent();
            student.setXp(student.getXp() + submission.getActivity().getXpReward());
            userRepository.save(student);
        }

        submissionRepository.save(submission);
        return new SubmissionResponseDTO(submission);
    }

    public List<SubmissionResponseDTO> listSubmissionsByActivity(UUID activityId) {
        return submissionRepository.findAllByActivityId(activityId)
                .stream()
                .map(SubmissionResponseDTO::new)
                .collect(Collectors.toList());
    }

    public List<SubmissionResponseDTO> listMySubmissions() {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Object unproxied = org.hibernate.Hibernate.unproxy(principal);
        if (!(unproxied instanceof Student student)) {
            throw new IllegalStateException("Apenas alunos possuem submissões.");
        }

        return submissionRepository.findAllByStudentId(student.getId())
                .stream()
                .map(SubmissionResponseDTO::new)
                .collect(Collectors.toList());
    }
}