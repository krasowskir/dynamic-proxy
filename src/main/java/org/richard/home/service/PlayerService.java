package org.richard.home.service;

import org.richard.home.domain.Address;
import org.richard.home.domain.Player;
import org.richard.home.web.dto.PlayerDTO;

import java.util.List;
import java.util.Map;

public interface PlayerService {
    Player findPlayer(String name);

    Player findPlayerById(String id);

    List<Player> findPlayerByAge(int age);

    Map<Player, Address> getAllPlayers();

    List<Player> getPlayersFromTeam(int teamId);

    Player savePlayer(Player toSave);

    List<Player> savePlayerList(List<Player> toSaveList);

    boolean updatePlayer(Player toBe, String nameWhere);
    Player updatePlayerById(PlayerDTO toBe, String id);

    boolean savePlayerLivesIn(Player toSave, Address whereLive);

    boolean deletePlayerById(String playerId);
}
