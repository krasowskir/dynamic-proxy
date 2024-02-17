package org.richard.home.config;

import jakarta.persistence.EntityManagerFactory;
import org.richard.home.service.LocalPlayerService;
import org.richard.home.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {

    @Bean
    @Autowired
    public PlayerService playerService(EntityManagerFactory entityManagerFactory) {
        return new LocalPlayerService(entityManagerFactory);
    }
}
