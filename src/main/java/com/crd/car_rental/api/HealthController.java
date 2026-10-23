package com.crd.car_rental.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {
    @GetMapping
    public Map<String, Object> health() {
        var formatter = java.time.format.DateTimeFormatter
                .ofPattern("yyyy-MM-dd HH:mm:ss z")
                .withZone(java.time.ZoneId.systemDefault());

        return Map.of("status", "UP",
                "time", formatter.format(java.time.Instant.now()),
                "app", "Car Rental");
    }
}
