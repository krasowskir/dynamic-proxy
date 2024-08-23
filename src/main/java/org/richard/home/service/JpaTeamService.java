package org.richard.home.service;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.RollbackException;
import org.richard.home.domain.League;
import org.richard.home.domain.Team;
import org.richard.home.infrastructure.exception.LeagueDoesNotExistException;
import org.richard.home.repository.TeamRepository;
import org.richard.home.service.dto.TeamDto;
import org.richard.home.service.mapper.TeamMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Optional;

public class JpaTeamService implements TeamService {

    private static Logger log = LoggerFactory.getLogger(JpaTeamService.class);
    private EntityManagerFactory entityManagerFactory;
    private TeamRepository teamRepository;
    private TeamMapper teamMapper;

    public JpaTeamService(EntityManagerFactory entityManagerFactory, TeamMapper teamMapper, TeamRepository teamRepository) {
        this.entityManagerFactory = entityManagerFactory;
        this.teamMapper = teamMapper;
        this.teamRepository = teamRepository;
    }

    private static void updateTeamAttributes(TeamDto toTeamDTO, Team foundTeam, League foundLeague) {
        foundTeam.setLeague(foundLeague);
        if (toTeamDTO.getWyId() != 0) foundTeam.setWyId(toTeamDTO.getWyId());
        if (isNotNullOrEmpty(toTeamDTO.getWebsite())) foundTeam.setWebsite(toTeamDTO.getWebsite());
        if (isNotNullOrEmpty(toTeamDTO.getTla())) foundTeam.setTla(toTeamDTO.getTla());
        if (isNotNullOrEmpty(toTeamDTO.getVenue())) foundTeam.setVenue(toTeamDTO.getVenue());
        if (isNotNullOrEmpty(toTeamDTO.getPhone())) foundTeam.setPhone(toTeamDTO.getPhone());
        if (isNotNullOrEmpty(toTeamDTO.getLogo())) foundTeam.setLogo(toTeamDTO.getLogo());
        if (isNotNullOrEmpty(toTeamDTO.getName())) foundTeam.setName(toTeamDTO.getName());
        if (isNotNullOrEmpty(toTeamDTO.getEmail())) foundTeam.setEmail(toTeamDTO.getEmail());
//        if (isNotNullOrEmpty(toTeamDTO.getAddress().toString())) {
//            foundTeam.setAddress(toTeamDTO.getAddress().toString());
//        }
    }

    public static boolean isNotNullOrEmpty(String value) {
        return !(value == null || value.trim().length() == 0);
    }

    @Override
    public Team createTeam(TeamDto fromTeam) throws LeagueDoesNotExistException {
        EntityTransaction transaction = null;
        Team team = teamMapper.mapFromDomain(fromTeam);
        try (var entityManager = entityManagerFactory.createEntityManager();) {
            transaction = entityManager.getTransaction();
            transaction.begin();
            var foundLeague = Optional.ofNullable(
                            entityManager.find(League.class, fromTeam.getLeagueId())
                    )
                    .orElseThrow(() -> new LeagueDoesNotExistException("league id: " + fromTeam.getLeagueId()));
            team.setLeague(foundLeague);
            entityManager.persist(team);
            transaction.commit();
        } catch (LeagueDoesNotExistException e) {
            log.error("Creation not successful! League could not be found! leagueId: {}", fromTeam.getLeagueId());
            transaction.rollback();
            throw e;
        }
        return team;
    }

    // ToDo: rollback der Transaktion fehlt!
    @Override
    public boolean deleteTeam(String id) {
        Objects.requireNonNull(id.trim());
        EntityTransaction transaction = null;
        try (var entityManager = entityManagerFactory.createEntityManager()) {
            transaction = entityManager.getTransaction();
            transaction.begin();
            entityManager.remove(entityManager.find(Team.class, id));
            transaction.commit();
            return true;
        } catch (Exception e) {
            transaction.rollback();
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
    public Team updateTeam(String teamId, TeamDto toTeamDTO) throws LeagueDoesNotExistException {
        EntityTransaction transaction = null;
        try (var entityManager = entityManagerFactory.createEntityManager()) {
            transaction = entityManager.getTransaction();
            transaction.begin();
            var foundTeam = entityManager.find(Team.class, teamId);
            handleNotExistingLeague(toTeamDTO.getLeagueId());
            var foundLeague = Objects.requireNonNull(
                    entityManager.find(League.class, toTeamDTO.getLeagueId()),
                    String.format("league with id: %s could not be found!", toTeamDTO.getLeagueId())
            );
            updateTeamAttributes(toTeamDTO, foundTeam, foundLeague);
            var updated = entityManager.merge(foundTeam);
            transaction.commit();
            return updated;
        } catch (NullPointerException e) {
            log.warn("league could not be found with id: {}", toTeamDTO.getLeagueId());
            transaction.rollback();
            throw new LeagueDoesNotExistException(
                    String.format("league could not be found with id: %s!",
                            toTeamDTO.getLeagueId()));
        } catch (RollbackException | IllegalArgumentException e) {
            log.error("transaction aborted! Will roll back!");
            transaction.rollback();
            throw e;
        }
    }

    @Override
    public Team getCurrentTeamOfPlayer(String playerId) {
        var validPlayerId = Optional.ofNullable(playerId)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(elem -> !elem.isEmpty())
                .orElseThrow(() -> new NullPointerException("playerId was null or empty!"));

        return teamRepository.getTeamOfPlayer(validPlayerId);
    }

    private void handleNotExistingLeague(String leagueId) {
        if (!isNotNullOrEmpty(leagueId)) {
            throw new NullPointerException("leagueId is null or empty!");
        }
    }
}
