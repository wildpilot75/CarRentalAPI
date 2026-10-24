package com.crd.car_rental.dto;

import java.util.Date;

public record ReservationResponse (
        Long id,
        Long carUnitId,
        Date startDate,
        Date endDate) {}
