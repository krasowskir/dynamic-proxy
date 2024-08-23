package org.richard.home.service.mapper;

import org.richard.home.domain.Country;
import org.richard.home.domain.Player;
import org.richard.home.service.DomainMapper;
import org.richard.home.service.dto.PlayerDTO;

public class PlayerMapper implements DomainMapper<Player, PlayerDTO> {

    @Override
    public Player mapFromDomain(PlayerDTO playerDTO) {
        return new Player(
                playerDTO.getName(),
                playerDTO.getAge(),
                playerDTO.getPosition(),
                playerDTO.getDateOfBirth(),
                Country.valueOf(playerDTO.getCountryOfBirth().getValue()),
                null);
    }

}
