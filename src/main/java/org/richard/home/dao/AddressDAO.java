package org.richard.home.dao;

import org.richard.home.domain.Address;
import org.richard.home.infrastructure.exception.DatabaseAccessFailed;

public interface AddressDAO {
    String FIND_ADDRESS_BY_ID = "SELECT * FROM ADDRESSES WHERE ID = ?";
    String SAVE_ADDRESS = "INSERT INTO ADDRESSES VALUES (default, ?, ?, ?, ?)";

    Address getAddress(long id) throws DatabaseAccessFailed;

    int saveAddress(Address toSave) throws DatabaseAccessFailed;

    boolean updateAddress(Address toBe, long whereId) throws DatabaseAccessFailed;

    boolean deleteAddress(long whereId) throws DatabaseAccessFailed;
}
