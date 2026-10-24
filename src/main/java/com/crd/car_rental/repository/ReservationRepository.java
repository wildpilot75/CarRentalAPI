package com.crd.car_rental.repository;

import com.crd.car_rental.model.ReservationEntity;
import com.crd.car_rental.model.CarType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {
    List<ReservationEntity> findByCarUnit_Id(Long carUnitId);
    List<ReservationEntity> findByCarUnit_Car_Id(Long carId);
    List<ReservationEntity> findByCarUnit_Car_CarType(CarType carType);

    // Overlapping reservations for a specific CarUnit
    @Query("""
        SELECT r FROM ReservationEntity r
        WHERE r.carUnit.id = :carUnitId
        AND r.startDate <= :endDate
        AND r.endDate >= :startDate
    """)
    List<ReservationEntity> findOverlappingReservationsForUnit(Long carUnitId, Date startDate, Date endDate);
}
