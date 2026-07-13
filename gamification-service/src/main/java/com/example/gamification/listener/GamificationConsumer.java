package com.example.gamification.listener;

import com.example.gamification.domain.Classroom;
import com.example.gamification.domain.Student;
import java.util.ArrayList;
import com.example.gamification.listener.event.StudentJoinedClassroomEvent;
import com.example.gamification.listener.event.StudentRegisteredEvent;
import com.example.gamification.listener.event.StudentUpdatedEvent;
import com.example.gamification.listener.event.SubmissionEvaluatedEvent;
import com.example.gamification.listener.event.StudentDeletedEvent;
import com.example.gamification.listener.event.ClassroomDeletedEvent;
import com.example.gamification.repository.ClassroomRepository;
import com.example.gamification.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class GamificationConsumer {

    private final StudentRepository studentRepository;
    private final ClassroomRepository classroomRepository;

    @KafkaListener(topics = "ativhub.students.reg", groupId = "gamification-group")
    @Transactional
    public void onStudentRegistered(StudentRegisteredEvent event) {
        System.out.println("[GamificationConsumer] Recebido StudentRegisteredEvent: " + event);
        if (studentRepository.existsById(event.getId())) {
            System.out.println("[GamificationConsumer] Estudante já cadastrado localmente: " + event.getId());
            return;
        }

        Student student = Student.builder()
                .id(event.getId())
                .name(event.getName())
                .email(event.getEmail())
                .xp(0)
                .level(1)
                .submissionsCount(0)
                .build();

        studentRepository.save(student);
        System.out.println("[GamificationConsumer] Estudante cadastrado com sucesso: " + student.getName());
    }

    @KafkaListener(topics = "ativhub.students.update", groupId = "gamification-group")
    @Transactional
    public void onStudentUpdated(StudentUpdatedEvent event) {
        System.out.println("[GamificationConsumer] Recebido StudentUpdatedEvent: " + event);
        studentRepository.findById(event.getId()).ifPresentOrElse(student -> {
            student.setName(event.getName());
            studentRepository.save(student);
            System.out.println("[GamificationConsumer] Nome do estudante atualizado localmente para: " + event.getName());
        }, () -> {
            System.out.println("[GamificationConsumer] Estudante não encontrado para atualização de nome: " + event.getId());
        });
    }

    @KafkaListener(topics = "ativhub.classrooms.join", groupId = "gamification-group")
    @Transactional
    public void onStudentJoinedClassroom(StudentJoinedClassroomEvent event) {
        System.out.println("[GamificationConsumer] Recebido StudentJoinedClassroomEvent: " + event);

        // Garante que a sala de aula exista no banco local
        Classroom classroom = classroomRepository.findById(event.getClassroomId())
                .orElseGet(() -> {
                    Classroom newRoom = Classroom.builder()
                            .id(event.getClassroomId())
                            .name(event.getClassroomName())
                            .subject(event.getClassroomSubject())
                            .roomCode(event.getClassroomCode())
                            .build();
                    return classroomRepository.save(newRoom);
                });

        // Garante que o estudante exista no banco local
        Student student = studentRepository.findById(event.getStudentId())
                .orElseThrow(() -> new IllegalStateException("Estudante não encontrado para vincular à sala: " + event.getStudentId()));

        if (!student.getClassrooms().contains(classroom)) {
            student.getClassrooms().add(classroom);
            studentRepository.save(student);
            System.out.println("[GamificationConsumer] Estudante " + student.getName() + " vinculado à sala " + classroom.getName());
        }
    }

    @KafkaListener(topics = "ativhub.submissions.eval", groupId = "gamification-group")
    @Transactional
    public void onSubmissionEvaluated(SubmissionEvaluatedEvent event) {
        System.out.println("[GamificationConsumer] Recebido SubmissionEvaluatedEvent: " + event);

        Student student = studentRepository.findById(event.getStudentId())
                .orElseThrow(() -> new IllegalStateException("Estudante não encontrado para atualização de XP: " + event.getStudentId()));

        // Incrementa o contador de submissões
        student.setSubmissionsCount(student.getSubmissionsCount() + 1);

        // Se a atividade foi aprovada, adiciona XP e recalcula nível
        if (event.getApproved()) {
            student.setXp(student.getXp() + event.getXpReward());
            int newLevel = (student.getXp() / 100) + 1;
            student.setLevel(newLevel);
            System.out.println("[GamificationConsumer] XP adicionado a " + student.getName() + ": +" + event.getXpReward() + " XP. Novo Nível: " + newLevel);
        }

        studentRepository.save(student);

        // Simula o disparo de notificações / emails de forma assíncrona
        dispatchNotification(student, event);
    }

    @KafkaListener(topics = "ativhub.students.delete", groupId = "gamification-group")
    @Transactional
    public void onStudentDeleted(StudentDeletedEvent event) {
        System.out.println("[GamificationConsumer] Recebido StudentDeletedEvent: " + event);
        studentRepository.findById(event.getId()).ifPresentOrElse(student -> {
            student.getClassrooms().clear();
            studentRepository.save(student);
            studentRepository.delete(student);
            System.out.println("[GamificationConsumer] Estudante removido localmente: " + event.getId());
        }, () -> {
            System.out.println("[GamificationConsumer] Estudante não encontrado para remoção: " + event.getId());
        });
    }

    @KafkaListener(topics = "ativhub.classrooms.delete", groupId = "gamification-group")
    @Transactional
    public void onClassroomDeleted(ClassroomDeletedEvent event) {
        System.out.println("[GamificationConsumer] Recebido ClassroomDeletedEvent: " + event);
        classroomRepository.findById(event.getId()).ifPresentOrElse(classroom -> {
            for (Student student : new ArrayList<>(classroom.getStudents())) {
                student.getClassrooms().remove(classroom);
                studentRepository.save(student);
            }
            classroomRepository.delete(classroom);
            System.out.println("[GamificationConsumer] Sala de aula removida localmente: " + event.getId());
        }, () -> {
            System.out.println("[GamificationConsumer] Sala de aula não encontrada para remoção: " + event.getId());
        });
    }

    private void dispatchNotification(Student student, SubmissionEvaluatedEvent event) {
        System.out.println("\n--- [NOTIFICAÇÃO ASSÍNCRONA VIA MICROSERVIÇO] ---");
        System.out.println("Destinatário: " + student.getEmail());
        System.out.println("Assunto: Sua missão '" + event.getActivityTitle() + "' foi avaliada!");
        System.out.println("Mensagem: Olá " + student.getName() + ",");
        System.out.println("  Sua submissão para a atividade '" + event.getActivityTitle() + "' recebeu a nota " + event.getGrade() + ".");
        if (event.getFeedback() != null && !event.getFeedback().trim().isEmpty()) {
            System.out.println("  Feedback do Professor: \"" + event.getFeedback() + "\"");
        }
        if (event.getApproved()) {
            System.out.println("  Parabéns! Sua solução foi APROVADA. Você faturou +" + event.getXpReward() + " XP!");
            System.out.println("  Seu progresso atual: Nível " + student.getLevel() + " (" + student.getXp() + " XP totais).");
        } else {
            System.out.println("  Sua solução precisa de revisões e não pôde ser aprovada desta vez. Fale com seu professor.");
        }
        System.out.println("Atenciosamente,");
        System.out.println("Equipe AtivHub");
        System.out.println("-------------------------------------------------\n");
    }
}
