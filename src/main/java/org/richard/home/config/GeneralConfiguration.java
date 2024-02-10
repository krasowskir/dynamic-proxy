package org.richard.home.config;

import jakarta.validation.ConstraintValidatorFactory;
import jakarta.validation.Validation;
import org.richard.home.infrastructure.PlayerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;


@Configuration
public class GeneralConfiguration {

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor(){
        return new MethodValidationPostProcessor();
    }

    @Bean
    public LocalValidatorFactoryBean localValidatorFactoryBean() {
        return new LocalValidatorFactoryBean();
    }
    @Bean
    public ConstraintValidatorFactory validatorFactory(){
        return Validation.buildDefaultValidatorFactory().getConstraintValidatorFactory();
    }

    @Bean
    public PlayerService playerService(){
        return new PlayerService();
    }

}
