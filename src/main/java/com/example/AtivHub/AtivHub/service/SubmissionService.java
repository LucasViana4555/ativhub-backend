package com.example.AtivHub.AtivHub.service;

import com.example.AtivHub.AtivHub.domain.activity.Activity;
import com.example.AtivHub.AtivHub.domain.activity.Submission;
import com.example.AtivHub.AtivHub.domain.activity.dto.EvaluationRequestDTO;
import com.example.AtivHub.AtivHub.domain.activity.dto.SubmissionRequestDTO;
import com.example.AtivHub.AtivHub.domain.activity.dto.SubmissionResponseDTO;
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

@Service
@RequiredArgsConstructor
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;

    @Transactional
    public SubmissionResponseDTO submitActivity(UUID activityId, SubmissionRequestDTO data) {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User student;

        if (principal instanceof User) {
            student = (User) principal;
        } else {
            throw new IllegalStateException("Usuário não autenticado.");
        }

        if (submissionRepository.findByActivityIdAndStudentId(activityId, student.getId()).isPresent()) {
            throw new IllegalArgumentException("Você já submeteu uma resposta para esta atividade.");
        }

        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new EntityNotFoundException("Atividade não encontrada."));

        Submission submission = Submission.builder()
                .activity(activity)
                .student(student)
                .answer(data.answer())
                .build();

        submissionRepository.save(submission);
        return new SubmissionResponseDTO(submission);
    }

    @Transactional
    public SubmissionResponseDTO evaluateSubmission(UUID submissionId, EvaluationRequestDTO data) {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User professor;

        if (principal instanceof User) {
            professor = (User) principal;
        } else {
            throw new IllegalStateException("Usuário não autenticado.");
        }

        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new EntityNotFoundException("Submissão não encontrada."));

        // Garantir que o professor que está avaliando é o dono da atividade
        if (!submission.getActivity().getProfessor().getId().equals(professor.getId())) {
            throw new IllegalStateException("Você só pode avaliar submissões das suas próprias atividades.");
        }
        
        // Verifica se já foi avaliado antes para evitar dar XP duas vezes
        if (submission.getGrade() != null) {
            throw new IllegalArgumentException("Esta submissão já foi avaliada.");
        }

        submission.setFeedback(data.feedback());
        submission.setGrade(data.grade());

        if (data.approved()) {
            User student = submission.getStudent();
            student.setXp(student.getXp() + submission.getActivity().getXpReward());
            userRepository.save(student);
        }

        submissionRepository.save(submission);
        return new SubmissionResponseDTO(submission);
    }

    @Transactional(readOnly = true)
    public List<SubmissionResponseDTO> listSubmissionsByActivity(UUID activityId) {
        return submissionRepository.findAllByActivityId(activityId)
                .stream()
                .map(SubmissionResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SubmissionResponseDTO> listMySubmissions() {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User student;

        if (principal instanceof User) {
            student = (User) principal;
        } else {
            throw new IllegalStateException("Usuário não autenticado.");
        }

        return submissionRepository.findAllByStudentId(student.getId())
                .stream()
                .map(SubmissionResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public SubmissionResponseDTO updateSubmission(UUID submissionId, SubmissionRequestDTO data) {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User student;

        if (principal instanceof User) {
            student = (User) principal;
        } else {
            throw new IllegalStateException("Usuário não autenticado.");
        }

        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new EntityNotFoundException("Submissão não encontrada."));

        if (!submission.getStudent().getId().equals(student.getId())) {
            throw new IllegalStateException("Você só pode editar suas próprias submissões.");
        }

        if (submission.getGrade() != null) {
            throw new IllegalStateException("Você não pode editar uma submissão já avaliada.");
        }

        submission.setAnswer(data.answer());
        submissionRepository.save(submission);
        return new SubmissionResponseDTO(submission);
    }

    @Transactional
    public void deleteSubmission(UUID submissionId) {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User student;

        if (principal instanceof User) {
            student = (User) principal;
        } else {
            throw new IllegalStateException("Usuário não autenticado.");
        }

        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new EntityNotFoundException("Submissão não encontrada."));

        if (!submission.getStudent().getId().equals(student.getId())) {
            throw new IllegalStateException("Você só pode deletar suas próprias submissões.");
        }

        if (submission.getGrade() != null) {
            throw new IllegalStateException("Você não pode deletar uma submissão já avaliada.");
        }

        submissionRepository.delete(submission);
    }
}