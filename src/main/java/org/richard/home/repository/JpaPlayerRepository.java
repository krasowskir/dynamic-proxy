package org.richard.home.repository;

import jakarta.persistence.EntityManager;
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

    public JpaPlayerRepository() {
    }

    @Override
    public Player getPlayer(EntityManager entityManager, String name) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Player> query = criteriaBuilder.createQuery(Player.class);
//        CriteriaQuery<Player> query = entityManager.createNamedQuery("findPlayerByName", Player.class);
        Root<Player> root = query.from(Player.class);
        query.where(criteriaBuilder.equal(root.get(Player_.NAME), name));
        return entityManager.createQuery(query).getSingleResult();
    }

    // ToDo: Thread basierte registry, wo der entityManager geholt wird pro thread!
    @Override
    public List<Player> getPlayerByAlter(EntityManager entityManager, int alter) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Player> query = criteriaBuilder.createQuery(Player.class);
        Root<Player> root = query.from(Player.class);
        query.where(criteriaBuilder.equal(root.get(Player_.ALTER), alter));

        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public Map<Player, Address> getAllPlayers(EntityManager entityManager) {
        // joining over multiple tables does not work in JPA!!!
        return null;
    }

    // ToDo: dynamic Query via Graph! und via annotation maping
    @Override
    public List<Player> getPlayersFromTeam(EntityManager entityManager, int teamId) {
        var criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Player> query = criteriaBuilder.createQuery(Player.class);
        Root<Team> root = query.from(Team.class);
//        root.join(Team_.)

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
