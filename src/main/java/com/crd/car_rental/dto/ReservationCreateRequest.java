package com.crd.car_rental.dto;

import com.crd.car_rental.validation.ValidReservationDates;
import jakarta.validation.constraints.*;

import java.util.Date;

@ValidReservationDates
public record ReservationCreateRequest (
        @NotNull
        Long carUnitId,

        @NotNull
        @FutureOrPresent(message = "Start date must be today or in the future")
        Date startDate,

        @NotNull
        @Future(message = "End date must be in the future")
        Date endDate
) { }
