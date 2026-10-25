package com.crd.car_rental.service;

import com.crd.car_rental.dto.*;
import com.crd.car_rental.error.BadRequestException;
import com.crd.car_rental.error.NotFoundException;
import com.crd.car_rental.error.ReservationOverlapException;
import com.crd.car_rental.model.CarEntity;
import com.crd.car_rental.model.CarType;
import com.crd.car_rental.model.CarUnitEntity;
import com.crd.car_rental.model.ReservationEntity;
import com.crd.car_rental.repository.*;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final CarUnitRepository carUnitRepository;
    private final CarRepository  carRepository;

    public ReservationService(
            ReservationRepository reservationRepository,
            CarUnitRepository carUnitRepository,
            CarRepository carRepository
    ) {
        this.reservationRepository = reservationRepository;
        this.carUnitRepository = carUnitRepository;
        this.carRepository = carRepository;
    }

    public List<ReservationDetailsResponse> getAll() {
        return reservationRepository.findAll()
                .stream()
                .map(this::mapToDetails)
                .toList();
    }

    public ReservationDetailsResponse getById(Long id) {
        return reservationRepository.findById(id)
                .map(this::mapToDetails)
                .orElseThrow(() -> new NotFoundException("Reservation with id " + id + " does not exist"));
    }

    public ReservationResponse addReservation(@Valid ReservationCreateRequest request) {

        CarUnitEntity unit = carUnitRepository.findById(request.carUnitId())
                .orElseThrow(() -> new NotFoundException("Car unit not found"));

        List<ReservationEntity> overlaps =
                reservationRepository.findOverlappingReservationsForUnit(
                        unit.getId(),
                        request.startDate(),
                        request.endDate()
                );

        if (!overlaps.isEmpty()) {
            throw new ReservationOverlapException(
                    "Car unit " + unit.getId() + " has conflicting reservations: " +
                            overlaps.stream().map(ReservationEntity::getId).toList()
            );
        }

        ReservationEntity entity = new ReservationEntity();
        entity.setCarUnit(unit);
        entity.setStartDate(request.startDate());
        entity.setEndDate(request.endDate());

        ReservationEntity saved = reservationRepository.save(entity);
        return mapToResponse(saved);
    }

    public ReservationResponse createReservationByCar(@Valid ReservationCreateByCarRequest request) {
        if (request.carId() != null) {
            List<CarUnitEntity> units = carUnitRepository.findByCar_Id(request.carId());
            return createReservationOnFirstAvailableUnit(units, request.startDate(), request.endDate());
        }

        if (request.carType() != null) {
            List<CarEntity> cars = carRepository.getByCarType(request.carType());
            List<Long> carIds = cars.stream().map(CarEntity::getId).toList();
            List<CarUnitEntity> units = carUnitRepository.findByCar_IdIn(carIds);

            return createReservationOnFirstAvailableUnit(units, request.startDate(), request.endDate());
        }

        throw new BadRequestException("You must provide carId, or carType");
    }

    private ReservationResponse createReservationOnFirstAvailableUnit(
            List<CarUnitEntity> units,
            Date startDate,
            Date endDate) {
        for (CarUnitEntity unit : units) {
            List<ReservationEntity> overlaps =
                    reservationRepository.findOverlappingReservationsForUnit(unit.getId(), startDate, endDate);

            if (overlaps.isEmpty()) {
                ReservationEntity entity = new ReservationEntity();
                entity.setCarUnit(unit);
                entity.setStartDate(startDate);
                entity.setEndDate(endDate);
                return mapToResponse(reservationRepository.save(entity));
            }
        }

        throw new ReservationOverlapException("No available units for the selected dates");
    }

    public ReservationResponse updateReservation(Long id, @Valid ReservationUpdateRequest request) {

        ReservationEntity entity = reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Reservation with id " + id + " does not exist"));

        CarUnitEntity unit = carUnitRepository.findById(request.carUnitId())
                .orElseThrow(() -> new NotFoundException("Car unit not found"));

        List<ReservationEntity> overlaps =
                reservationRepository.findOverlappingReservationsForUnit(
                        unit.getId(),
                        request.startDate(),
                        request.endDate()
                );

        if (!overlaps.isEmpty()) {
            throw new ReservationOverlapException(
                    "Car unit " + unit.getId() + " has conflicting reservations: " +
                            overlaps.stream().map(ReservationEntity::getId).toList()
            );
        }

        entity.setCarUnit(unit);
        entity.setStartDate(request.startDate());
        entity.setEndDate(request.endDate());

        return mapToResponse(reservationRepository.save(entity));
    }

    public void deleteReservation(Long id) {
        if (!reservationRepository.existsById(id)) {
            throw new NotFoundException("Reservation with id " + id + " does not exist");
        }
        reservationRepository.deleteById(id);
    }

    public List<ReservationDetailsResponse> getByCarUnit(Long carUnitId) {
        return reservationRepository.findByCarUnit_Id(carUnitId)
                .stream()
                .map(this::mapToDetails)
                .toList();
    }

    public List<ReservationDetailsResponse> getByCarEntity(Long carEntityId) {
        return reservationRepository.findByCarUnit_Car_Id(carEntityId)
                .stream()
                .map(this::mapToDetails)
                .toList();
    }


    public List<ReservationDetailsResponse> getByCarType(CarType carType) {
        return reservationRepository.findByCarUnit_Car_CarType(carType)
                .stream()
                .map(this::mapToDetails)
                .toList();
    }

    public AvailabilityResponse checkUnitAvailability(Long carUnitId, Date startDate, Date endDate) {

        List<ReservationEntity> overlaps =
                reservationRepository.findOverlappingReservationsForUnit(carUnitId, startDate, endDate);

        boolean available = overlaps.isEmpty();

        return new AvailabilityResponse(
                available ? 1 : 0,
                available ? List.of(carUnitId) : List.of()
        );
    }

    public AvailabilityResponse checkEntityAvailability(Long carEntityId, Date startDate, Date endDate) {

        List<CarUnitEntity> units = carUnitRepository.findByCar_Id(carEntityId);

        List<Long> availableUnits = units.stream()
                .filter(unit -> reservationRepository
                        .findOverlappingReservationsForUnit(unit.getId(), startDate, endDate)
                        .isEmpty())
                .map(CarUnitEntity::getId)
                .toList();

        return new AvailabilityResponse(
                availableUnits.size(),
                availableUnits
        );
    }

    public AvailabilityResponse checkEntityAvailability(CarType carType, Date startDate, Date endDate) {

        List<CarEntity> cars = carRepository.getByCarType(carType);
        List<Long> carIds = cars.stream().map(CarEntity::getId).toList();

        List<CarUnitEntity> units = carUnitRepository.findByCar_IdIn(carIds);

        List<Long> availableUnits = units.stream()
                .filter(unit -> reservationRepository
                        .findOverlappingReservationsForUnit(unit.getId(), startDate, endDate)
                        .isEmpty())
                .map(CarUnitEntity::getId)
                .toList();

        return new AvailabilityResponse(
                availableUnits.size(),
                availableUnits
        );
    }

    private ReservationResponse mapToResponse(ReservationEntity entity) {
        return new ReservationResponse(
                entity.getId(),
                entity.getCarUnit().getId(),
                entity.getStartDate(),
                entity.getEndDate()
        );
    }

    private ReservationDetailsResponse mapToDetails(ReservationEntity entity) {
        CarUnitEntity unit = entity.getCarUnit();
        CarEntity car = unit.getCar();

        return new ReservationDetailsResponse(
                entity.getId(),
                unit.getId(),
                unit.getVin(),
                car.getId(),
                car.getMake(),
                car.getModel(),
                car.getCarType(),
                entity.getStartDate(),
                entity.getEndDate()
        );
    }
}
