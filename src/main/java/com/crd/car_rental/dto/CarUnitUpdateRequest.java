package com.crd.car_rental.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CarUnitUpdateRequest(
        @NotNull Long carEntityId,
        @NotBlank String vin) {
}
