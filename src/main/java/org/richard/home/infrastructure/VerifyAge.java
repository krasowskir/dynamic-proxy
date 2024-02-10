package org.richard.home.infrastructure;

import org.springframework.validation.annotation.Validated;

@Validated
public interface VerifyAge {
    @AgeMustBeAtLeastEighteen
    void verifyAge(int a1, int a2);
}
