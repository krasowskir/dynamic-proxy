package org.richard.home.web.mapper;

import org.richard.home.domain.Country;
import org.richard.home.domain.Player;
import org.richard.home.web.dto.PlayerDTO;

public class PlayerMapper {

    public static Player fromWebLayerTo(PlayerDTO playerDTO) {
        return new Player(
                playerDTO.getName(),
                playerDTO.getAge(),
                playerDTO.getPosition(),
                playerDTO.getDateOfBirth(),
                Country.valueOf(playerDTO.getCountryOfBirth().getValue()));
    }
}
