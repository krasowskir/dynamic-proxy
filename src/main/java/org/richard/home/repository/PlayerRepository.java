package org.richard.home.repository;

import jakarta.persistence.EntityManager;
import org.richard.home.domain.Address;
import org.richard.home.domain.Player;

import java.util.List;
import java.util.Map;

public interface PlayerRepository {

    Player getPlayer(EntityManager entityManager, String name);

    List<Player> getPlayerByAlter(EntityManager entityManager,int alter);

    Map<Player, Address> getAllPlayers(EntityManager entityManager);

    List<Player> getPlayersFromTeam(EntityManager entityManager, int teamId);

    int savePlayer(Player toSave);

    List<Player> savePlayerList(List<Player> toSaveList);

    boolean updatePlayer(Player toBe, String nameWhere);

    boolean savePlayerLivesIn(Player toSave, Address whereLive);
}
