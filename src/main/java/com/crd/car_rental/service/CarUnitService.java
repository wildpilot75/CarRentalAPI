package com.crd.car_rental.service;

import com.crd.car_rental.dto.*;
import com.crd.car_rental.error.NotFoundException;
import com.crd.car_rental.model.CarEntity;
import com.crd.car_rental.model.CarType;
import com.crd.car_rental.model.CarUnitEntity;
import com.crd.car_rental.repository.CarRepository;
import com.crd.car_rental.repository.CarUnitRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CarUnitService {

    private final CarUnitRepository carUnitRepository;
    private final CarRepository carRepository;

    public CarUnitService(CarUnitRepository carUnitRepository, CarRepository carRepository) {
        this.carUnitRepository = carUnitRepository;
        this.carRepository = carRepository;
    }

    public List<CarUnitResponse> getAll(Optional<Long> carId, Optional<CarType> carType) {

        // Case 1 — both filters present → intersection
        if (carId.isPresent() && carType.isPresent()) {
            Long id = carId.get();
            CarType type = carType.get();

            // Get all cars of that type
            List<Long> carIdsOfType = carRepository.getByCarType(type)
                    .stream()
                    .map(CarEntity::getId)
                    .toList();

            // If the specific carId is NOT in the list → empty
            if (!carIdsOfType.contains(id)) {
                return List.of();
            }

            // Otherwise return units for that car
            return carUnitRepository.findByCar_Id(id)
                    .stream()
                    .map(this::mapToResponse)
                    .toList();
        }

        // Case 2 — only carType
        if (carType.isPresent()) {
            CarType type = carType.get();

            return carUnitRepository.findByCar_CarType(type)
                    .stream()
                    .map(this::mapToResponse)
                    .toList();
        }

        // Case 3 — only carId
        if (carId.isPresent()) {
            return carUnitRepository.findByCar_Id(carId.get())
                    .stream()
                    .map(this::mapToResponse)
                    .toList();
        }

        // Case 4 — no filters → return all
        return carUnitRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public CarUnitResponse getById(Long id) {
        return carUnitRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new NotFoundException("Car unit with id " + id + " does not exist"));
    }

    public CarUnitResponse addCarUnit(CarUnitCreateRequest request) {
        CarUnitEntity entity = new CarUnitEntity();
        mapToEntity(request, entity);
        CarUnitEntity saved = carUnitRepository.save(entity);
        return mapToResponse(saved);
    }

    public CarUnitResponse updateCarUnit(Long id, CarUnitUpdateRequest request) {
        if (!carUnitRepository.existsById(id)) {
            throw new NotFoundException("Car unit with id " + id + " does not exist");
        }

        CarUnitEntity entity = new CarUnitEntity();
        mapToEntity(id, request, entity);
        CarUnitEntity saved = carUnitRepository.save(entity);
        return mapToResponse(saved);
    }

    public void deleteCarUnit(Long id) {
        if (!carUnitRepository.existsById(id)) {
            throw new NotFoundException("Car unit with id " + id + " does not exist");
        }
        carUnitRepository.deleteById(id);
    }

    private void mapToEntity(CarUnitCreateRequest request, CarUnitEntity entity) {
        CarEntity car = carRepository.findById(request.carId())
                .orElseThrow(() -> new NotFoundException("Car not found with id " + request.carId()));

        entity.setCar(car);
        entity.setVin(request.vin());
    }

    private void mapToEntity(Long id, CarUnitUpdateRequest request, CarUnitEntity entity) {
        CarEntity car = carRepository.findById(request.carId())
                .orElseThrow(() -> new NotFoundException("Car not found with id " + request.carId()));

        entity.setId(id);
        entity.setCar(car);
        entity.setVin(request.vin());
    }

    private CarUnitResponse mapToResponse(CarUnitEntity entity) {
        return new CarUnitResponse(
                entity.getId(),
                entity.getCar().getId(),
                entity.getVin()
        );
    }
}
