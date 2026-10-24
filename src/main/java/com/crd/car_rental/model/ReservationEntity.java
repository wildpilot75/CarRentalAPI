package com.crd.car_rental.model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name="Reservation")
public class ReservationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "CarUnit_id")
    private CarUnitEntity carUnit;

    private Date startDate;
    private Date endDate;

    public Long getId() {
        return id;
    }

    public CarUnitEntity getCarUnit() {
        return carUnit;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCarUnit(CarUnitEntity carUnit) {
        this.carUnit = carUnit;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
