package org.richard.home.service;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.richard.home.domain.League;
import org.richard.home.domain.Team;
import org.richard.home.infrastructure.exception.LeagueDoesNotExistException;
import org.richard.home.web.dto.TeamDto;
import org.richard.home.web.mapper.TeamMapper;

import java.util.Optional;

public class TeamServiceImpl implements TeamService {

    private EntityManagerFactory entityManagerFactory;
    private TeamMapper teamMapper;

    public TeamServiceImpl(EntityManagerFactory entityManagerFactory, TeamMapper teamMapper) {
        this.entityManagerFactory = entityManagerFactory;
        this.teamMapper = teamMapper;
    }

    @Override
    public Team createTeam(TeamDto fromTeam) throws LeagueDoesNotExistException {
        Team team = teamMapper.mapToTeam(fromTeam);
        var entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        var foundLeague = Optional.ofNullable(entityManager.find(League.class, fromTeam.getLeagueId())).orElseThrow(() -> new LeagueDoesNotExistException("leaggue id: " + fromTeam.getLeagueId()));
        team.setLeague(foundLeague);
        entityManager.persist(team);
        transaction.commit();
        return team;
    }
}
