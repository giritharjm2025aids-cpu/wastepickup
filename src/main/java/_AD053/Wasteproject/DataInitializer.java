package _AD053.Wasteproject;

import _AD053.Wasteproject.entity.Household;
import _AD053.Wasteproject.entity.PickupLog;
import _AD053.Wasteproject.entity.Schedule;
import _AD053.Wasteproject.entity.Zone;
import _AD053.Wasteproject.repository.HouseholdRepository;
import _AD053.Wasteproject.repository.PickupLogRepository;
import _AD053.Wasteproject.repository.ScheduleRepository;
import _AD053.Wasteproject.repository.ZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private ZoneRepository zoneRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private HouseholdRepository householdRepository;

    @Autowired
    private PickupLogRepository pickupLogRepository;

    @Override
    public void run(String... args) {
        // Only seed data if database is empty
        if (zoneRepository.count() > 0) {
            return;
        }

        // 1. Zones
        Zone zone1 = zoneRepository.save(new Zone("Zone 1"));
        Zone zone2 = zoneRepository.save(new Zone("Zone 2"));

        // 2. Schedules
        // Zone 1 - MONDAY - 09:00 - 11:00
        Schedule s1 = scheduleRepository.save(new Schedule(
                zone1, "MONDAY", LocalTime.of(9, 0), LocalTime.of(11, 0)
        ));
        // Zone 1 - WEDNESDAY - 09:00 - 11:00
        Schedule s2 = scheduleRepository.save(new Schedule(
                zone1, "WEDNESDAY", LocalTime.of(9, 0), LocalTime.of(11, 0)
        ));
        // Zone 2 - TUESDAY - 10:00 - 12:00
        Schedule s3 = scheduleRepository.save(new Schedule(
                zone2, "TUESDAY", LocalTime.of(10, 0), LocalTime.of(12, 0)
        ));

        // 3. Households
        Household ravi = householdRepository.save(new Household(
                "Ravi Family", "12 Gandhi Street", "9876543210", zone1, 50.0
        ));
        Household kumar = householdRepository.save(new Household(
                "Kumar Family", "45 Anna Nagar", "9876543211", zone1, 50.0
        ));
        Household priya = householdRepository.save(new Household(
                "Priya Family", "78 Beach Road", "9876543212", zone2, 60.0
        ));

        // 4. Pickup Logs
        // Recent Monday and Wednesday dates matching the schedule days
        LocalDate lastMonday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate prevMonday = lastMonday.minusWeeks(1);
        LocalDate lastWednesday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.WEDNESDAY));
        LocalDate prevWednesday = lastWednesday.minusWeeks(1);
        LocalDate lastTuesday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.TUESDAY));

        // Ravi Family: Score 90 & 80 -> Average 85 (>= 50 NORMAL)
        pickupLogRepository.save(new PickupLog(
                ravi, s1, lastMonday, LocalTime.of(9, 30), 90
        ));
        pickupLogRepository.save(new PickupLog(
                ravi, s1, prevMonday, LocalTime.of(10, 15), 80
        ));

        // Kumar Family: Score 45 & 35 -> Average 40 (< 50 FLAGGED!)
        pickupLogRepository.save(new PickupLog(
                kumar, s2, lastWednesday, LocalTime.of(9, 45), 45
        ));
        pickupLogRepository.save(new PickupLog(
                kumar, s2, prevWednesday, LocalTime.of(10, 30), 35
        ));

        // Priya Family: Score 70 -> Average 70 (>= 60 NORMAL)
        pickupLogRepository.save(new PickupLog(
                priya, s3, lastTuesday, LocalTime.of(10, 45), 70
        ));
    }
}
