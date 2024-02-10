package org.richard.home.config;

import org.richard.home.infrastructure.PlayerService;
import org.richard.home.infrastructure.VerifyAge;
import org.richard.home.web.PlayerServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class GeneralConfiguration {

//    @Bean
//    public MethodValidationPostProcessor methodValidationPostProcessor(){
//        return new MethodValidationPostProcessor();
//    }
//
//    @Bean
//    public LocalValidatorFactoryBean localValidatorFactoryBean() {
//        return new LocalValidatorFactoryBean();
//    }
//    @Bean
//    public ConstraintValidatorFactory validatorFactory(){
//        return Validation.buildDefaultValidatorFactory().getConstraintValidatorFactory();
//    }

    @Bean
    public PlayerService playerService(){
        return new PlayerService();
    }
    @Bean
    @Autowired
    public PlayerServlet playerServlet(VerifyAge verifyAge){
        var playerServlet = new PlayerServlet();
        playerServlet.setPlayerService(verifyAge);
        return playerServlet;
    }

}
