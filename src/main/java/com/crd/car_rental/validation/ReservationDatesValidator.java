package com.crd.car_rental.validation;

import com.crd.car_rental.dto.ReservationCreateRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ReservationDatesValidator implements ConstraintValidator<ValidReservationDates, ReservationCreateRequest> {

    @Override
    public boolean isValid(ReservationCreateRequest request, ConstraintValidatorContext context) {

        if (request.startDate() == null || request.endDate() == null) {
            return true; // @NotNull handles this
        }

        // endDate must be strictly after startDate
        return request.endDate().after(request.startDate());
    }
}
