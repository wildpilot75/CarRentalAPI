package com.crd.car_rental.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CarSelectorValidator.class)
@Documented
public @interface ValidCarSelector {

    String message() default "Either carId or carType must be provided";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

