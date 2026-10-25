package com.crd.car_rental.service;

import com.crd.car_rental.dto.*;
import com.crd.car_rental.error.BadRequestException;
import com.crd.car_rental.error.NotFoundException;
import com.crd.car_rental.error.ReservationOverlapException;
import com.crd.car_rental.model.CarEntity;
import com.crd.car_rental.model.CarType;
import com.crd.car_rental.model.CarUnitEntity;
import com.crd.car_rental.model.ReservationEntity;
import com.crd.car_rental.repository.CarRepository;
import com.crd.car_rental.repository.CarUnitRepository;
import com.crd.car_rental.repository.ReservationRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservationServiceTest {

    private ReservationRepository reservationRepository;
    private CarUnitRepository carUnitRepository;
    private CarRepository carRepository;
    private ReservationService reservationService;

    @BeforeEach
    void setUp() {
        reservationRepository = mock(ReservationRepository.class);
        carUnitRepository = mock(CarUnitRepository.class);
        carRepository = mock(CarRepository.class);

        reservationService = new ReservationService(
                reservationRepository,
                carUnitRepository,
                carRepository
        );
    }

    // ---------------------------------------------------------
    // GET ALL
    // ---------------------------------------------------------
    @Test
    void shouldReturnAllReservations() {
        ReservationEntity r1 = new ReservationEntity();
        r1.setId(1L);
        CarUnitEntity u1 = new CarUnitEntity();
        CarEntity c1 = new CarEntity();
        c1.setId(100L);
        u1.setCar(c1);
        r1.setCarUnit(u1);

        when(reservationRepository.findAll()).thenReturn(List.of(r1));

        List<ReservationDetailsResponse> result = reservationService.getAll();

        assertThat(result)
                .hasSize(1)
                .first()
                .extracting(ReservationDetailsResponse::id)
                .isEqualTo(1L);
    }

    // ---------------------------------------------------------
    // GET BY ID
    // ---------------------------------------------------------
    @Test
    void shouldReturnReservationById() {
        ReservationEntity entity = new ReservationEntity();
        entity.setId(1L);

        CarUnitEntity unit = new CarUnitEntity();
        CarEntity car = new CarEntity();
        car.setId(10L);
        unit.setCar(car);
        entity.setCarUnit(unit);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(entity));

        ReservationDetailsResponse result = reservationService.getById(1L);

        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    void shouldThrowNotFoundWhenReservationDoesNotExist() {
        when(reservationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.getById(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Reservation with id 99 does not exist");
    }

    // ---------------------------------------------------------
    // ADD RESERVATION
    // ---------------------------------------------------------
    @Test
    void shouldAddReservationSuccessfully() {
        Date start = new Date();
        Date end = new Date(start.getTime() + 3600_000);

        CarUnitEntity unit = new CarUnitEntity();
        unit.setId(10L);

        when(carUnitRepository.findById(10L)).thenReturn(Optional.of(unit));
        when(reservationRepository.findOverlappingReservationsForUnit(10L, start, end))
                .thenReturn(List.of());

        ReservationEntity saved = new ReservationEntity();
        saved.setId(1L);
        saved.setCarUnit(unit);
        saved.setStartDate(start);
        saved.setEndDate(end);

        when(reservationRepository.save(any())).thenReturn(saved);

        ReservationCreateRequest request = new ReservationCreateRequest(10L, start, end);

        ReservationResponse result = reservationService.addReservation(request);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.carUnitId()).isEqualTo(10L);
    }

    @Test
    void shouldThrowOverlapExceptionWhenAddingOverlappedReservation() {
        Date start = new Date();
        Date end = new Date(start.getTime() + 3600_000);

        CarUnitEntity unit = new CarUnitEntity();
        unit.setId(10L);

        ReservationEntity overlap = new ReservationEntity();
        overlap.setId(999L);

        when(carUnitRepository.findById(10L)).thenReturn(Optional.of(unit));
        when(reservationRepository.findOverlappingReservationsForUnit(10L, start, end))
                .thenReturn(List.of(overlap));

        ReservationCreateRequest request = new ReservationCreateRequest(10L, start, end);

        assertThatThrownBy(() -> reservationService.addReservation(request))
                .isInstanceOf(ReservationOverlapException.class)
                .hasMessageContaining("conflicting reservations");
    }

    // ---------------------------------------------------------
    // CREATE RESERVATION BY CAR
    // ---------------------------------------------------------
    @Test
    void shouldCreateReservationByCarId() {
        Date start = new Date();
        Date end = new Date(start.getTime() + 3600_000);

        CarUnitEntity unit = new CarUnitEntity();
        unit.setId(10L);

        when(carUnitRepository.findByCar_Id(1L)).thenReturn(List.of(unit));
        when(reservationRepository.findOverlappingReservationsForUnit(10L, start, end))
                .thenReturn(List.of());

        ReservationEntity saved = new ReservationEntity();
        saved.setId(1L);
        saved.setCarUnit(unit);

        when(reservationRepository.save(any())).thenReturn(saved);

        ReservationCreateByCarRequest request =
                new ReservationCreateByCarRequest(1L, null, start, end);

        ReservationResponse result = reservationService.createReservationByCar(request);

        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    void shouldThrowBadRequestWhenCarIdAndCarTypeMissing() {
        ReservationCreateByCarRequest request =
                new ReservationCreateByCarRequest(null, null, new Date(), new Date());

        assertThatThrownBy(() -> reservationService.createReservationByCar(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("You must provide carId, or carType");
    }

    @Test
    void shouldThrowOverlapExceptionWhenNoUnitsAvailable() {
        Date start = new Date();
        Date end = new Date(start.getTime() + 3600_000);

        CarUnitEntity unit = new CarUnitEntity();
        unit.setId(10L);

        when(carUnitRepository.findByCar_Id(1L)).thenReturn(List.of(unit));
        when(reservationRepository.findOverlappingReservationsForUnit(10L, start, end))
                .thenReturn(List.of(new ReservationEntity()));

        ReservationCreateByCarRequest request =
                new ReservationCreateByCarRequest(1L, null, start, end);

        assertThatThrownBy(() -> reservationService.createReservationByCar(request))
                .isInstanceOf(ReservationOverlapException.class)
                .hasMessageContaining("No available units");
    }

    // ---------------------------------------------------------
    // UPDATE RESERVATION
    // ---------------------------------------------------------
    @Test
    void shouldUpdateReservationSuccessfully() {
        Date start = new Date();
        Date end = new Date(start.getTime() + 3600_000);

        ReservationEntity entity = new ReservationEntity();
        entity.setId(1L);

        CarUnitEntity unit = new CarUnitEntity();
        unit.setId(10L);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(carUnitRepository.findById(10L)).thenReturn(Optional.of(unit));

        ReservationEntity saved = new ReservationEntity();
        saved.setId(1L);
        saved.setCarUnit(unit);

        when(reservationRepository.save(any())).thenReturn(saved);

        ReservationUpdateRequest request =
                new ReservationUpdateRequest(10L, start, end);

        ReservationResponse result = reservationService.updateReservation(1L, request);

        assertThat(result.carUnitId()).isEqualTo(10L);
    }

    @Test
    void shouldThrowNotFoundWhenUpdatingMissingReservation() {
        when(reservationRepository.findById(99L)).thenReturn(Optional.empty());

        ReservationUpdateRequest request =
                new ReservationUpdateRequest(10L, new Date(), new Date());

        assertThatThrownBy(() -> reservationService.updateReservation(99L, request))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldThrowOverlapExceptionWhenUpdatingReservationToOverlapped() {
        Date start = new Date();
        Date end = new Date(start.getTime() + 3600_000);

        // Existing reservation being updated
        ReservationEntity existing = new ReservationEntity();
        existing.setId(1L);

        // Car unit for the reservation
        CarUnitEntity unit = new CarUnitEntity();
        unit.setId(10L);

        // Overlapping reservation (different ID → real conflict)
        ReservationEntity overlap = new ReservationEntity();
        overlap.setId(2L);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(carUnitRepository.findById(10L)).thenReturn(Optional.of(unit));

        // Overlap returned by repository
        when(reservationRepository.findOverlappingReservationsForUnit(10L, start, end))
                .thenReturn(List.of(overlap));

        ReservationUpdateRequest request =
                new ReservationUpdateRequest(10L, start, end);

        assertThatThrownBy(() -> reservationService.updateReservation(1L, request))
                .isInstanceOf(ReservationOverlapException.class)
                .hasMessageContaining("conflicting reservations");
    }

    // ---------------------------------------------------------
    // DELETE RESERVATION
    // ---------------------------------------------------------
    @Test
    void shouldDeleteReservationSuccessfully() {
        when(reservationRepository.existsById(1L)).thenReturn(true);

        reservationService.deleteReservation(1L);

        verify(reservationRepository).deleteById(1L);
    }

    @Test
    void shouldThrowNotFoundWhenDeletingMissingReservation() {
        when(reservationRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> reservationService.deleteReservation(99L))
                .isInstanceOf(NotFoundException.class);
    }

    // ---------------------------------------------------------
    // FILTERING
    // ---------------------------------------------------------
    @Test
    void shouldReturnReservationsByCarUnit() {
        ReservationEntity entity = new ReservationEntity();
        entity.setId(1L);

        CarUnitEntity unit = new CarUnitEntity();
        CarEntity car = new CarEntity();
        car.setId(10L);
        unit.setCar(car);
        entity.setCarUnit(unit);

        when(reservationRepository.findByCarUnit_Id(10L)).thenReturn(List.of(entity));

        List<ReservationDetailsResponse> result = reservationService.getByCarUnit(10L);

        assertThat(result).hasSize(1);
    }

    @Test
    void shouldReturnReservationsByCarEntity() {
        ReservationEntity entity = new ReservationEntity();
        entity.setId(1L);

        CarUnitEntity unit = new CarUnitEntity();
        CarEntity car = new CarEntity();
        car.setId(10L);
        unit.setCar(car);
        entity.setCarUnit(unit);

        when(reservationRepository.findByCarUnit_Car_Id(10L)).thenReturn(List.of(entity));

        List<ReservationDetailsResponse> result = reservationService.getByCarEntity(10L);

        assertThat(result).hasSize(1);
    }

    @Test
    void shouldReturnReservationsByCarType() {
        ReservationEntity entity = new ReservationEntity();
        entity.setId(1L);

        CarUnitEntity unit = new CarUnitEntity();
        CarEntity car = new CarEntity();
        car.setCarType(CarType.SUV);
        unit.setCar(car);
        entity.setCarUnit(unit);

        when(reservationRepository.findByCarUnit_Car_CarType(CarType.SUV))
                .thenReturn(List.of(entity));

        List<ReservationDetailsResponse> result = reservationService.getByCarType(CarType.SUV);

        assertThat(result).hasSize(1);
    }

    // ---------------------------------------------------------
    // AVAILABILITY
    // ---------------------------------------------------------
    @Test
    void shouldReturnUnitAvailable() {
        when(reservationRepository.findOverlappingReservationsForUnit(10L, new Date(), new Date()))
                .thenReturn(List.of());

        AvailabilityResponse result =
                reservationService.checkUnitAvailability(10L, new Date(), new Date());

        assertThat(result.availableUnits()).isEqualTo(1);
        assertThat(result.availableCarUnitIds()).containsExactly(10L);
    }

    @Test
    void shouldReturnUnitUnavailable() {
        when(reservationRepository.findOverlappingReservationsForUnit(10L, new Date(), new Date()))
                .thenReturn(List.of(new ReservationEntity()));

        AvailabilityResponse result =
                reservationService.checkUnitAvailability(10L, new Date(), new Date());

        assertThat(result.availableUnits()).isEqualTo(0);
        assertThat(result.availableCarUnitIds()).isEmpty();
    }

    @Test
    void shouldReturnAvailableUnitsForCarEntity() {
        Date start = new Date();
        Date end = new Date(start.getTime() + 3600_000);

        CarUnitEntity u1 = new CarUnitEntity();
        u1.setId(10L);

        CarUnitEntity u2 = new CarUnitEntity();
        u2.setId(11L);

        when(carUnitRepository.findByCar_Id(1L)).thenReturn(List.of(u1, u2));

        when(reservationRepository.findOverlappingReservationsForUnit(10L, start, end))
                .thenReturn(List.of());
        when(reservationRepository.findOverlappingReservationsForUnit(11L, start, end))
                .thenReturn(List.of(new ReservationEntity()));

        AvailabilityResponse result =
                reservationService.checkEntityAvailability(1L, start, end);

        assertThat(result.availableUnits()).isEqualTo(1);
        assertThat(result.availableCarUnitIds()).containsExactly(10L);
    }

    @Test
    void shouldReturnAvailableUnitsForCarType() {
        Date start = new Date();
        Date end = new Date(start.getTime() + 3600_000);

        CarEntity car = new CarEntity();
        car.setId(1L);
        car.setCarType(CarType.SUV);

        CarUnitEntity unit = new CarUnitEntity();
        unit.setId(10L);

        when(carRepository.getByCarType(CarType.SUV)).thenReturn(List.of(car));
        when(carUnitRepository.findByCar_IdIn(List.of(1L))).thenReturn(List.of(unit));
        when(reservationRepository.findOverlappingReservationsForUnit(10L, start, end))
                .thenReturn(List.of());

        AvailabilityResponse result =
                reservationService.checkEntityAvailability(CarType.SUV, start, end);

        assertThat(result.availableUnits()).isEqualTo(1);
        assertThat(result.availableCarUnitIds()).containsExactly(10L);
    }
}

