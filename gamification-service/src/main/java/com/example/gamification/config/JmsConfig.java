package com.example.gamification.config;

import com.example.gamification.listener.event.StudentRegisteredEvent;
import com.example.gamification.listener.event.StudentUpdatedEvent;
import com.example.gamification.listener.event.StudentJoinedClassroomEvent;
import com.example.gamification.listener.event.SubmissionEvaluatedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJms
public class JmsConfig {

    @Bean
    public MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");

        Map<String, Class<?>> typeIdMappings = new HashMap<>();
        typeIdMappings.put("StudentRegisteredEvent", StudentRegisteredEvent.class);
        typeIdMappings.put("StudentUpdatedEvent", StudentUpdatedEvent.class);
        typeIdMappings.put("StudentJoinedClassroomEvent", StudentJoinedClassroomEvent.class);
        typeIdMappings.put("SubmissionEvaluatedEvent", SubmissionEvaluatedEvent.class);
        converter.setTypeIdMappings(typeIdMappings);

        return converter;
    }
}
