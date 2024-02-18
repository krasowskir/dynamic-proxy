package org.richard.home.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.richard.home.domain.Address;
import org.richard.home.domain.Player;
import org.richard.home.domain.Player_;

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

    @Override
    public List<Player> getPlayerByAlter(int alter) {
        return null;
    }

    @Override
    public Map<Player, Address> getAllPlayers() {
        return null;
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
