package org.richard.home.service;

import jakarta.persistence.NoResultException;
import org.richard.home.domain.Team;
import org.richard.home.infrastructure.exception.LeagueDoesNotExistException;
import org.richard.home.service.dto.TeamDTO;

public interface TeamService {

    Team createTeam(TeamDTO fromTeam) throws LeagueDoesNotExistException;

    boolean deleteTeam(String id);

    Team findTeamById(String id) throws NoResultException;

    Team updateTeam(String teamId, TeamDTO toTeam) throws LeagueDoesNotExistException;

    Team getCurrentTeamOfPlayer(String playerId);


}
