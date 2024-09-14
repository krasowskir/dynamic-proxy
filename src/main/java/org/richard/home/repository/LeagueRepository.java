package org.richard.home.repository;

import org.richard.home.domain.League;

import java.util.List;

public interface LeagueRepository {

    League getLeagueById(String leagueId);

    League getLeagueByName(String leagueName);

    League getLeagueByCode(String code);

    List<League> getAllLeagues();
}
