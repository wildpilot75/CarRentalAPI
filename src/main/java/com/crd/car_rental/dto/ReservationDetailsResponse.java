package com.crd.car_rental.dto;

import com.crd.car_rental.model.CarType;

import java.util.Date;

public record ReservationDetailsResponse(
        Long id,
        Long carUnitId,
        String vin,
        Long carId,
        String make,
        String model,
        CarType carType,
        Date startDate,
        Date endDate
) {}
