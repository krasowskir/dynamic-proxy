package org.richard.home.service.dto.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.richard.home.service.dto.AddressDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddressValidator implements ConstraintValidator<ValidAddress, AddressDTO> {
    private static final Logger log = LoggerFactory.getLogger(AgeValidator.class);

    @Override
    public boolean isValid(AddressDTO value, ConstraintValidatorContext context) {
        if (value == null
                || value.getCity() == null || value.getCity().trim().equals("")
                || value.getCountry() == null || value.getCountry().getValue().trim().equals("")
                || value.getStreet() == null || value.getStreet().trim().equals("")) {
            return false;
        }
        return true;
    }
}
