package org.richard.home.service;

import jakarta.persistence.NoResultException;
import org.richard.home.domain.Team;
import org.richard.home.infrastructure.exception.LeagueDoesNotExistException;
import org.richard.home.web.dto.TeamDto;

public interface TeamService {

    Team createTeam(TeamDto fromTeam) throws LeagueDoesNotExistException;

    boolean deleteTeam(String id);

    Team findTeamById(String id) throws NoResultException;

    Team updateTeam(String teamId, TeamDto toTeam) throws LeagueDoesNotExistException;

    Team getCurrentTeamOfPlayer(String playerId);


}
