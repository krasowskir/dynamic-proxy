package org.richard.home.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.richard.home.domain.Address;
import org.richard.home.domain.Player;
import org.richard.home.domain.Player_;
import org.richard.home.domain.Team;

import java.util.List;
import java.util.Map;

public class JpaPlayerRepository implements PlayerRepository {

    private EntityManagerFactory entityManagerFactory;

    public JpaPlayerRepository(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public Player getPlayer(EntityManager localEntityManager, String name) {
            CriteriaBuilder criteriaBuilder = localEntityManager.getCriteriaBuilder();
            CriteriaQuery<Player> query = criteriaBuilder.createQuery(Player.class);
    //        CriteriaQuery<Player> query = entityManager.createNamedQuery("findPlayerByName", Player.class);
            Root<Player> root = query.from(Player.class);
            query.where(criteriaBuilder.equal(root.get(Player_.NAME), name));
            return localEntityManager.createQuery(query).getSingleResult();
    }

    // ToDo: Thread basierte registry, wo der entityManager geholt wird pro thread!
    @Override
    public List<Player> getPlayerByAlter( int alter) {
        try (var entityManager = this.entityManagerFactory.createEntityManager()) {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Player> query = criteriaBuilder.createQuery(Player.class);
            Root<Player> root = query.from(Player.class);
            query.where(criteriaBuilder.equal(root.get(Player_.ALTER), alter));

            return entityManager.createQuery(query).getResultList();
        }
    }

    @Override
    public Map<Player, Address> getAllPlayers() {
        // joining over multiple tables does not work in JPA!!!
        return null;
    }

    // ToDo: dynamic Query via Graph! und via annotation maping
    @Override
    public List<Player> getPlayersFromTeam( int teamId) {
        try (var entityManager = this.entityManagerFactory.createEntityManager()) {
            var criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Player> query = criteriaBuilder.createQuery(Player.class);
            Root<Team> root = query.from(Team.class);
//        root.join(Team_.)

            return null;
        }
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
