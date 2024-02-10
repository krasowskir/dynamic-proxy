package org.richard.home.infrastructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PlayerService implements VerifyAge {
    private static Logger log = LoggerFactory.getLogger(PlayerService.class);

    public PlayerService() {
    }

    @Override
    public void verifyAge(int a1, int a2) {
        log.info("age verification passed. Age 1: {}, Age 2: {}", a1, a2);
    }
}
