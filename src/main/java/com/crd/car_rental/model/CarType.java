package com.crd.car_rental.model;

public enum CarType {
    SEDAN (1),
    SUV (2),
    VAN (3);

    private final int carType;

    CarType(int carType) {
        this.carType = carType;
    }

    public int getCarType() {
        return this.carType;
    }
}
