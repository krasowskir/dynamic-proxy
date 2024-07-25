package org.richard.home.infrastructure;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AddressValidator.class)
public @interface ValidAddress {

    String message() default "Address must have a city name, street, plz and a house number";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
