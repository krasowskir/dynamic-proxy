package org.richard.home.service;

import org.richard.home.domain.League;
import org.richard.home.web.dto.LeagueDTO;

public interface LeagueService {

    League createLeague(LeagueDTO leagueDTO);

    League getLeague(String id);
    League getLeagueByName(String name);
    League getLeagueByCode(String code);

    boolean deleteLeague(String id);

    League updateLeague(String id, LeagueDTO toBe);
}
