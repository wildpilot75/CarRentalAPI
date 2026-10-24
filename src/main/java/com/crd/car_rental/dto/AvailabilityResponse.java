package com.crd.car_rental.dto;

import java.util.List;

public record AvailabilityResponse(
        int availableUnits,
        List<Long> availableCarUnitIds
) {}
