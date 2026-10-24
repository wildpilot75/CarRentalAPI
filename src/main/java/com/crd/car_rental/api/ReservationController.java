package com.crd.car_rental.api;

import com.crd.car_rental.dto.*;
import com.crd.car_rental.model.CarType;
import com.crd.car_rental.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {
    private final ReservationService reservationService;

    @Autowired
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public List<ReservationDetailsResponse> getAll() {

        return reservationService.getAll();
    }

    @GetMapping("/{id}")
    public ReservationDetailsResponse getById(@PathVariable Long id) {
        return reservationService.getById(id);
    }

    @PostMapping
    public ReservationResponse add(@Valid @RequestBody ReservationCreateRequest request) {
        return reservationService.addReservation(request);
    }

    @PostMapping("/by-car")
    public ReservationResponse createByCar(@Valid @RequestBody ReservationCreateByCarRequest request) {
        return reservationService.createReservationByCar(request);
    }

    @PutMapping("/{id}")
    public ReservationResponse update(
            @PathVariable Long id,
            @Valid @RequestBody ReservationUpdateRequest request) {
        return reservationService.updateReservation(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        reservationService.deleteReservation(id);
    }

    @GetMapping("/car-unit/{carUnitId}")
    public List<ReservationDetailsResponse> getByCarUnit(@PathVariable Long carUnitId) {
        return reservationService.getByCarUnit(carUnitId);
    }

    @GetMapping("/car-entity/{carEntityId}")
    public List<ReservationDetailsResponse> getByCarEntity(@PathVariable Long carEntityId) {
        return reservationService.getByCarEntity(carEntityId);
    }

    @GetMapping("/car-type/{carType}")
    public List<ReservationDetailsResponse> getByCarType(@PathVariable CarType carType) {
        return reservationService.getByCarType(carType);
    }

    @GetMapping("/car-unit/{carUnitId}/availability")
    public AvailabilityResponse checkUnitAvailability(
            @PathVariable Long carUnitId,
            @RequestParam Date startDate,
            @RequestParam Date endDate) {

        return reservationService.checkUnitAvailability(carUnitId, startDate, endDate);
    }

    @GetMapping("/car-entity/{carEntityId}/availability")
    public AvailabilityResponse checkEntityAvailability(
            @PathVariable Long carEntityId,
            @RequestParam Date startDate,
            @RequestParam Date endDate) {

        return reservationService.checkEntityAvailability(carEntityId, startDate, endDate);
    }

    @GetMapping("/car-type/{carType}/availability")
    public AvailabilityResponse checkEntityAvailability(
            @PathVariable CarType carType,
            @RequestParam Date startDate,
            @RequestParam Date endDate) {

        return reservationService.checkEntityAvailability(carType, startDate, endDate);
    }
}
