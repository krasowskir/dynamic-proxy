package org.richard.home.service.dto.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AgeValidator.class)
public @interface AgeMustBeAtLeastEighteen {

    String message() default "Age must be greater than 18";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
