package org.richard.home.infrastructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PlayerValidationService implements VerifyAge {
    private static final Logger log = LoggerFactory.getLogger(PlayerValidationService.class);

    public PlayerValidationService() {
    }

    @Override
    public void verifyAge(int a1, int a2) {
        log.info("age verification passed. Age 1: {}, Age 2: {}", a1, a2);
    }
}
