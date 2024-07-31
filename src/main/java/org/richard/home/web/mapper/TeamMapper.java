package org.richard.home.web.mapper;

import org.richard.home.domain.Team;
import org.richard.home.web.dto.TeamDto;

public class TeamMapper {

    public Team mapToTeam(TeamDto fromTeamDTO) {
        var team = new Team();
        team.setName(fromTeamDTO.getName());
        team.setBudget(fromTeamDTO.getBudget());
        team.setLogo(fromTeamDTO.getLogo());
        team.setOwner(fromTeamDTO.getOwner());
        team.setEmail(fromTeamDTO.getEmail());
        team.setPhone(fromTeamDTO.getPhone());
        team.setTla(fromTeamDTO.getTla());
        team.setVenue(fromTeamDTO.getVenue());
        team.setAddress(fromTeamDTO.getAddress().toString());
        team.setWebsite(fromTeamDTO.getWebsite());
//        team.setLeague(fromTeamDTO.getLeagueId()); // liga komplexes OBjekt!
        team.setWyId(fromTeamDTO.getWyId());
        return team;
    }
}
