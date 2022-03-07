package com.challenge.taxi.pooling.model;

import org.hibernate.validator.constraints.Range;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
public class Taxi {
    @NotNull(message =  "Taxi id is mandatory")
    private @Id long id;
    @Range(min = 4, max = 6)
    private int seats;
    private int availableSeats;

    public Taxi() {
    }

    public Taxi(long id, int seats) {
        this.id = id;
        this.seats = seats;
        this.availableSeats = seats;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
        setAvailableSeats(seats);
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }
}
