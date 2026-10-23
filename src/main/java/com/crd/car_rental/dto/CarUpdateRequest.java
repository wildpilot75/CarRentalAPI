package com.crd.car_rental.dto;

import com.crd.car_rental.model.CarType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CarUpdateRequest (
        @NotBlank String make,
        @NotBlank String model,
        @NotNull CarType carType) { }
