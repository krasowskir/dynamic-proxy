package org.richard.home.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.*;
import org.richard.home.domain.Player;
import org.richard.home.domain.Player_;
import org.richard.home.domain.Team;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class JpaTeamRepository implements TeamRepository {

    private static Logger log = LoggerFactory.getLogger(JpaTeamRepository.class);
    private EntityManagerFactory entityManagerFactory;

    public JpaTeamRepository(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public Team getTeamOfPlayer(String playerId) {

        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Tuple> query = criteriaBuilder.createTupleQuery();
            Root<Player> root = query.from(Player.class);
            Path<Team> teamPath = root.get(Player_.CURRENT_TEAM);

            root.join(Player_.CURRENT_TEAM, JoinType.INNER);
            query.multiselect(root, teamPath).where(criteriaBuilder.equal(root.get(Player_.ID), playerId));

            List<Tuple> resultList = entityManager.createQuery(query)
                    .getResultList();

            return Optional.of(resultList.stream().map(t -> {
                                Team team = t.get(teamPath);
                                return team;
                            })
                            .findFirst())
                    .get()
                    .orElseThrow(NoResultException::new);
        } catch (NoResultException e) {
            log.warn("no team found for player: {}", playerId);
            throw e;
        }
    }
}
