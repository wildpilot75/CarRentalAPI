package com.crd.car_rental.repository;

import com.crd.car_rental.model.CarEntity;
import com.crd.car_rental.model.CarUnitEntity;
import com.crd.car_rental.model.CarType;
import com.crd.car_rental.model.ReservationEntity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ReservationRepositoryTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private CarUnitRepository carUnitRepository;

    @Autowired
    private CarRepository carRepository;

    @Test
    void shouldFindOverlappingReservations() {
        // Create car
        CarEntity car = new CarEntity();
        car.setMake("Toyota");
        car.setModel("Camry");
        car.setCarType(CarType.SEDAN);
        car = carRepository.save(car);

        // Create car unit
        CarUnitEntity unit = new CarUnitEntity();
        unit.setCar(car);
        unit.setVin("VIN123");
        unit = carUnitRepository.save(unit);

        // Existing reservation: 10:00 → 12:00
        ReservationEntity existing = new ReservationEntity();
        existing.setCarUnit(unit);
        existing.setStartDate(new Date(1000)); // 10:00
        existing.setEndDate(new Date(2000));   // 12:00
        reservationRepository.save(existing);

        // Query for overlapping reservation: 11:00 → 13:00
        List<ReservationEntity> overlaps =
                reservationRepository.findOverlappingReservationsForUnit(
                        unit.getId(),
                        new Date(1500),
                        new Date(2500)
                );

        assertThat(overlaps)
                .hasSize(1)
                .first()
                .extracting(ReservationEntity::getId)
                .isEqualTo(existing.getId());
    }

    @Test
    void shouldNotFindNonOverlappingReservations() {
        // Create car
        CarEntity car = new CarEntity();
        car.setMake("Toyota");
        car.setModel("Camry");
        car.setCarType(CarType.SEDAN);
        car = carRepository.save(car);

        // Create car unit
        CarUnitEntity unit = new CarUnitEntity();
        unit.setCar(car);
        unit.setVin("VIN123");
        unit = carUnitRepository.save(unit);

        // Existing reservation: 10:00 → 12:00
        ReservationEntity existing = new ReservationEntity();
        existing.setCarUnit(unit);
        existing.setStartDate(new Date(1000));
        existing.setEndDate(new Date(2000));
        reservationRepository.save(existing);

        // Query for non-overlapping reservation: 12:01 → 14:00
        List<ReservationEntity> overlaps =
                reservationRepository.findOverlappingReservationsForUnit(
                        unit.getId(),
                        new Date(2001),
                        new Date(3000)
                );

        assertThat(overlaps).isEmpty();
    }
}

