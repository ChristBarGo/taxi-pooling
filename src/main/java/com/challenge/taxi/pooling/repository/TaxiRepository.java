package com.challenge.taxi.pooling.repository;
import com.challenge.taxi.pooling.model.Taxi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaxiRepository extends JpaRepository<Taxi, Long> {
    List<Taxi> findAllByAvailableSeatsGreaterThan(int requestedSeats);
    @Modifying
    @Query("update Taxi t set t.availableSeats = :availableSeats where t.id = :id")
    void updateAvailableSeatsById(@Param(value = "id") long id, @Param(value = "availableSeats") int availableSeats);
}
