package com.crd.car_rental.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import com.crd.car_rental.validation.ValidReservationDates;

import java.util.Date;

@ValidReservationDates
public record ReservationUpdateRequest(
        @NotNull
        Long carUnitId,

        @NotNull
        @FutureOrPresent(message = "Start date must be today or in the future")
        Date startDate,

        @NotNull
        @Future(message = "End date must be in the future")
        Date endDate
) {}
