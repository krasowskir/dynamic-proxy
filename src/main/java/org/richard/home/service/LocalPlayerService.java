package org.richard.home.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.validation.constraints.NotBlank;
import org.richard.home.domain.Player;
import org.springframework.beans.factory.annotation.Autowired;

public class LocalPlayerService implements PlayerService {

    private EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

    @Autowired
    public LocalPlayerService(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public Player findPlayer(@NotBlank String id) {
        entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        var foundPlayer = entityManager.find(Player.class, id);
        transaction.commit();
        return foundPlayer;
    }
}
