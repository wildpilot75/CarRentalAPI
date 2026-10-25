package com.crd.car_rental.validation;

import com.crd.car_rental.dto.ReservationCreateByCarRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CarSelectorValidator implements ConstraintValidator<ValidCarSelector, ReservationCreateByCarRequest> {

    @Override
    public boolean isValid(ReservationCreateByCarRequest request, ConstraintValidatorContext context) {

        boolean hasCarId = request.carId() != null;
        boolean hasCarType = request.carType() != null;

        // At least one must be provided
        return hasCarId || hasCarType;
    }
}
