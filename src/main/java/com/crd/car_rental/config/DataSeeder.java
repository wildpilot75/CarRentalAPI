package com.crd.car_rental.config;

import com.crd.car_rental.model.CarEntity;
import com.crd.car_rental.model.CarUnitEntity;
import com.crd.car_rental.model.ReservationEntity;
import com.crd.car_rental.repository.CarRepository;
import com.crd.car_rental.model.CarType;
import com.crd.car_rental.repository.CarUnitRepository;
import com.crd.car_rental.repository.ReservationRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner initCarRepository(
            CarRepository carRepository,
            CarUnitRepository carUnitRepository,
            ReservationRepository reservationRepository) {
        return args -> {
            if (carRepository.count() == 0) {
                System.out.println("Seeding initial data...");

                System.out.println("Seeding car entity data...");

                CarEntity car1 = new CarEntity();
                car1.setMake("Toyota");
                car1.setModel("Camry");
                car1.setCarType(CarType.SEDAN);
                carRepository.save(car1);

                CarEntity car2 = new CarEntity();
                car2.setMake("Honda");
                car2.setModel("Accord");
                car2.setCarType(CarType.SEDAN);
                carRepository.save(car2);

                CarEntity car3 = new CarEntity();
                car3.setMake("Ford");
                car3.setModel("Explorer");
                car3.setCarType(CarType.SUV);
                carRepository.save(car3);

                CarEntity car4 = new CarEntity();
                car4.setMake("Toyota");
                car4.setModel("RAV4");
                car4.setCarType(CarType.SUV);
                carRepository.save(car4);

                CarEntity car5 = new CarEntity();
                car5.setMake("Dodge");
                car5.setModel("Grand Caravan");
                car5.setCarType(CarType.VAN);
                carRepository.save(car5);

                CarEntity car6 = new CarEntity();
                car6.setMake("Honda");
                car6.setModel("Odyssey");
                car6.setCarType(CarType.VAN);
                carRepository.save(car6);
                System.out.println("Seeding car entity Complete");
            }

            if (carUnitRepository.count() == 0) {
                System.out.println("Seeding car unit entity data...");
                for (CarEntity car : carRepository.findAll()) {
                    for (int i = 1; i <= 2; i++) {
                        CarUnitEntity unit = new CarUnitEntity();
                        unit.setCar(car);
                        unit.setVin(car.getMake().substring(0, 3).toUpperCase() + "-" +
                                car.getModel().substring(0, 3).toUpperCase() + "-" +
                                car.getId() + "-" + i);
                        carUnitRepository.save(unit);
                    }
                }

                System.out.println("Seeding car unit entity Complete");
            }

            if (reservationRepository.count() == 0) {
                System.out.println("Seeding reservation data...");

                Random random = new Random();
                List<CarUnitEntity> units = carUnitRepository.findAll();

                for (CarUnitEntity unit : units) {

                    // Generate between 1 and 4 reservations
                    int reservationCount = random.nextInt(4) + 1;

                    Date lastEndDate = null;

                    for (int i = 0; i < reservationCount; i++) {

                        // Start date: today + 0–7 days
                        long startOffsetDays = random.nextInt(7);
                        Date startDate = Date.from(
                                LocalDate.now().plusDays(startOffsetDays)
                                        .atStartOfDay(ZoneId.systemDefault()).toInstant()
                        );

                        // Length: 1–5 days
                        int lengthDays = random.nextInt(5) + 1;
                        Date endDate = Date.from(
                                LocalDate.now().plusDays(startOffsetDays + lengthDays)
                                        .atStartOfDay(ZoneId.systemDefault()).toInstant()
                        );

                        // Ensure no overlap with previous reservation
                        if (lastEndDate != null && !startDate.after(lastEndDate)) {
                            startDate = Date.from(
                                    lastEndDate.toInstant().plus(1, ChronoUnit.DAYS)
                            );
                            endDate = Date.from(
                                    startDate.toInstant().plus(lengthDays, ChronoUnit.DAYS)
                            );
                        }

                        lastEndDate = endDate;

                        ReservationEntity reservation = new ReservationEntity();
                        reservation.setCarUnit(unit);
                        reservation.setStartDate(startDate);
                        reservation.setEndDate(endDate);

                        reservationRepository.save(reservation);
                    }
                }

                System.out.println("Seeding reservation data complete");
            }

            System.out.println("Seeding complete.");
        };
    }
}
