package org.richard.home.service;

import org.richard.home.domain.Team;
import org.richard.home.web.dto.TeamDto;

public interface TeamService {

    Team createTeam(TeamDto fromTeam);
}
