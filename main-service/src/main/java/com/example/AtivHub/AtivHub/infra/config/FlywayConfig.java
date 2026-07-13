package com.example.AtivHub.AtivHub.infra.config;

import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfig {

    @Bean
    public FlywayMigrationStrategy repairStrategy() {
        return flyway -> {
            // Repara a tabela de metadados do Flyway (checksums)
            flyway.repair();
            // Executa as migrações pendentes
            flyway.migrate();
        };
    }
}