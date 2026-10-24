package com.crd.car_rental.repository;

import com.crd.car_rental.model.CarType;
import com.crd.car_rental.model.CarUnitEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarUnitRepository extends JpaRepository<CarUnitEntity, Long> {
    List<CarUnitEntity> findByCar_Id(Long carId);
    List<CarUnitEntity> findByCar_CarType(CarType carType);
    List<CarUnitEntity> findByCar_IdIn(List<Long> carIds);
}
