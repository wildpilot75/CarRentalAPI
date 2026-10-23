package com.crd.car_rental.config;

import com.crd.car_rental.dto.CarUnitResponse;
import com.crd.car_rental.model.CarEntity;
import com.crd.car_rental.model.CarUnitEntity;
import com.crd.car_rental.repository.CarRepository;
import com.crd.car_rental.model.CarType;
import com.crd.car_rental.repository.CarUnitRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner initCarRepository(CarRepository carRepository, CarUnitRepository carUnitRepository) {
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
                        unit.setCarEntityId(car.getId());
                        unit.setVin(car.getMake().substring(0, 3).toUpperCase() + "-" +
                                car.getModel().substring(0, 3).toUpperCase() + "-" +
                                car.getId() + "-" + i);
                        carUnitRepository.save(unit);
                    }
                }

                System.out.println("Seeding car unit entity Complete");
            }

            System.out.println("Seeding complete.");
        };
    }
}
