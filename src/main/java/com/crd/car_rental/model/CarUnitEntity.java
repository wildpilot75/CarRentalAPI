package com.crd.car_rental.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "CarUnit")
public class CarUnitEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long carEntityId;
    private String vin;

    @OneToMany(mappedBy = "carUnit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservationEntity>  reservations;

    public Long getId() {
        return id;
    }

    public Long getCarEntityId() {
        return carEntityId;
    }

    public String getVin() {
        return vin;
    }

    public List<ReservationEntity> getReservations() {
        return reservations;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCarEntityId(Long carEntityId) {
        this.carEntityId = carEntityId;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public void setReservations(List<ReservationEntity> reservations) {
        this.reservations = reservations;
    }
}
