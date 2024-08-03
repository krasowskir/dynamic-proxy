package org.richard.home.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import org.richard.home.domain.League;
import org.richard.home.domain.Team;
import org.richard.home.infrastructure.exception.LeagueDoesNotExistException;
import org.richard.home.web.dto.TeamDto;
import org.richard.home.web.mapper.TeamMapper;

import java.util.Objects;
import java.util.Optional;

public class JpaTeamService implements TeamService {

    private EntityManagerFactory entityManagerFactory;
    private TeamMapper teamMapper;

    public JpaTeamService(EntityManagerFactory entityManagerFactory, TeamMapper teamMapper) {
        this.entityManagerFactory = entityManagerFactory;
        this.teamMapper = teamMapper;
    }

    private static void updateTeamAttributes(TeamDto toTeamDTO, EntityManager entityManager, Team foundTeam) {
        if (isNotNullOrEmpty(toTeamDTO.getLeagueId()))
            foundTeam.setLeague(entityManager.find(League.class, toTeamDTO.getLeagueId()));
        if (toTeamDTO.getWyId() != 0) foundTeam.setWyId(toTeamDTO.getWyId());
        if (isNotNullOrEmpty(toTeamDTO.getWebsite())) foundTeam.setWebsite(toTeamDTO.getWebsite());
        if (isNotNullOrEmpty(toTeamDTO.getTla())) foundTeam.setTla(toTeamDTO.getTla());
        if (isNotNullOrEmpty(toTeamDTO.getVenue())) foundTeam.setVenue(toTeamDTO.getVenue());
        if (isNotNullOrEmpty(toTeamDTO.getPhone())) foundTeam.setPhone(toTeamDTO.getPhone());
        if (isNotNullOrEmpty(toTeamDTO.getLogo())) foundTeam.setLogo(toTeamDTO.getLogo());
        if (isNotNullOrEmpty(toTeamDTO.getName())) foundTeam.setName(toTeamDTO.getName());
        if (isNotNullOrEmpty(toTeamDTO.getEmail())) foundTeam.setEmail(toTeamDTO.getEmail());
        if (isNotNullOrEmpty(toTeamDTO.getAddress().toString()))
            foundTeam.setAddress(toTeamDTO.getAddress().toString());
    }

    public static boolean isNotNullOrEmpty(String value) {
        return !(value == null || value.trim().length() == 0);
    }

    @Override
    public Team createTeam(TeamDto fromTeam) throws LeagueDoesNotExistException {
        Team team = teamMapper.mapToTeam(fromTeam);
        try (var entityManager = entityManagerFactory.createEntityManager();) {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();
            var foundLeague = Optional.ofNullable(entityManager.find(League.class, fromTeam.getLeagueId())).orElseThrow(() -> new LeagueDoesNotExistException("leaggue id: " + fromTeam.getLeagueId()));
            team.setLeague(foundLeague);
            entityManager.persist(team);
            transaction.commit();
        }
        return team;
    }

    // ToDo: rollback der Transaktion fehlt!
    @Override
    public boolean deleteTeam(String id) {
        Objects.requireNonNull(id.trim());
        try (var entityManager = entityManagerFactory.createEntityManager()) {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();
            entityManager.remove(entityManager.find(Team.class, id));
            transaction.commit();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Team findTeamById(String id) throws NoResultException {
        Objects.requireNonNull(id.trim());
        try (var entityManager = entityManagerFactory.createEntityManager()) {
            return entityManager.find(Team.class, id);
        }
    }

    // ToDo: rollback der Transaktion fehlt!
    @Override
    public Team updateTeam(String teamId, TeamDto toTeamDTO) {
        try (var entityManager = entityManagerFactory.createEntityManager()) {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();
            var foundTeam = entityManager.find(Team.class, teamId);
            updateTeamAttributes(toTeamDTO, entityManager, foundTeam);
            var updated = entityManager.merge(foundTeam);
            transaction.commit();
            return updated;
        }
    }
}
