package com.crd.car_rental.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Car")
public class CarEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String make;
    private String model;
    private CarType carType;

    public String getModel() {
        return model;
    }

    public Long getId() {
        return id;
    }

    public String getMake() {
        return make;
    }

    public CarType getCarType() {
        return carType;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public void setCarType(CarType carType) {
        this.carType = carType;
    }
}
