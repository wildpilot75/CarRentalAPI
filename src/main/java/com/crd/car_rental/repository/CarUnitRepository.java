package com.crd.car_rental.repository;

import com.crd.car_rental.model.CarUnitEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarUnitRepository extends JpaRepository<CarUnitEntity, Long> {
    List<CarUnitEntity> getByCarEntityId(Long carEntityId);
    List<CarUnitEntity> findByCarEntityIdIn(List<Long> carEntityIds);
}
