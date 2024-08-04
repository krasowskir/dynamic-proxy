package org.richard.home.service;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.richard.home.domain.Address;
import org.richard.home.domain.Player;
import org.richard.home.repository.PlayerRepository;
import org.richard.home.web.dto.PlayerDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.richard.home.service.JpaTeamService.isNotNullOrEmpty;

public class JpaPlayerService implements PlayerService {

    private static final Logger log = LoggerFactory.getLogger(JpaPlayerService.class);

    private final EntityManagerFactory entityManagerFactory;
    private final PlayerRepository playerRepository;

    @Autowired
    public JpaPlayerService(EntityManagerFactory entityManagerFactory, PlayerRepository playerRepository) {
        this.entityManagerFactory = entityManagerFactory;
        this.playerRepository = playerRepository;
    }

    private static Player updatePlayerAttributes(PlayerDTO toBe, Player player) {
        if (isNotNullOrEmpty(toBe.getName())) {
            player.setName(toBe.getName());
        }
        if (isNotNullOrEmpty(toBe.getPosition())) {
            player.setPosition(toBe.getPosition());
        }
        if (toBe.getAge() != 0) {
            player.setAlter(toBe.getAge());
        }
        if (toBe.getDateOfBirth() != null) {
            player.setDateOfBirth(toBe.getDateOfBirth());
        }
        if (toBe.getCountryOfBirth() != null) {
            player.setCountryOfBirth(toBe.getCountryOfBirth().name());
        }
        return player;
    }

    @Override
    public Player findPlayer(@NotBlank String name) {
        try (var entityManager = entityManagerFactory.createEntityManager()) {
            return playerRepository.getPlayer(entityManager, name);
        } catch (NoResultException e) {
            log.error("no player found with the specified name: {}", name);
            throw e;
        }
    }

    @Override
    public Player findPlayerById(String id) {
        try (var entityManager = entityManagerFactory.createEntityManager()) {
            return Optional.ofNullable(entityManager.find(Player.class, id))
                    .orElseThrow(() -> new NoResultException("no player found with id: " + id));
        } catch (NoResultException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Player> findPlayerByAge(int age) {
        return playerRepository.getPlayerByAlter(age);
    }

    @Override
    public Map<Player, Address> getAllPlayers() {
        try (var entityManager = entityManagerFactory.createEntityManager()) {
            return null;

        }
    }

    @Override
    public List<Player> getPlayersFromTeam(int teamId) {
        return null;
    }

    @Override
    public Player savePlayer(Player toSave) {
        EntityTransaction transaction = null;
        try (var entityManager = entityManagerFactory.createEntityManager()) {
            transaction = entityManager.getTransaction();
            transaction.begin();
            entityManager.persist(toSave);
            transaction.commit();
            return toSave;
        } catch (PersistenceException | IllegalStateException e){
            log.error("error while saving player: {}", toSave, e);
            transaction.rollback();
            throw e;
        }
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
    public Player updatePlayerById(PlayerDTO toBe, String id) {
        EntityTransaction transaction = null;
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            Player foundPlayer = entityManager.find(Player.class, id);
            transaction = entityManager.getTransaction();
            transaction.begin();
            foundPlayer = updatePlayerAttributes(toBe, foundPlayer);
            transaction.commit();
            return foundPlayer;
        } catch (PersistenceException | IllegalStateException e){
            log.error("error while saving player: {} with id: {}", toBe, id, e);
            transaction.rollback();
            throw e;
        }
    }

    @Override
    public boolean savePlayerLivesIn(Player toSave, Address whereLive) {
        return false;
    }

    @Override
    public boolean deletePlayerById(String playerId) {
        EntityTransaction transaction = null;
        if (playerId != null && !playerId.isBlank()) {
            try (var entityManager = entityManagerFactory.createEntityManager()) {
                transaction = entityManager.getTransaction();
                transaction.begin();
                entityManager.remove(Optional.ofNullable(entityManager.find(Player.class, playerId)).orElseThrow(() -> new NoResultException("no player found with id " + playerId)));
                transaction.commit();
                return true;
            } catch (IllegalStateException | PersistenceException e) {
                log.error("error while deleting player: " + playerId, e);
                transaction.rollback();
                throw e;
            }
        } else {
            log.error("playerId {} is null or empty", playerId);
        }
        return false;
    }
}
