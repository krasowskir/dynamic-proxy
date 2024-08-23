package org.richard.home.dao;

import org.richard.home.domain.Team;
import org.richard.home.domain.Trainer;
import org.richard.home.infrastructure.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class TeamDAO {

    static final String FETCH_TEAMS = "SELECT id, name, budget, logo, owner FROM TEAMS";
    static final String FETCH_TEAM_BY_ID = "SELECT * FROM TEAMS WHERE ID = ?";
    static final String FETCH_TRAINER_BY_TEAM_ID = "SELECT tr.* FROM TRAINERS TR inner JOIN TEAMS TE ON TR.WYID = TE.WYID WHERE TE.ID = ?";
    static final String FETCH_TEAMS_BY_LEAGUE = "SELECT id, name, budget, logo, owner FROM TEAMS where league_id = ?";
    private static final Logger log = LoggerFactory.getLogger(TeamDAO.class);
    private final DataSource master;

    @Autowired
    public TeamDAO(@Qualifier("hikariDataSource") DataSource master) {
        this.master = master;
    }

    public List<Team> selectAllTeams() throws SQLException {
        log.debug("entering selectAllTeams");
        try (Connection con = master.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement statement = con.prepareStatement(FETCH_TEAMS, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
                log.debug("connection could be established");
                ResultSet rs = statement.executeQuery();
                List<Team> teams = mapResultSetToTeams(rs);
                rs.close();
                con.commit();
                return teams;
            }
        }
    }

    public List<Team> fetchTeamsByLeagueId(String leagueId) throws SQLException {
        log.debug("entering fetchTeamsByLeagueId");
        try (Connection con = master.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement pS = con.prepareStatement(FETCH_TEAMS_BY_LEAGUE, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
                log.debug("connection could be established");
                pS.setInt(1, Integer.parseInt(leagueId));
                ResultSet rs = pS.executeQuery();
                List<Team> teams = mapResultSetToTeams(rs);
                rs.close();
                con.commit();
                return teams;
            }
        }
    }

    public Team getTeamById(String teamId) throws SQLException {
        log.debug("entering getTeamById");
        try (Connection con = master.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement preparedStatement = con.prepareStatement(FETCH_TEAM_BY_ID, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
                log.debug("connection could be established");
                preparedStatement.setInt(1, Integer.parseInt(teamId));
                ResultSet rs = preparedStatement.executeQuery();
                Team foundTeam = mapResultSetToSingleTeam(rs);
                rs.close();
                con.commit();
                return foundTeam;
            }
        }
    }

    public Trainer getTrainerFromTeam(String teamId) throws SQLException {
        log.debug("entering getTrainerFromTeam with teamId {}", teamId);
        try (Connection con = master.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement preparedStatement = con.prepareStatement(FETCH_TRAINER_BY_TEAM_ID, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
                log.debug("connection could be established");
                preparedStatement.setInt(1, Integer.parseInt(teamId));
                ResultSet rs = preparedStatement.executeQuery();
                Trainer foundTrainer = mapResultSetToSingleTrainer(rs);
                rs.close();
                con.commit();
                return foundTrainer;
            }
        }
    }

    private List<Team> mapResultSetToTeams(ResultSet rs) {
        try {
            if (!rs.next()) {
                log.error("no resultset");
                throw new NotFoundException("no teams found!");
            } else {
                log.debug("query contained teams");
                List<Team> teams = new ArrayList<>();
                do {
                    Team tmpTeam = new Team();
                    tmpTeam.setId(rs.getInt(1));
                    tmpTeam.setName(rs.getString(2));
                    tmpTeam.setBudget(rs.getInt(3));
                    tmpTeam.setLogo(rs.getString(4));
                    tmpTeam.setOwner(rs.getString(5));
                    teams.add(tmpTeam);
                } while (rs.next());
                return teams;
            }
        } catch (SQLException e) {
            log.error("exception while converting the results", e);
            log.debug("returning empty list");
            return List.of();
        }
    }

    private Team mapResultSetToSingleTeam(ResultSet rs) {
        try {
            if (!rs.next()) {
                log.error("no resultset");
                throw new NotFoundException("no teams found!");
            } else {
                log.debug("query contained teams");

                Team tmpTeam = new Team();
                tmpTeam.setId(rs.getInt(1));
                tmpTeam.setName(rs.getString(2));
                tmpTeam.setBudget(rs.getInt(3));
                tmpTeam.setLogo(rs.getString(4));
                tmpTeam.setOwner(rs.getString(5));
                tmpTeam.setTla(rs.getString(6));
//                tmpTeam.setAddress(rs.getString(7));
                tmpTeam.setPhone(rs.getString(8));
                tmpTeam.setWebsite(rs.getString(9));
                tmpTeam.setEmail(rs.getString(10));
                tmpTeam.setVenue(rs.getString(11));
                return tmpTeam;
            }
        } catch (SQLException e) {
            log.error("exception while converting the results", e);
            log.debug("returning null");
            return null;
        }
    }

    private Trainer mapResultSetToSingleTrainer(ResultSet rs) {
        try {
            if (!rs.next()) {
                log.error("no resultset");
                throw new NotFoundException("no trainer found!");
            } else {
                log.debug("query contained teams");
                return new Trainer(rs.getInt("wyid"), rs.getString("shortname"),
                        rs.getString("country"), rs.getString("birthdate"));

            }
        } catch (SQLException e) {
            log.error("exception while converting the results", e);
            log.debug("returning null");
            return null;
        }
    }
}
