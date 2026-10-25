package com.crd.car_rental.dto;

import com.crd.car_rental.validation.ValidCarSelector;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import com.crd.car_rental.model.CarType;
import com.crd.car_rental.validation.ValidReservationDates;

import java.util.Date;

@ValidCarSelector
@ValidReservationDates
public record ReservationCreateByCarRequest(
        Long carId,
        CarType carType,

        @NotNull
        @FutureOrPresent(message = "Start date must be today or in the future")
        Date startDate,

        @NotNull
        @Future(message = "End date must be in the future")
        Date endDate
) {}
