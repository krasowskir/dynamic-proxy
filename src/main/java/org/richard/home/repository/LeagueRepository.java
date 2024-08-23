package org.richard.home.repository;

import org.richard.home.domain.League;

public interface LeagueRepository {

    League getLeagueById(String leagueId);

    League getLeagueByName(String leagueName);

    League getLeagueByCode(String code);
}
