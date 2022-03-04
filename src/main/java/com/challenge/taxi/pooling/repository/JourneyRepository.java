package com.challenge.taxi.pooling.repository;

import com.challenge.taxi.pooling.model.Journey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JourneyRepository extends JpaRepository<Journey, Long> {
    boolean existsByGroupId(long groupId);
    Journey findByGroupId(long groupId);
}
