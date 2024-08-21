package org.richard.home.repository;

import org.richard.home.domain.Team;

public interface TeamRepository {

    Team getTeamOfPlayer(String playerId);
}
