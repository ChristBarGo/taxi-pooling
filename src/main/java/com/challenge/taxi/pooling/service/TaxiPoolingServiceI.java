package com.challenge.taxi.pooling.service;

import com.challenge.taxi.pooling.model.Group;
import com.challenge.taxi.pooling.model.Journey;
import com.challenge.taxi.pooling.model.Taxi;

import java.util.List;

public interface TaxiPoolingServiceI {
    void saveTaxisToRepository(List<Taxi> taxiList);
    List<Taxi> getTaxisFromRepository();
    void clearRepositoriesAndQueue();
    void insertTaxiListToRepository(List<Taxi> taxiList);
    void addGroupToQueue(Group group);
    void requestJourneyByGroup(Group group);
    Journey createJourneyForTaxiAndGroup(Taxi taxi, Group group);
    void requestDropoffByGroupId(long groupId);
    Taxi locateGroupByGroupId(long groupId) throws Exception;
    void deleteTaxisFromRepository();
}
