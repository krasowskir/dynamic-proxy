package org.richard.home.service;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.richard.home.domain.Address;
import org.richard.home.domain.Player;
import org.richard.home.domain.Team;
import org.richard.home.repository.PlayerRepository;
import org.richard.home.service.dto.PlayerDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Objects;
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
            log.warn("no player found with the specified name: {}", name);
            throw e;
        }
    }

    @Override
    public Player findPlayerById(String id) {
        try (var entityManager = entityManagerFactory.createEntityManager()) {
            return Optional.ofNullable(entityManager.find(Player.class, id))
                    .orElseThrow(() -> new NoResultException("no player found with id: " + id));
        } catch (NoResultException e) {
            log.warn(e.getMessage());
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
        } catch (PersistenceException | IllegalStateException e) {
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
            transaction = entityManager.getTransaction();
            transaction.begin();
            Player foundPlayer = entityManager.find(Player.class, id);
            foundPlayer = updatePlayerAttributes(toBe, foundPlayer);
            transaction.commit();
            return foundPlayer;
        } catch (PersistenceException | IllegalStateException e) {
            log.error("error while saving player: {} with id: {}", toBe, id, e);
            transaction.rollback();
            throw e;
        }
    }

    @Override
    public Player updatePlayerById(Map<String, PlayerDTO> toBe) {
        var key = toBe.keySet().iterator().next();
        return this.updatePlayerById(toBe.get(key), key);
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
                entityManager.remove(
                        Optional.ofNullable(
                                        entityManager.find(Player.class, playerId))
                                .orElseThrow(() -> new NoResultException("no player found with id " + playerId))
                );
                transaction.commit();
                return true;
            } catch (NoResultException e) {
                log.error("could not delete player: {} as it could not be found!: ", playerId);
                transaction.rollback();
                throw e;
            } catch (IllegalStateException | PersistenceException e) {
                log.error("error while deleting player: {}", playerId, e);
                transaction.rollback();
                throw e;
            }
        } else {
            log.error("playerId {} is null or empty", playerId);
        }
        return false;
    }

    @Override
    public Map.Entry<Player, Team> updateTeamOfPlayer(String playerId, String newTeamId) {
        EntityTransaction transaction = null;
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            transaction = entityManager.getTransaction();
            transaction.begin();
            var foundPlayer = Objects.requireNonNull(entityManager.find(Player.class, playerId), String.format("Player with id: %s not found!", playerId));
            var foundTeam = Objects.requireNonNull(entityManager.find(Team.class, newTeamId), String.format("Team with id: %s not found!", newTeamId));
            foundPlayer.setCurrentTeam(foundTeam);
            transaction.commit();
            return Map.entry(foundPlayer, foundTeam);
        } catch (NullPointerException e) {
            log.error("error while updating player!");
            log.warn(e.getMessage());
            transaction.rollback();
            throw e;
        } catch (RollbackException | IllegalStateException e) {
            log.error("error while updating player!");
            transaction.rollback();
            throw e;
        }
    }

    @Override
    public void deletePlayersContract(String playerId, String teamId) {
        EntityTransaction transaction = null;
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            transaction = entityManager.getTransaction();
            transaction.begin();
            var foundPlayer = Objects.requireNonNull(
                    entityManager.find(Player.class, playerId), String.format("Player with id: %s not found!", playerId));
            if (foundPlayer.getCurrentTeam().getId() != Integer.parseInt(teamId)) {
                throw new IllegalArgumentException(String.format("provided teamId: %s is not the current team of the player: %s", teamId, playerId));
            }
            foundPlayer.setCurrentTeam(null);
            transaction.commit();
        } catch (NullPointerException e) {
            log.error("error cannot find player with id: {}!", playerId);
            log.warn(e.getMessage());
            transaction.rollback();
            throw e;
        } catch (RollbackException | IllegalStateException e) {
            log.error("error while terminating contract of player: {} with team!", playerId);
            transaction.rollback();
            throw e;
        } catch (IllegalArgumentException e) {
            log.warn("player: {} is currently not under contract with team: {}. Hence contract could not be terminated!", playerId, teamId);
            transaction.rollback();
            throw e;
        }
    }
}
