package com.example.AtivHub.AtivHub.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessagePublisher {

    private final ObjectProvider<JmsTemplate> jmsTemplateProvider;
    private final ObjectProvider<RabbitTemplate> rabbitTemplateProvider;
    private final ObjectProvider<KafkaTemplate<String, Object>> kafkaTemplateProvider;

    @Value("${ativhub.messaging.jms.enabled:true}")
    private boolean jmsEnabled;

    @Value("${ativhub.messaging.rabbitmq.enabled:false}")
    private boolean rabbitmqEnabled;

    @Value("${ativhub.messaging.kafka.enabled:false}")
    private boolean kafkaEnabled;

    public void publishStudentRegistered(Object event) {
        if (jmsEnabled) {
            jmsTemplateProvider.ifAvailable(template -> {
                template.convertAndSend("ativhub.students.reg", event);
                System.out.println("[JMS MessagePublisher] Student Registered Event sent.");
            });
        }
        if (rabbitmqEnabled) {
            rabbitTemplateProvider.ifAvailable(template -> {
                template.convertAndSend("ativhub.direct", "students.reg", event);
                System.out.println("[RabbitMQ MessagePublisher] Student Registered Event sent.");
            });
        }
        if (kafkaEnabled) {
            kafkaTemplateProvider.ifAvailable(template -> {
                template.send("ativhub.students.reg", event);
                System.out.println("[Kafka MessagePublisher] Student Registered Event sent.");
            });
        }
    }

    public void publishStudentUpdated(Object event) {
        if (jmsEnabled) {
            jmsTemplateProvider.ifAvailable(template -> {
                template.convertAndSend("ativhub.students.update", event);
                System.out.println("[JMS MessagePublisher] Student Updated Event sent.");
            });
        }
        if (rabbitmqEnabled) {
            rabbitTemplateProvider.ifAvailable(template -> {
                template.convertAndSend("ativhub.direct", "students.update", event);
                System.out.println("[RabbitMQ MessagePublisher] Student Updated Event sent.");
            });
        }
        if (kafkaEnabled) {
            kafkaTemplateProvider.ifAvailable(template -> {
                template.send("ativhub.students.update", event);
                System.out.println("[Kafka MessagePublisher] Student Updated Event sent.");
            });
        }
    }

    public void publishStudentJoinedClassroom(Object event) {
        if (jmsEnabled) {
            jmsTemplateProvider.ifAvailable(template -> {
                template.convertAndSend("ativhub.classrooms.join", event);
                System.out.println("[JMS MessagePublisher] Student Joined Classroom Event sent.");
            });
        }
        if (rabbitmqEnabled) {
            rabbitTemplateProvider.ifAvailable(template -> {
                template.convertAndSend("ativhub.direct", "classrooms.join", event);
                System.out.println("[RabbitMQ MessagePublisher] Student Joined Classroom Event sent.");
            });
        }
        if (kafkaEnabled) {
            kafkaTemplateProvider.ifAvailable(template -> {
                template.send("ativhub.classrooms.join", event);
                System.out.println("[Kafka MessagePublisher] Student Joined Classroom Event sent.");
            });
        }
    }

    public void publishSubmissionEvaluated(Object event) {
        if (jmsEnabled) {
            jmsTemplateProvider.ifAvailable(template -> {
                template.convertAndSend("ativhub.submissions.eval", event);
                System.out.println("[JMS MessagePublisher] Submission Evaluated Event sent.");
            });
        }
        if (rabbitmqEnabled) {
            rabbitTemplateProvider.ifAvailable(template -> {
                template.convertAndSend("ativhub.direct", "submissions.eval", event);
                System.out.println("[RabbitMQ MessagePublisher] Submission Evaluated Event sent.");
            });
        }
        if (kafkaEnabled) {
            kafkaTemplateProvider.ifAvailable(template -> {
                template.send("ativhub.submissions.eval", event);
                System.out.println("[Kafka MessagePublisher] Submission Evaluated Event sent.");
            });
        }
    }

    public void publishStudentDeleted(Object event) {
        if (jmsEnabled) {
            jmsTemplateProvider.ifAvailable(template -> {
                template.convertAndSend("ativhub.students.delete", event);
                System.out.println("[JMS MessagePublisher] Student Deleted Event sent.");
            });
        }
        if (rabbitmqEnabled) {
            rabbitTemplateProvider.ifAvailable(template -> {
                template.convertAndSend("ativhub.direct", "students.delete", event);
                System.out.println("[RabbitMQ MessagePublisher] Student Deleted Event sent.");
            });
        }
        if (kafkaEnabled) {
            kafkaTemplateProvider.ifAvailable(template -> {
                template.send("ativhub.students.delete", event);
                System.out.println("[Kafka MessagePublisher] Student Deleted Event sent.");
            });
        }
    }

    public void publishClassroomDeleted(Object event) {
        if (jmsEnabled) {
            jmsTemplateProvider.ifAvailable(template -> {
                template.convertAndSend("ativhub.classrooms.delete", event);
                System.out.println("[JMS MessagePublisher] Classroom Deleted Event sent.");
            });
        }
        if (rabbitmqEnabled) {
            rabbitTemplateProvider.ifAvailable(template -> {
                template.convertAndSend("ativhub.direct", "classrooms.delete", event);
                System.out.println("[RabbitMQ MessagePublisher] Classroom Deleted Event sent.");
            });
        }
        if (kafkaEnabled) {
            kafkaTemplateProvider.ifAvailable(template -> {
                template.send("ativhub.classrooms.delete", event);
                System.out.println("[Kafka MessagePublisher] Classroom Deleted Event sent.");
            });
        }
    }
}
