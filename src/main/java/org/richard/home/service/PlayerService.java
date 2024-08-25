package org.richard.home.service;

import org.richard.home.domain.Address;
import org.richard.home.domain.Player;
import org.richard.home.domain.Team;
import org.richard.home.service.dto.PlayerDTO;

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

    Player updatePlayerById(Map<String, PlayerDTO> toBe);

    boolean savePlayerLivesIn(Player toSave, Address whereLive);

    boolean deletePlayerById(String playerId);

    Player updateTeamOfPlayer(String playerId, String newTeamId);

    void deletePlayersContract(String playerId, String teamId);
}
