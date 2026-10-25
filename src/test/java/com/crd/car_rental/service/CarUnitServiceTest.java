package com.crd.car_rental.service;

import com.crd.car_rental.dto.CarUnitCreateRequest;
import com.crd.car_rental.dto.CarUnitResponse;
import com.crd.car_rental.dto.CarUnitUpdateRequest;
import com.crd.car_rental.error.NotFoundException;
import com.crd.car_rental.model.CarEntity;
import com.crd.car_rental.model.CarType;
import com.crd.car_rental.model.CarUnitEntity;
import com.crd.car_rental.repository.CarRepository;
import com.crd.car_rental.repository.CarUnitRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CarUnitServiceTest {

    private CarUnitRepository carUnitRepository;
    private CarRepository carRepository;
    private CarUnitService carUnitService;

    @BeforeEach
    void setUp() {
        carUnitRepository = mock(CarUnitRepository.class);
        carRepository = mock(CarRepository.class);
        carUnitService = new CarUnitService(carUnitRepository, carRepository);
    }

    // ---------------------------------------------------------
    // GET ALL
    // ---------------------------------------------------------
    @Test
    void shouldReturnAllUnitsWhenNoFiltersProvided() {
        CarEntity car = new CarEntity();
        car.setId(1L);

        CarUnitEntity unit1 = new CarUnitEntity();
        unit1.setId(10L);
        unit1.setCar(car);
        unit1.setVin("VIN1");

        CarUnitEntity unit2 = new CarUnitEntity();
        unit2.setId(11L);
        unit2.setCar(car);
        unit2.setVin("VIN2");

        when(carUnitRepository.findAll()).thenReturn(List.of(unit1, unit2));

        List<CarUnitResponse> result = carUnitService.getAll(Optional.empty(), Optional.empty());

        assertThat(result)
                .hasSize(2)
                .extracting(CarUnitResponse::id)
                .containsExactly(10L, 11L);
    }

    @Test
    void shouldReturnUnitsFilteredByCarId() {
        CarEntity car = new CarEntity();
        car.setId(1L);

        CarUnitEntity unit = new CarUnitEntity();
        unit.setId(10L);
        unit.setCar(car);
        unit.setVin("VIN1");

        when(carUnitRepository.findByCar_Id(1L)).thenReturn(List.of(unit));

        List<CarUnitResponse> result = carUnitService.getAll(Optional.of(1L), Optional.empty());

        assertThat(result)
                .hasSize(1)
                .first()
                .extracting(CarUnitResponse::carId)
                .isEqualTo(1L);
    }

    @Test
    void shouldReturnUnitsFilteredByCarType() {
        CarEntity car = new CarEntity();
        car.setId(1L);
        car.setCarType(CarType.SUV);

        CarUnitEntity unit = new CarUnitEntity();
        unit.setId(10L);
        unit.setCar(car);
        unit.setVin("VIN1");

        when(carUnitRepository.findByCar_CarType(CarType.SUV)).thenReturn(List.of(unit));

        List<CarUnitResponse> result = carUnitService.getAll(Optional.empty(), Optional.of(CarType.SUV));

        assertThat(result)
                .hasSize(1)
                .first()
                .extracting(CarUnitResponse::carId)
                .isEqualTo(1L);
    }

    @Test
    void shouldFilterOutUnitsOfDifferentCarType() {
        // SUV car
        CarEntity suvCar = new CarEntity();
        suvCar.setId(1L);
        suvCar.setCarType(CarType.SUV);

        CarUnitEntity suvUnit = new CarUnitEntity();
        suvUnit.setId(10L);
        suvUnit.setCar(suvCar);
        suvUnit.setVin("SUVVIN");

        // SEDAN car (wrong type)
        CarEntity sedanCar = new CarEntity();
        sedanCar.setId(2L);
        sedanCar.setCarType(CarType.SEDAN);

        CarUnitEntity sedanUnit = new CarUnitEntity();
        sedanUnit.setId(20L);
        sedanUnit.setCar(sedanCar);
        sedanUnit.setVin("SEDANVIN");

        when(carUnitRepository.findByCar_CarType(CarType.SUV))
                .thenReturn(List.of(suvUnit));

        when(carUnitRepository.findByCar_CarType(CarType.SEDAN))
                .thenReturn(List.of(sedanUnit));

        List<CarUnitResponse> result =
                carUnitService.getAll(Optional.empty(), Optional.of(CarType.SUV));

        assertThat(result)
                .hasSize(1)
                .extracting(CarUnitResponse::carId)
                .containsExactly(1L); // only SUV carId
    }

    @Test
    void shouldReturnEmptyListWhenCarIdDoesNotMatchCarType() {
        CarEntity car = new CarEntity();
        car.setId(2L);
        car.setCarType(CarType.SUV);

        when(carRepository.getByCarType(CarType.SUV)).thenReturn(List.of(car));

        List<CarUnitResponse> result = carUnitService.getAll(Optional.of(1L), Optional.of(CarType.SUV));

        assertThat(result).isEmpty();
    }

    // ---------------------------------------------------------
    // GET BY ID
    // ---------------------------------------------------------
    @Test
    void shouldReturnCarUnitById() {
        CarEntity car = new CarEntity();
        car.setId(1L);

        CarUnitEntity unit = new CarUnitEntity();
        unit.setId(10L);
        unit.setCar(car);
        unit.setVin("VIN1");

        when(carUnitRepository.findById(10L)).thenReturn(Optional.of(unit));

        CarUnitResponse result = carUnitService.getById(10L);

        assertThat(result.id()).isEqualTo(10L);
        assertThat(result.vin()).isEqualTo("VIN1");
    }

    @Test
    void shouldThrowNotFoundWhenUnitDoesNotExist() {
        when(carUnitRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> carUnitService.getById(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Car unit with id 99 does not exist");
    }

    // ---------------------------------------------------------
    // ADD CAR UNIT
    // ---------------------------------------------------------
    @Test
    void shouldAddCarUnitSuccessfully() {
        CarEntity car = new CarEntity();
        car.setId(1L);

        CarUnitCreateRequest request = new CarUnitCreateRequest(1L, "VIN123");

        when(carRepository.findById(1L)).thenReturn(Optional.of(car));

        CarUnitEntity saved = new CarUnitEntity();
        saved.setId(10L);
        saved.setCar(car);
        saved.setVin("VIN123");

        when(carUnitRepository.save(any(CarUnitEntity.class))).thenReturn(saved);

        CarUnitResponse result = carUnitService.addCarUnit(request);

        assertThat(result.id()).isEqualTo(10L);
        assertThat(result.vin()).isEqualTo("VIN123");

        ArgumentCaptor<CarUnitEntity> captor = ArgumentCaptor.forClass(CarUnitEntity.class);
        verify(carUnitRepository).save(captor.capture());

        assertThat(captor.getValue().getVin()).isEqualTo("VIN123");
    }

    // ---------------------------------------------------------
    // UPDATE CAR UNIT
    // ---------------------------------------------------------
    @Test
    void shouldUpdateCarUnitSuccessfully() {
        CarEntity car = new CarEntity();
        car.setId(1L);

        CarUnitUpdateRequest request = new CarUnitUpdateRequest(1L, "NEWVIN");

        when(carUnitRepository.existsById(10L)).thenReturn(true);
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));

        CarUnitEntity saved = new CarUnitEntity();
        saved.setId(10L);
        saved.setCar(car);
        saved.setVin("NEWVIN");

        when(carUnitRepository.save(any(CarUnitEntity.class))).thenReturn(saved);

        CarUnitResponse result = carUnitService.updateCarUnit(10L, request);

        assertThat(result.vin()).isEqualTo("NEWVIN");
    }

    @Test
    void shouldThrowNotFoundWhenUpdatingNonExistingUnit() {
        when(carUnitRepository.existsById(99L)).thenReturn(false);

        CarUnitUpdateRequest request = new CarUnitUpdateRequest(1L, "VIN");

        assertThatThrownBy(() -> carUnitService.updateCarUnit(99L, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Car unit with id 99 does not exist");
    }

    // ---------------------------------------------------------
    // DELETE CAR UNIT
    // ---------------------------------------------------------
    @Test
    void shouldDeleteCarUnitSuccessfully() {
        when(carUnitRepository.existsById(10L)).thenReturn(true);

        carUnitService.deleteCarUnit(10L);

        verify(carUnitRepository).deleteById(10L);
    }

    @Test
    void shouldThrowNotFoundWhenDeletingNonExistingUnit() {
        when(carUnitRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> carUnitService.deleteCarUnit(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Car unit with id 99 does not exist");
    }
}

