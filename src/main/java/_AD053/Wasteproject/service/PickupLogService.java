package _AD053.Wasteproject.service;

import _AD053.Wasteproject.entity.Household;
import _AD053.Wasteproject.entity.PickupLog;
import _AD053.Wasteproject.entity.Schedule;
import _AD053.Wasteproject.repository.HouseholdRepository;
import _AD053.Wasteproject.repository.PickupLogRepository;
import _AD053.Wasteproject.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
public class PickupLogService {

    @Autowired
    private PickupLogRepository pickupLogRepository;

    @Autowired
    private HouseholdRepository householdRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    public List<PickupLog> getAllPickupLogs() {
        return pickupLogRepository.findAll();
    }

    public PickupLog getPickupLogById(Long id) {
        return pickupLogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pickup log not found with id: " + id));
    }

    public PickupLog createPickupLog(PickupLog pickupLog) {
        // Validate score
        if (pickupLog.getSegregationScore() == null) {
            throw new RuntimeException("Segregation score is required.");
        }
        if (pickupLog.getSegregationScore() < 0 || pickupLog.getSegregationScore() > 100) {
            throw new RuntimeException("Segregation score must be between 0 and 100.");
        }

        // 1. Find the Household
        if (pickupLog.getHousehold() == null || pickupLog.getHousehold().getId() == null) {
            throw new RuntimeException("Household is required.");
        }
        Household household = householdRepository.findById(pickupLog.getHousehold().getId())
                .orElseThrow(() -> new RuntimeException("Household not found with id: " + pickupLog.getHousehold().getId()));

        // 2. Find the Schedule
        if (pickupLog.getSchedule() == null || pickupLog.getSchedule().getId() == null) {
            throw new RuntimeException("Schedule is required.");
        }
        Schedule schedule = scheduleRepository.findById(pickupLog.getSchedule().getId())
                .orElseThrow(() -> new RuntimeException("Schedule not found with id: " + pickupLog.getSchedule().getId()));

        // 3. Check that household.zone.id == schedule.zone.id
        if (household.getZone() == null || schedule.getZone() == null ||
                !household.getZone().getId().equals(schedule.getZone().getId())) {
            throw new RuntimeException("Household and schedule must belong to the same zone.");
        }

        // 4. Check pickupDate day matches schedule.pickupDay
        if (pickupLog.getPickupDate() == null) {
            throw new RuntimeException("Pickup date is required.");
        }
        String dayOfWeek = pickupLog.getPickupDate().getDayOfWeek().name();
        if (!dayOfWeek.equalsIgnoreCase(schedule.getPickupDay().trim())) {
            throw new RuntimeException("Pickup can only be recorded during the scheduled time window for this zone.");
        }

        // 5. Check pickupTime is between schedule.startTime and schedule.endTime
        if (pickupLog.getPickupTime() == null) {
            throw new RuntimeException("Pickup time is required.");
        }
        LocalTime pickupTime = pickupLog.getPickupTime();
        if (pickupTime.isBefore(schedule.getStartTime()) || pickupTime.isAfter(schedule.getEndTime())) {
            throw new RuntimeException("Pickup can only be recorded during the scheduled time window for this zone.");
        }

        pickupLog.setHousehold(household);
        pickupLog.setSchedule(schedule);

        return pickupLogRepository.save(pickupLog);
    }

    public PickupLog updatePickupLog(Long id, PickupLog details) {
        PickupLog existing = getPickupLogById(id);

        if (details.getSegregationScore() != null) {
            if (details.getSegregationScore() < 0 || details.getSegregationScore() > 100) {
                throw new RuntimeException("Segregation score must be between 0 and 100.");
            }
            existing.setSegregationScore(details.getSegregationScore());
        }

        Household household = existing.getHousehold();
        if (details.getHousehold() != null && details.getHousehold().getId() != null) {
            household = householdRepository.findById(details.getHousehold().getId())
                    .orElseThrow(() -> new RuntimeException("Household not found with id: " + details.getHousehold().getId()));
        }

        Schedule schedule = existing.getSchedule();
        if (details.getSchedule() != null && details.getSchedule().getId() != null) {
            schedule = scheduleRepository.findById(details.getSchedule().getId())
                    .orElseThrow(() -> new RuntimeException("Schedule not found with id: " + details.getSchedule().getId()));
        }

        // Check zone match
        if (household.getZone() == null || schedule.getZone() == null ||
                !household.getZone().getId().equals(schedule.getZone().getId())) {
            throw new RuntimeException("Household and schedule must belong to the same zone.");
        }

        // Check date and day match
        if (details.getPickupDate() != null) {
            String dayOfWeek = details.getPickupDate().getDayOfWeek().name();
            if (!dayOfWeek.equalsIgnoreCase(schedule.getPickupDay().trim())) {
                throw new RuntimeException("Pickup can only be recorded during the scheduled time window for this zone.");
            }
            existing.setPickupDate(details.getPickupDate());
        }

        // Check time
        if (details.getPickupTime() != null) {
            LocalTime pickupTime = details.getPickupTime();
            if (pickupTime.isBefore(schedule.getStartTime()) || pickupTime.isAfter(schedule.getEndTime())) {
                throw new RuntimeException("Pickup can only be recorded during the scheduled time window for this zone.");
            }
            existing.setPickupTime(details.getPickupTime());
        }

        existing.setHousehold(household);
        existing.setSchedule(schedule);

        return pickupLogRepository.save(existing);
    }

    public void deletePickupLog(Long id) {
        PickupLog log = getPickupLogById(id);
        pickupLogRepository.delete(log);
    }
}
