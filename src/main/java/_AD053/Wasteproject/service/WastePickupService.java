package com.example._AD053.Wasteproject.service;

import com.example._AD053.Wasteproject.entity.Household;
import com.example._AD053.Wasteproject.entity.PickupLog;
import com.example._AD053.Wasteproject.entity.Schedule;
import com.example._AD053.Wasteproject.entity.Zone;
import com.example._AD053.Wasteproject.repository.HouseholdRepository;
import com.example._AD053.Wasteproject.repository.PickupLogRepository;
import com.example._AD053.Wasteproject.repository.ScheduleRepository;
import com.example._AD053.Wasteproject.repository.ZoneRepository;

import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class WastePickupService {

    private final ZoneRepository zoneRepository;
    private final HouseholdRepository householdRepository;
    private final ScheduleRepository scheduleRepository;
    private final PickupLogRepository pickupLogRepository;

    public WastePickupService(
            ZoneRepository zoneRepository,
            HouseholdRepository householdRepository,
            ScheduleRepository scheduleRepository,
            PickupLogRepository pickupLogRepository) {

        this.zoneRepository = zoneRepository;
        this.householdRepository = householdRepository;
        this.scheduleRepository = scheduleRepository;
        this.pickupLogRepository = pickupLogRepository;
    }

    // ==================== ZONE ====================

    public List<Zone> getZones() {
        return zoneRepository.findAll();
    }

    public Zone createZone(Zone zone) {
        return zoneRepository.save(zone);
    }

    // ==================== HOUSEHOLD ====================

    public List<Household> getHouseholds() {
        return householdRepository.findAll();
    }

    public Household createHousehold(
            Long zoneId,
            Household household) {

        Zone zone = zoneRepository.findById(zoneId)
                .orElseThrow(() ->
                        new RuntimeException("Zone not found"));

        household.setZone(zone);

        return householdRepository.save(household);
    }

    // ==================== SCHEDULE ====================

    public List<Schedule> getSchedules() {
        return scheduleRepository.findAll();
    }

    public Schedule createSchedule(
            Long zoneId,
            Schedule schedule) {

        Zone zone = zoneRepository.findById(zoneId)
                .orElseThrow(() ->
                        new RuntimeException("Zone not found"));

        if (!schedule.getStartTime()
                .isBefore(schedule.getEndTime())) {

            throw new RuntimeException(
                    "Start time must be before end time");
        }

        schedule.setZone(zone);

        return scheduleRepository.save(schedule);
    }

    // ==================== PICKUP ====================

    public List<PickupLog> getPickups() {
        return pickupLogRepository.findAll();
    }

    public PickupLog createPickup(
            Long householdId,
            PickupLog pickup) {

        Household household =
                householdRepository.findById(householdId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Household not found"));

        LocalDateTime pickupTime =
                pickup.getPickupTime();

        if (pickupTime == null) {
            pickupTime = LocalDateTime.now();
        }

        DayOfWeek day =
                pickupTime.getDayOfWeek();

        LocalTime time =
                pickupTime.toLocalTime();

        List<Schedule> schedules =
                scheduleRepository
                        .findByZoneIdAndDayOfWeek(
                                household.getZone().getId(),
                                day
                        );

        boolean validTime = schedules.stream()
                .anyMatch(schedule ->
                        !time.isBefore(
                                schedule.getStartTime())
                                &&
                                !time.isAfter(
                                        schedule.getEndTime())
                );

        if (!validTime) {
            throw new RuntimeException(
                    "Pickup can be recorded only within " +
                            "the scheduled time for this zone."
            );
        }

        pickup.setHousehold(household);
        pickup.setPickupTime(pickupTime);

        PickupLog savedPickup =
                pickupLogRepository.save(pickup);

        // Calculate average segregation score
        Double averageScore =
                pickupLogRepository
                        .averageScore(householdId);

        // Flag household if score is below 50
        if (averageScore != null &&
                averageScore < 50) {

            household.setFlagged(true);

        } else {

            household.setFlagged(false);
        }

        householdRepository.save(household);

        return savedPickup;
    }

    // ==================== HOUSEHOLD AVERAGE ====================

    public Double householdAverage(Long householdId) {

        Double average =
                pickupLogRepository
                        .averageScore(householdId);

        if (average == null) {
            return 0.0;
        }

        return average;
    }

    // ==================== ZONE AVERAGE ====================

    public Double zoneAverage(Long zoneId) {

        Double average =
                pickupLogRepository
                        .zoneAverageScore(zoneId);

        if (average == null) {
            return 0.0;
        }

        return average;
    }

    // ==================== FLAGGED HOUSEHOLDS ====================

    public List<Household> flaggedHouseholds() {

        return householdRepository.findAll()
                .stream()
                .filter(Household::isFlagged)
                .toList();
    }
}