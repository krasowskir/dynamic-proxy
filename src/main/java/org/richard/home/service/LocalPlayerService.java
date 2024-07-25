package org.richard.home.service;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.validation.constraints.NotBlank;
import org.richard.home.domain.Address;
import org.richard.home.domain.Player;
import org.richard.home.repository.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

public class LocalPlayerService implements PlayerService {

    private static final Logger log = LoggerFactory.getLogger(LocalPlayerService.class);

    private final EntityManagerFactory entityManagerFactory;
    private final PlayerRepository playerRepository;

    @Autowired
    public LocalPlayerService(EntityManagerFactory entityManagerFactory, PlayerRepository playerRepository) {
        this.entityManagerFactory = entityManagerFactory;
        this.playerRepository = playerRepository;
    }

    @Override
    public Player findPlayer(@NotBlank String name) {
        Player foundPlayer;
        try (var entityManager = entityManagerFactory.createEntityManager()) {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();
            foundPlayer = playerRepository.getPlayer(entityManager, name);
            transaction.commit();
        } catch (NoResultException e) {
            log.error("no player found with the specified name: {}", name);
            throw e;
        }
        return foundPlayer;
    }

    @Override
    public List<Player> findPlayerByAge(int age) {
        try (var entityManager = entityManagerFactory.createEntityManager()) {
            return playerRepository.getPlayerByAlter(entityManager, age);
        } catch (Exception e) {
            log.error("error during query", e);
        }
        return null;
    }


    @Override
    public Map<Player, Address> getAllPlayers() {
        try (var entityManager = entityManagerFactory.createEntityManager()){
            return null;

        }
    }

    @Override
    public List<Player> getPlayersFromTeam(int teamId) {
        return null;
    }

    @Override
    public int savePlayer(Player toSave) {
        return 0;
    }

    @Override
    public List<Player> savePlayerList(List<Player> toSaveList) {
        return null;
    }

    @Override
    public boolean updatePlayer(Player toBe, String nameWhere) {
        return false;
    }

    @Override
    public boolean savePlayerLivesIn(Player toSave, Address whereLive) {
        return false;
    }
}
