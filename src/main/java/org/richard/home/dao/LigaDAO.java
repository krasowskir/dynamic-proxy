package org.richard.home.dao;

import org.richard.home.domain.League;
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
public class LigaDAO {
    static final String FETCH_LEAGUES = "SELECT * FROM league";
    private static final Logger log = LoggerFactory.getLogger(LigaDAO.class);
    private final DataSource master;

    @Autowired
    public LigaDAO(@Qualifier("hikariDataSource") DataSource master) {
        this.master = master;
    }

    public List<League> selectAllLeagues() throws SQLException {
        log.debug("entering selectAllLeagues");
        try (Connection con = master.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement statement = con.prepareStatement(FETCH_LEAGUES, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
                log.debug("connection could be established");
                ResultSet rs = statement.executeQuery();
                List<League> leagues = mapResultSetToLeagues(rs);
                rs.close();
                con.commit();
                return leagues;
            }
        }
    }

    private List<League> mapResultSetToLeagues(ResultSet rs) {
        try {
            if (!rs.next()) {
                log.error("no resultset");
                throw new NotFoundException("no leagues found!");
            } else {
                log.debug("query contained leagues");
                List<League> leagues = new ArrayList<>();
                do {
                    League tmpLeague = new League();
                    tmpLeague.setId(rs.getInt(1));
                    tmpLeague.setName(rs.getString(2));
                    tmpLeague.setCode(rs.getString(3));
                    leagues.add(tmpLeague);
                } while (rs.next());
                return leagues;
            }
        } catch (SQLException e) {
            log.error("exception while converting the results", e);
            log.debug("returning empty list");
            return List.of();
        }
    }
}

