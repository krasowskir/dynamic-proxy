package org.richard.home.service;

import org.richard.home.domain.Address;
import org.richard.home.domain.Player;

import java.util.List;
import java.util.Map;

public interface PlayerService {
    Player findPlayer(String id);

    List<Player> findPlayerByAge(int age);

    Map<Player, Address> getAllPlayers();

    List<Player> getPlayersFromTeam(int teamId);

    Player savePlayer(Player toSave);

    List<Player> savePlayerList(List<Player> toSaveList);

    boolean updatePlayer(Player toBe, String nameWhere);

    boolean savePlayerLivesIn(Player toSave, Address whereLive);
}
