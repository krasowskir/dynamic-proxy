package org.richard.home.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.validation.constraints.NotBlank;
import org.richard.home.domain.Player;
import org.richard.home.repository.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class LocalPlayerService implements PlayerService {

    private static Logger log = LoggerFactory.getLogger(LocalPlayerService.class);

    private EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;
    private PlayerRepository playerRepository;

    @Autowired
    public LocalPlayerService(EntityManagerFactory entityManagerFactory, PlayerRepository playerRepository) {
        this.entityManagerFactory = entityManagerFactory;
        this.playerRepository = playerRepository;
    }

    @Override
    public Player findPlayer(@NotBlank String name) {
        Player foundPlayer;
        try (var entityManager = entityManagerFactory.createEntityManager()){
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();
            foundPlayer = playerRepository.getPlayer(entityManager, name);
            transaction.commit();
        } catch (NoResultException e){
            log.error("no player found with the specified name: {}", name);
            throw e;
        }
        return foundPlayer;
    }
}
