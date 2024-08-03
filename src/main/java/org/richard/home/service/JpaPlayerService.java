package org.richard.home.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
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

    private static void updatePlayerAttributes(PlayerDTO toBe, Player player) {
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
    public Player findPlayerById(String id) {
        try (var entityManager = entityManagerFactory.createEntityManager()) {
            return Optional.ofNullable(entityManager.find(Player.class, id)).orElseThrow(() -> new NoResultException("no player found with id: " + id));
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
        try (var entityManager = entityManagerFactory.createEntityManager()) {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();
            entityManager.persist(toSave);
            transaction.commit();
            return toSave;
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

    // ToDo: rollback der Transaktion fehlt!
    @Override
    public Player updatePlayerById(PlayerDTO toBe, String id) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            Player foundPlayer = entityManager.find(Player.class, id);
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();
            updatePlayerAttributes(toBe, foundPlayer);
            foundPlayer = entityManager.merge(foundPlayer);
            transaction.commit();
            return foundPlayer;
        }
    }

    @Override
    public boolean savePlayerLivesIn(Player toSave, Address whereLive) {
        return false;
    }

    // ToDo: rollback der Transaktion fehlt!
    @Override
    public boolean deletePlayerById(String playerId) {
        if (playerId != null && !playerId.isBlank()) {
            try (var entityManager = entityManagerFactory.createEntityManager()) {
                EntityTransaction transaction = entityManager.getTransaction();
                transaction.begin();
                entityManager.remove(Optional.ofNullable(entityManager.find(Player.class, playerId)).orElseThrow(() -> new NoResultException("no player found with id " + playerId)));
                transaction.commit();
                return true;
            } catch (Exception e) {
                log.error("error while deleting player: " + playerId, e);
            }
        } else {
            log.error("playerId {} is null or empty", playerId);
        }
        return false;
    }
}
