package com.challenge.taxi.pooling.service;

import com.challenge.taxi.pooling.model.Group;
import com.challenge.taxi.pooling.model.Journey;
import com.challenge.taxi.pooling.model.Taxi;
import com.challenge.taxi.pooling.repository.JourneyRepository;
import com.challenge.taxi.pooling.repository.TaxiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.NoResultException;
import java.util.*;

@Service
public class TaxiPoolingService implements TaxiPoolingServiceI {
    @Autowired
    private TaxiRepository taxiRepository;
    @Autowired
    private JourneyRepository journeyRepository;
    private Queue<Group> waitingGroupsQueue = new LinkedList<>();

    public void replaceTaxiList(List<Taxi> taxiList) {
        if (taxiList.size() > 0) {
            clearRepositoriesAndQueue();
            this.insertTaxiListToRepository(taxiList);
        }
        else {
            throw new IllegalStateException();
        }
    }

    public List<Taxi> getTaxiList() {
        return taxiRepository.findAll();
    }

    @Override
    public void clearRepositoriesAndQueue() {
        taxiRepository.deleteAllInBatch();
        journeyRepository.deleteAllInBatch();
        waitingGroupsQueue.clear();
    }

    public void insertTaxiListToRepository(List<Taxi> taxiList) {
        taxiRepository.saveAll(taxiList);
    }

    @Override
    public void requestJourneyByGroup(Group group) {
        if (group != null) {
            if (checkIfGroupIsInJourney(group)) {
                throw new IllegalArgumentException("The group with id " + group.getId() + " already exists in the system");
            }
            else {
                Group queueGroup = getGroupInQueueByGroupId(group.getId());

                if (queueGroup == null) {
                    addGroupToQueue(group);
                }
                else {
                    group = queueGroup;
                }

                List<Journey> journeyList = assignJourneyForWaitingGroups();
                saveJourneysToRepository(journeyList);
            }
        }
        else {
            throw new IllegalArgumentException("Group passed as argument has no data");
        }
    }

    private List<Journey> assignJourneyForWaitingGroups() {
        List<Journey> journeysToBeProcessed = new ArrayList<>();
        List<Taxi> taxiList = taxiRepository.findAllByAvailableSeatsGreaterThan(0);

        if (taxiList.size() > 0) {
            for (Group group : waitingGroupsQueue) {
                Taxi assignedTaxi = findTaxiForGroup(taxiList, group.getPeople());
                Journey journey = this.createJourneyForTaxiAndGroup(assignedTaxi, group);

                if (journey != null) {
                    updateTaxiAvailableSeats(assignedTaxi, group.getPeople());
                    journeysToBeProcessed.add(journey);
                    waitingGroupsQueue.poll();
                }
            }
        }

        return journeysToBeProcessed;
    }

    private void updateTaxiAvailableSeats(Taxi taxi, int occupiedSeats) {
        taxi.setAvailableSeats(taxi.getAvailableSeats() - occupiedSeats);
        taxiRepository.save(taxi);
    }

    private boolean checkIfGroupInQueueHasJourney(Group group, List<Journey> journeyList) {
        boolean groupHasJourney = false;

        for (Journey journey : journeyList) {
            if (journey.getGroupId().equals(group.getId())) {
                groupHasJourney = true;
                break;
            }
        }

        return groupHasJourney;
    }

    private void saveJourneysToRepository(List<Journey> journeyList) {
        journeyRepository.saveAll(journeyList);
    }

    private Taxi findTaxiForGroup(List<Taxi> taxiList, int groupPeople) {
        Taxi foundTaxi = null;

        for(Taxi taxi : taxiList) {
            if (taxi.getAvailableSeats() >= groupPeople) {
                foundTaxi = taxi;
                break;
            }
        }

        return foundTaxi;
    }

    public Journey createJourneyForTaxiAndGroup(Taxi taxi, Group group) {
        Journey journey = null;

        if (taxi != null) {
            journey = new Journey(taxi.getId(), group.getId(), group.getPeople());
        }

        return journey;
    }


    private boolean checkIfGroupIsInJourney(Group group) {
        return journeyRepository.existsByGroupId(group.getId());
    }

    @Override
    public void addGroupToQueue(Group group) {
        waitingGroupsQueue.add(group);
    }

    @Override
    public void requestDropoffByGroupId(long groupId) {
        Group groupInQueue = getGroupInQueueByGroupId(groupId);

        if (groupInQueue == null) {
            Journey journey = getJourneyByGroupId(groupId);

            if (journey != null) {
                updateTaxiSeatsByJourney(journey);
                journeyRepository.delete(journey);
            }
            else {
                throw new NoSuchElementException("Group with id " + groupId + " not found");
            }
        }
        else {
            waitingGroupsQueue.remove(groupInQueue);
        }
    }

    private void dropoffGroupByJourney(Journey journey) {

    }

    private Group getGroupInQueueByGroupId(long groupId) {
        Group foundGroup = null;

        for (Group group : waitingGroupsQueue) {
            if (group.getId().equals(groupId)) {
                foundGroup = group;
                break;
            }
        }

        return foundGroup;
    }

    private Journey getJourneyByGroupId(long groupId) {
        return journeyRepository.findByGroupId(groupId);
    }

    private void updateTaxiSeatsByJourney(Journey journey) {
        Taxi taxi = taxiRepository.findById(journey.getTaxiId()).orElse(null);

        if (taxi != null) {
             taxi.setAvailableSeats(taxi.getAvailableSeats() + journey.getOccupiedSeats());
        }
    }

    @Override
    public Taxi locateGroupByGroupId(long groupId) throws Exception {
        Journey journey = journeyRepository.findByGroupId(groupId);

        if (journey != null) {
            Taxi taxi = taxiRepository.findById(journey.getTaxiId()).orElse(null);

            if (taxi != null) {
                return taxi;
            }
            else {
                throw new Exception("There is no taxi assigned for journey with id" + journey.getId() + " and group with id " + groupId);
            }
        }
        else {
            if (getGroupInQueueByGroupId(groupId) != null) {
                throw new NoResultException("The group with id " + groupId + " is still waiting to be served");
            }
            else {
                throw new NoSuchElementException("The group with id " + groupId + " not found");
            }
        }
    }
}
