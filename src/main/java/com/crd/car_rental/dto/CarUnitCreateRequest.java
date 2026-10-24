package com.crd.car_rental.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CarUnitCreateRequest(
        @NotNull Long carId,
        @NotBlank String vin) {
}
