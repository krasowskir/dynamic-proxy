package org.richard.home.service.mapper;

import org.richard.home.domain.Address;
import org.richard.home.domain.Country;
import org.richard.home.service.dto.AddressDTO;

import java.util.function.Function;

public class AddressMapper {
    public static Function<AddressDTO, Address> mapFromDTO = (fromAddressDTO) -> new Address(fromAddressDTO.getCity(), fromAddressDTO.getStreet(), fromAddressDTO.getPlz(), Country.valueOf(fromAddressDTO.getCountry().name()));
}
