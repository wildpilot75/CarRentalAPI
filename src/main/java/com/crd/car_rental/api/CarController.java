package com.crd.car_rental.api;

import com.crd.car_rental.dto.CarCreateRequest;
import com.crd.car_rental.dto.CarResponse;
import com.crd.car_rental.dto.CarUpdateRequest;
import com.crd.car_rental.model.CarType;
import com.crd.car_rental.service.CarService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cars")
public class CarController {

    private final CarService carService;

    @Autowired
    public CarController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping
    public List<CarResponse> getCars(@RequestParam Optional<CarType> carType) {
        return carService.getAll(carType);
    }

    @GetMapping("{id}")
    public CarResponse getCarById(@PathVariable Long id) {
        return carService.getById(id);
    }

    @PostMapping
    public ResponseEntity<CarResponse> addCar(@Valid @RequestBody CarCreateRequest car) {
        CarResponse created =  carService.addCar(car);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody CarUpdateRequest request) {
        return ResponseEntity.ok(carService.updateCar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        carService.deleteCar(id);
        return ResponseEntity.noContent().build();
    }
}
