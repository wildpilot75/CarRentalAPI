package com.crd.car_rental.dto;

import com.crd.car_rental.model.CarType;

public record CarResponse (
        Long id,
        String make,
        String model,
        CarType carType
)
{
}
