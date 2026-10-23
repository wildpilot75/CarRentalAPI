package com.crd.car_rental.api;

import com.crd.car_rental.dto.*;
import com.crd.car_rental.model.CarType;
import com.crd.car_rental.service.CarUnitService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/carunits")
public class CarUnitController {
    private final CarUnitService carUnitService;

    @Autowired
    public CarUnitController(CarUnitService carUnitService) {
        this.carUnitService = carUnitService;
    }

    @GetMapping
    public List<CarUnitResponse> getCarUnits(
            @RequestParam Optional<Long> carId,
            @RequestParam Optional<CarType> carType) {
        return carUnitService.getAll(carId, carType);
    }

    @GetMapping("{id}")
    public CarUnitResponse getCarUnitById(@PathVariable Long id) {
        return carUnitService.getById(id);
    }

    @PostMapping
    public ResponseEntity<CarUnitResponse> addCarUnit(@Valid @RequestBody CarUnitCreateRequest carUnit) {
        CarUnitResponse created =  carUnitService.addCarUnit(carUnit);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarUnitResponse> updateCarUnit(
            @PathVariable Long id,
            @Valid @RequestBody CarUnitUpdateRequest request) {
        return ResponseEntity.ok(carUnitService.updateCarUnit(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        carUnitService.deleteCarUnit(id);
        return ResponseEntity.noContent().build();
    }
}
