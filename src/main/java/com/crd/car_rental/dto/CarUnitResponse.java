package com.crd.car_rental.dto;

import com.crd.car_rental.model.ReservationEntity;
import java.util.List;

public record CarUnitResponse(
        Long id,
        Long carEntityId,
        String vin,
        List<ReservationEntity> reservations) {

}
