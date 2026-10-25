package com.crd.car_rental.service;

import com.crd.car_rental.dto.CarCreateRequest;
import com.crd.car_rental.dto.CarResponse;
import com.crd.car_rental.dto.CarUpdateRequest;
import com.crd.car_rental.model.CarEntity;
import com.crd.car_rental.model.CarType;
import com.crd.car_rental.repository.CarRepository;
import com.crd.car_rental.error.NotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CarServiceTest {

    private CarRepository carRepository;
    private CarService carService;

    @BeforeEach
    void setUp() {
        carRepository = mock(CarRepository.class);
        carService = new CarService(carRepository);
    }

    // ---------------------------------------------------------
    // GET ALL
    // ---------------------------------------------------------
    @Test
    void shouldReturnAllCarsWhenNoFilterProvided() {
        CarEntity car1 = new CarEntity();
        car1.setId(1L);
        car1.setMake("Toyota");
        car1.setModel("Camry");
        car1.setCarType(CarType.SEDAN);

        CarEntity car2 = new CarEntity();
        car2.setId(2L);
        car2.setMake("Honda");
        car2.setModel("Camry");
        car2.setCarType(CarType.SEDAN);

        when(carRepository.findAll()).thenReturn(List.of(car1, car2));

        List<CarResponse> result = carService.getAll(Optional.empty());

        assertThat(result)
                .hasSize(2)
                .extracting(CarResponse::id)
                .containsExactly(1L, 2L);
    }

    @Test
    void shouldReturnCarsFilteredByType() {
        CarEntity car = new CarEntity();
        car.setId(1L);
        car.setMake("Ford");
        car.setModel("Explorer");
        car.setCarType(CarType.SUV);

        when(carRepository.getByCarType(CarType.SUV)).thenReturn(List.of(car));

        List<CarResponse> result = carService.getAll(Optional.of(CarType.SUV));

        assertThat(result)
                .hasSize(1)
                .first()
                .extracting(CarResponse::carType)
                .isEqualTo(CarType.SUV);
    }

    // ---------------------------------------------------------
    // GET BY ID
    // ---------------------------------------------------------
    @Test
    void shouldReturnCarById() {
        CarEntity car = new CarEntity();
        car.setId(1L);
        car.setMake("Toyota");
        car.setModel("Camry");
        car.setCarType(CarType.SEDAN);

        when(carRepository.findById(1L)).thenReturn(Optional.of(car));

        CarResponse result = carService.getById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.make()).isEqualTo("Toyota");
    }

    @Test
    void shouldThrowNotFoundWhenCarDoesNotExist() {
        when(carRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> carService.getById(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Car with id 99 does not exist");
    }

    // ---------------------------------------------------------
    // ADD CAR
    // ---------------------------------------------------------
    @Test
    void shouldAddCarSuccessfully() {
        CarCreateRequest request = new CarCreateRequest("Toyota", "Camry", CarType.SEDAN);

        CarEntity saved = new CarEntity();
        saved.setId(1L);
        saved.setMake("Toyota");
        saved.setModel("Camry");
        saved.setCarType(CarType.SEDAN);
        when(carRepository.save(any(CarEntity.class))).thenReturn(saved);

        CarResponse result = carService.addCar(request);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.make()).isEqualTo("Toyota");

        ArgumentCaptor<CarEntity> captor = ArgumentCaptor.forClass(CarEntity.class);
        verify(carRepository).save(captor.capture());

        assertThat(captor.getValue().getMake()).isEqualTo("Toyota");
    }

    // ---------------------------------------------------------
    // UPDATE CAR
    // ---------------------------------------------------------
    @Test
    void shouldUpdateCarSuccessfully() {
        CarUpdateRequest request = new CarUpdateRequest("Honda", "Civic", CarType.SEDAN);

        when(carRepository.existsById(1L)).thenReturn(true);

        CarEntity saved = new CarEntity();
        saved.setId(1L);
        saved.setMake("Honda");
        saved.setModel("Civic");
        saved.setCarType(CarType.SEDAN);

        when(carRepository.save(any(CarEntity.class))).thenReturn(saved);

        CarResponse result = carService.updateCar(1L, request);

        assertThat(result.make()).isEqualTo("Honda");
        assertThat(result.model()).isEqualTo("Civic");
    }

    @Test
    void shouldThrowNotFoundWhenUpdatingNonExistingCar() {
        when(carRepository.existsById(99L)).thenReturn(false);

        CarUpdateRequest request = new CarUpdateRequest("Ford", "Focus", CarType.SEDAN);

        assertThatThrownBy(() -> carService.updateCar(99L, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Car not found with id: 99");
    }

    // ---------------------------------------------------------
    // DELETE CAR
    // ---------------------------------------------------------
    @Test
    void shouldDeleteCarSuccessfully() {
        when(carRepository.existsById(1L)).thenReturn(true);

        carService.deleteCar(1L);

        verify(carRepository).deleteById(1L);
    }

    @Test
    void shouldThrowNotFoundWhenDeletingNonExistingCar() {
        when(carRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> carService.deleteCar(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Car not found with id: 99");
    }
}

