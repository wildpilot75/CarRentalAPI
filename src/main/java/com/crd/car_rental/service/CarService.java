package com.crd.car_rental.service;

import com.crd.car_rental.dto.*;
import com.crd.car_rental.error.NotFoundException;
import com.crd.car_rental.model.CarEntity;
import com.crd.car_rental.repository.CarRepository;
import com.crd.car_rental.model.CarType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CarService {
    private final CarRepository carRepository;

    public CarService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    public List<CarResponse> getAll(Optional<CarType> carType) {
        return carType.map(type -> carRepository
                .getByCarType(type)
                .stream()
                .map(this::mapToResponse)
                .toList()).orElseGet(() -> carRepository
                .findAll()
                .stream()
                .map(this::mapToResponse)
                .toList());
    }

    public CarResponse getById(Long id) {
        return carRepository
                .findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new NotFoundException("Car with id " + id + " does not exist"));
    }

    public CarResponse addCar(CarCreateRequest request) {
        CarEntity carEntity = new CarEntity();
        mapToEntity(request, carEntity);
        CarEntity saved = carRepository.save(carEntity);
        return mapToResponse(saved);
    }

    public CarResponse updateCar(Long id, CarUpdateRequest request) {
        if (!carRepository.existsById(id)) {
            throw new NotFoundException("Car not found with id: " + id);
        }

        CarEntity carEntity = new CarEntity();
        mapToEntity(id, request, carEntity);
        CarEntity saved = carRepository.save(carEntity);
        return mapToResponse(saved);
    }

    public void deleteCar(Long id) {
        if (!carRepository.existsById(id)) {
            throw new NotFoundException("Car not found with id: " + id);
        }
        carRepository.deleteById(id);
    }

    private void mapToEntity(CarCreateRequest request, CarEntity carEntity) {
        carEntity.setMake(request.make());
        carEntity.setModel(request.model());
        carEntity.setCarType(request.carType());
    }

    private void mapToEntity(Long id, CarUpdateRequest request, CarEntity carEntity) {
        carEntity.setId(id);
        carEntity.setMake(request.make());
        carEntity.setModel(request.model());
        carEntity.setCarType(request.carType());
    }

    private CarResponse mapToResponse(CarEntity carEntity) {
        return new CarResponse(
                carEntity.getId(),
                carEntity.getMake(),
                carEntity.getModel(),
                carEntity.getCarType()
        );
    }
}
