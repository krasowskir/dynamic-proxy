package org.richard.home.repository;

import org.richard.home.domain.Team;

import java.util.Optional;

public interface TeamRepository {

    Team getTeamOfPlayer(String playerId);

    Optional<String> getLogoOfTeam(String teamId);
}
