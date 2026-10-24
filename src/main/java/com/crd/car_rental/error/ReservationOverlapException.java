package com.crd.car_rental.error;

public class ReservationOverlapException extends RuntimeException {
    public ReservationOverlapException(String message) { super(message); }
}
