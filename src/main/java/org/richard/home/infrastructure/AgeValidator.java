package org.richard.home.infrastructure;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.constraintvalidation.SupportedValidationTarget;
import jakarta.validation.constraintvalidation.ValidationTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SupportedValidationTarget({ValidationTarget.PARAMETERS})
public class AgeValidator implements ConstraintValidator<AgeMustBeAtLeastEighteen, Object[]> {
    private static Logger log = LoggerFactory.getLogger(AgeValidator.class);
    @Override
    public boolean isValid(Object[] value, ConstraintValidatorContext context) {
        log.info("validator called...");
        if((Integer)value[0] + (Integer)value[1] < 18){
            throw new IllegalArgumentException("age must be greater than 18");
        };
        return true;
    }
}
