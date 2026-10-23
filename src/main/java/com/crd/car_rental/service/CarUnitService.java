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

    public List<CarUnitResponse> getAll(Optional<Long> carEntityId, Optional<CarType> carType) {

        if (carEntityId.isPresent() && carType.isPresent()) {
            Long id = carEntityId.get();
            CarType type = carType.get();

            List<Long> carIdsOfType = carRepository.getByCarType(type)
                    .stream()
                    .map(CarEntity::getId)
                    .toList();

            if (!carIdsOfType.contains(id)) {
                return List.of();
            }

            return carUnitRepository.getByCarEntityId(id)
                    .stream()
                    .map(this::mapToResponse)
                    .toList();
        }

        if (carType.isPresent()) {
            CarType type = carType.get();

            List<Long> carIdsOfType = carRepository.getByCarType(type)
                    .stream()
                    .map(CarEntity::getId)
                    .toList();

            return carUnitRepository.findByCarEntityIdIn(carIdsOfType)
                    .stream()
                    .map(this::mapToResponse)
                    .toList();
        }

        if (carEntityId.isPresent()) {
            return carUnitRepository.getByCarEntityId(carEntityId.get())
                    .stream()
                    .map(this::mapToResponse)
                    .toList();
        }

        return carUnitRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public CarUnitResponse getById(Long id) {
        return carUnitRepository
                .findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new NotFoundException("Car unit with id " + id + " does not exist"));
    }

    public CarUnitResponse addCarUnit(CarUnitCreateRequest carUnitCreateRequest) {
        CarUnitEntity carUnitEntity = new CarUnitEntity();
        mapToEntity(carUnitCreateRequest, carUnitEntity);
        CarUnitEntity saved =  carUnitRepository.save(carUnitEntity);
        return mapToResponse(saved);
    }

    public CarUnitResponse updateCarUnit(Long id, CarUnitUpdateRequest request) {
        if (!carUnitRepository.existsById(id)) {
            throw new NotFoundException("Car unit with id " + id + " does not exist");
        }

        CarUnitEntity carUnitEntity = new CarUnitEntity();
        mapToEntity(id, request, carUnitEntity);
        CarUnitEntity saved = carUnitRepository.save(carUnitEntity);
        return mapToResponse(saved);
    }

    public void deleteCarUnit(Long id) {
        if (!carUnitRepository.existsById(id)) {
            throw new NotFoundException("Car unit with id " + id + " does not exist");
        }
        carUnitRepository.deleteById(id);
    }

    private void mapToEntity(CarUnitCreateRequest request, CarUnitEntity carUnitEntity) {
        carUnitEntity.setCarEntityId(request.carEntityId());
        carUnitEntity.setVin(request.vin());
    }

    private void mapToEntity(Long id, CarUnitUpdateRequest request, CarUnitEntity carUnitEntity) {
        carUnitEntity.setId(id);
        carUnitEntity.setCarEntityId(request.carEntityId());
        carUnitEntity.setVin(request.vin());
    }

    private CarUnitResponse mapToResponse(CarUnitEntity carUnitEntity) {
        return new CarUnitResponse(
                carUnitEntity.getId(),
                carUnitEntity.getCarEntityId(),
                carUnitEntity.getVin(),
                carUnitEntity.getReservations()
        );
    }
}
