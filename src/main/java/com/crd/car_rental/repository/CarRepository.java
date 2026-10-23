package com.crd.car_rental.repository;

import com.crd.car_rental.model.CarEntity;
import com.crd.car_rental.model.CarType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarRepository extends JpaRepository<CarEntity, Long> {
    List<CarEntity> getByCarType(CarType carType);
}
