package com.challenge.taxi.pooling.controller;

import com.challenge.taxi.pooling.model.Group;
import com.challenge.taxi.pooling.model.Taxi;
import com.challenge.taxi.pooling.service.TaxiPoolingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.NoResultException;
import javax.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
public class TaxiPoolingController {

    @Autowired
    private TaxiPoolingService taxiPoolingService;

    @GetMapping("/status")
    public ResponseEntity getStatus() {
        return ResponseEntity.ok("Server is running");
    }

    @PutMapping(
            path = "/taxis",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity replaceTaxiList(@Valid @RequestBody List<Taxi> taxiList) {
        taxiPoolingService.replaceTaxiList(taxiList);

        return ResponseEntity.ok(null);
    }

    @GetMapping(
            path = "/taxis",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getTaxis() {
        try {
            return ResponseEntity.ok(taxiPoolingService.getTaxiList());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No taxi found");
        }
    }


    @PostMapping(
            path = "/journey",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity requestJourney(@Valid @RequestBody Group group) {
        try {
            taxiPoolingService.requestJourneyByGroup(group);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.ok(null);
    }

    @PostMapping(
            path = "/dropoff",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity requestDropOff(@RequestParam long ID) {
        try {
            taxiPoolingService.requestDropoffByGroupId(ID);
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.ok(null);
    }

    @PostMapping(
            path = "/locate",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity locateGroup(@RequestParam long ID) {
        Taxi assignedTaxiForGroup = null;
        try {
            assignedTaxiForGroup = taxiPoolingService.locateGroupByGroupId(ID);
        }
        catch (NoResultException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(e.getMessage());
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.ok(assignedTaxiForGroup);
    }

}
