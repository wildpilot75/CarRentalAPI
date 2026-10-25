package com.crd.car_rental.validation;

import com.crd.car_rental.dto.ReservationCreateByCarRequest;
import com.crd.car_rental.dto.ReservationCreateRequest;
import com.crd.car_rental.dto.ReservationUpdateRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Date;

public class ReservationDatesValidator implements ConstraintValidator<ValidReservationDates, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {

        Date start;
        Date end;

        if (value instanceof ReservationCreateRequest r) {
            start = r.startDate();
            end = r.endDate();
        } else if (value instanceof ReservationCreateByCarRequest r) {
            start = r.startDate();
            end = r.endDate();
        } else if (value instanceof ReservationUpdateRequest r) {
            start = r.startDate();
            end = r.endDate();
        } else {
            // Unsupported type → do not validate
            return true;
        }

        if (start == null || end == null) {
            return true; // @NotNull handles this
        }

        return end.after(start);
    }
}
