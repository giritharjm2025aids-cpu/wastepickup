package com.example._AD053.Wasteproject.controller;

import com.example._AD053.Wasteproject.entity.Household;
import com.example._AD053.Wasteproject.entity.PickupLog;
import com.example._AD053.Wasteproject.entity.Schedule;
import com.example._AD053.Wasteproject.entity.Zone;
import com.example._AD053.Wasteproject.service.WastePickupService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class WastePickupController {

    private final WastePickupService service;

    public WastePickupController(WastePickupService service) {
        this.service = service;
    }

    // ---------------- ZONE ----------------

    @GetMapping("/zones")
    public List<Zone> getZones() {
        return service.getZones();
    }

    @PostMapping("/zones")
    @ResponseStatus(HttpStatus.CREATED)
    public Zone createZone(@Valid @RequestBody Zone zone) {
        return service.createZone(zone);
    }

    // ---------------- HOUSEHOLD ----------------

    @GetMapping("/households")
    public List<Household> getHouseholds() {
        return service.getHouseholds();
    }

    @PostMapping("/households/{zoneId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Household createHousehold(
            @PathVariable Long zoneId,
            @Valid @RequestBody Household household) {

        return service.createHousehold(zoneId, household);
    }

    // ---------------- SCHEDULE ----------------

    @GetMapping("/schedules")
    public List<Schedule> getSchedules() {
        return service.getSchedules();
    }

    @PostMapping("/schedules/{zoneId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Schedule createSchedule(
            @PathVariable Long zoneId,
            @Valid @RequestBody Schedule schedule) {

        return service.createSchedule(zoneId, schedule);
    }

    // ---------------- PICKUP ----------------

    @GetMapping("/pickups")
    public List<PickupLog> getPickups() {
        return service.getPickups();
    }

    @PostMapping("/pickups/{householdId}")
    @ResponseStatus(HttpStatus.CREATED)
    public PickupLog createPickup(
            @PathVariable Long householdId,
            @Valid @RequestBody PickupLog pickup) {

        return service.createPickup(householdId, pickup);
    }

    // ---------------- HOUSEHOLD AVERAGE ----------------

    @GetMapping("/households/{id}/average")
    public Map<String, Object> getHouseholdAverage(
            @PathVariable Long id) {

        return Map.of(
                "householdId", id,
                "averageScore", service.householdAverage(id)
        );
    }

    // ---------------- ZONE AVERAGE ----------------

    @GetMapping("/zones/{id}/average")
    public Map<String, Object> getZoneAverage(
            @PathVariable Long id) {

        return Map.of(
                "zoneId", id,
                "averageScore", service.zoneAverage(id)
        );
    }

    // ---------------- FLAGGED HOUSEHOLDS ----------------

    @GetMapping("/households/flagged")
    public List<Household> getFlaggedHouseholds() {
        return service.flaggedHouseholds();
    }
}