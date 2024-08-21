package org.richard.home.web.mapper;

import org.richard.home.domain.Country;
import org.richard.home.domain.Player;
import org.richard.home.web.DomainMapper;
import org.richard.home.web.dto.PlayerDTO;

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
