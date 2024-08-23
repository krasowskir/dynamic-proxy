package org.richard.home.service;

import jakarta.persistence.*;
import org.richard.home.domain.League;
import org.richard.home.repository.LeagueRepository;
import org.richard.home.service.dto.LeagueDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static org.richard.home.service.JpaTeamService.isNotNullOrEmpty;

public class JpaLeagueService implements LeagueService {

    private static final Logger log = LoggerFactory.getLogger(JpaLeagueService.class);

    private LeagueRepository leagueRepository;
    private EntityManagerFactory entityManagerFactory;

    public JpaLeagueService(EntityManagerFactory entityManagerFactory, LeagueRepository leagueRepository) {
        this.entityManagerFactory = entityManagerFactory;
        this.leagueRepository = leagueRepository;
    }

    @Override
    public League createLeague(LeagueDTO leagueDTO) {
        EntityTransaction transaction = null;
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            transaction = entityManager.getTransaction();
            transaction.begin();
            var league = mapToDomainLayer(leagueDTO);
            entityManager.persist(league);
            transaction.commit();
            return league;
        } catch (IllegalStateException | PersistenceException e){
            log.error("error while persisting league: {}", leagueDTO, e);
            transaction.rollback();
            throw e;
        }
    }

    @Override
    public League getLeague(String id) {
        Objects.requireNonNull(id, "id cannot be null as parameter in service layer");
        return leagueRepository.getLeagueById(id);
    }

    @Override
    public League getLeagueByName(String name) {
        Objects.requireNonNull(name, "name cannot be null in service layer!");
        return leagueRepository.getLeagueByName(name);
    }

    @Override
    public League getLeagueByCode(String code) {
        Objects.requireNonNull(code, "code as parameter cannot be null in service layer!");
        return leagueRepository.getLeagueByCode(code);
    }


    @Override
    public boolean deleteLeague(String id) {
        EntityTransaction transaction = null;
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            transaction = entityManager.getTransaction();
            transaction.begin();
            entityManager.remove(Objects.requireNonNull(entityManager.find(League.class, id)));
            transaction.commit();
            return true;
        } catch (IllegalStateException | RollbackException e) {
            log.error("something went wrong during deletion of league: {}!", id);
            transaction.rollback();
        } catch (NullPointerException e) {
            log.error("league: {} could not be found!", id);
            transaction.rollback();
        }
        return false;
    }

    @Override
    public League updateLeague(String id, LeagueDTO toBe) {
        EntityTransaction transaction = null;
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            League league = entityManager.find(League.class, id);
            transaction = entityManager.getTransaction();
            transaction.begin();
            league = handleUpdate(league, toBe);
            transaction.commit();
            return league;
        } catch (IllegalStateException | PersistenceException e) {
            log.error("error while updating league: {} with id: {}", toBe, id, e);
            transaction.rollback();
        }
        return null;
    }

    private League handleUpdate(League fromLeague, LeagueDTO toBe) {
        return new League(
                fromLeague.getId(),
                isNotNullOrEmpty(toBe.getCode()) ? toBe.getCode() : fromLeague.getCode(),
                isNotNullOrEmpty(toBe.getName()) ? toBe.getName() : fromLeague.getName()
        );
    }

    private League mapToDomainLayer(LeagueDTO leagueDTO) {
        var league = new League();
        league.setCode(leagueDTO.getCode());
        league.setName(leagueDTO.getName());
        return league;
    }
}
