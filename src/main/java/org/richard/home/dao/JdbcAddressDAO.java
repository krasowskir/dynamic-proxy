package org.richard.home.dao;

import org.richard.home.domain.Address;
import org.richard.home.domain.Country;
import org.richard.home.infrastructure.exception.DatabaseAccessFailed;
import org.richard.home.infrastructure.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;

@Component
public class JdbcAddressDAO implements AddressDAO {

    private static final Logger log = LoggerFactory.getLogger(JdbcAddressDAO.class);

    private final DataSource writeDataSource;

    public DataSource getWriteDataSource() {
        return writeDataSource;
    }

    @Autowired
    public JdbcAddressDAO(@Qualifier("hikariDataSource") DataSource writeOnly) {
        this.writeDataSource = writeOnly;
    }

    @Override
    public Address getAddress(long id) throws DatabaseAccessFailed {
        try (Connection con = writeDataSource.getConnection()) {
            try (PreparedStatement pS = con.prepareStatement(FIND_ADDRESS_BY_ID)) {
                pS.setLong(1, id);
                ResultSet rs = pS.executeQuery();
                return mapResultSetToAddress(rs, id);
            }
        } catch (SQLException e) {
            log.error("error while getting the address", e);
            throw new DatabaseAccessFailed("error while getting the address", e);
        } catch (NotFoundException e) {
            log.error("error while getting the address", e);
            throw new NotFoundException(String.format("error while getting the address with id %d", id), e);
        }
    }

    @Override
    public int saveAddress(Address toSave) throws DatabaseAccessFailed {
        try (Connection con = writeDataSource.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement pS = con.prepareStatement(SAVE_ADDRESS, Statement.RETURN_GENERATED_KEYS)) {
                pS.setString(1, toSave.getCity());
                pS.setString(2, toSave.getStreet());
                pS.setString(3, toSave.getPlz());
                pS.setString(4, toSave.getCountry().toString());
                pS.executeUpdate();
                con.commit();
                log.debug("update successfule");
                try (ResultSet rsGenKeys = pS.getGeneratedKeys()) {
                    if (rsGenKeys.next()) {
                        return rsGenKeys.getInt(1);
                    } else {
                        throw new SQLException("no id obtained!");
                    }
                }
            }
        } catch (SQLException e) {
            log.error("error while saving address");
            throw new DatabaseAccessFailed("error while saving address", e);
        }
    }

    @Override
    public boolean updateAddress(Address toBe, long whereId) throws DatabaseAccessFailed {
        return false;
    }

    @Override
    public boolean deleteAddress(long whereId) throws DatabaseAccessFailed {
        return false;
    }

    private Address mapResultSetToAddress(ResultSet rs, long id) throws SQLException {
        if (!rs.next()) {
            throw new NotFoundException(String.format("address with id %s not found!", id));
        }
        return new Address(rs.getInt(1), rs.getString(2), rs.getString(3),
                rs.getString(4), Country.valueOf(rs.getString(5)));
    }
}
