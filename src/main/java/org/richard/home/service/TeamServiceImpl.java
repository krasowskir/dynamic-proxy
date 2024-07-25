package org.richard.home.service;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.richard.home.domain.Team;
import org.richard.home.web.dto.TeamDto;
import org.richard.home.web.mapper.TeamMapper;

public class TeamServiceImpl implements TeamService {

    private EntityManagerFactory entityManagerFactory;
    private TeamMapper teamMapper;

    public TeamServiceImpl(EntityManagerFactory entityManagerFactory, TeamMapper teamMapper) {
        this.entityManagerFactory = entityManagerFactory;
        this.teamMapper = teamMapper;
    }

    @Override
    public Team createTeam(TeamDto fromTeam) {
        Team team = teamMapper.mapToTeam(fromTeam);
        var entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager.persist(team);
        transaction.commit();
        return team;
    }
}
