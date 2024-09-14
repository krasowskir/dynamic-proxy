package org.richard.home.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.richard.home.domain.League;
import org.richard.home.domain.League_;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class JpaLeagueRepository implements LeagueRepository {

    private static Logger log = LoggerFactory.getLogger(JpaLeagueRepository.class);

    private EntityManagerFactory entityManagerFactory;

    public JpaLeagueRepository(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public League getLeagueById(String leagueId) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<League> query = criteriaBuilder.createQuery(League.class);
            Root<League> root = query.from(League.class);

            return entityManager.createQuery(query.where(criteriaBuilder.equal(root.get(League_.ID), leagueId))).getSingleResult();
        } catch (IllegalArgumentException | NoResultException | NonUniqueResultException e) {
            log.error("error while querying by leagueId: {}", leagueId);
        }
        return null;
    }

    @Override
    public League getLeagueByName(String leagueName) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<League> query = criteriaBuilder.createQuery(League.class);
            Root<League> root = query.from(League.class);

            entityManager.createQuery(query.where(criteriaBuilder.equal(root.get(League_.NAME), leagueName))).getSingleResult();
        } catch (IllegalArgumentException | NoResultException | NonUniqueResultException e) {
            log.error("error while querying by leagueName: {}", leagueName);
        }
        return null;
    }

    @Override
    public List<League> getAllLeagues() {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<League> query = criteriaBuilder.createQuery(League.class);
            query.from(League.class);

            return entityManager.createQuery(query).getResultList();
        } catch (IllegalArgumentException | NoResultException | NonUniqueResultException e) {
            log.error("error while querying all leagues");
            throw e;
        }
    }

    @Override
    public League getLeagueByCode(String code) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<League> query = criteriaBuilder.createQuery(League.class);
            Root<League> root = query.from(League.class);
            return entityManager.createQuery(query.where(criteriaBuilder.equal(root.get(League_.CODE), code))).getSingleResult();
        } catch (IllegalArgumentException | NoResultException | NonUniqueResultException e) {
            log.error("error while querying by code: {}", code);
        }
        return null;
    }
}
