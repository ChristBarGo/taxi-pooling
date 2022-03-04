package com.challenge.taxi.pooling.model;

import org.hibernate.validator.constraints.Range;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Journey {
    private @Id @GeneratedValue Long id;
    private long taxiId;
    @Column(unique = true)
    private long groupId;
    @Range(min = 1, max = 6)
    private int occupiedSeats;

    public Journey() {

    }

    public Journey(long taxiId, long groupId, int occupiedSeats) {
        this.taxiId = taxiId;
        this.groupId = groupId;
        this.occupiedSeats = occupiedSeats;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTaxiId() {
        return taxiId;
    }

    public void setTaxiId(Long taxiId) {
        this.taxiId = taxiId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public int getOccupiedSeats() {
        return occupiedSeats;
    }

    public void setOccupiedSeats(int occupiedSeats) {
        this.occupiedSeats = occupiedSeats;
    }
}


