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
        Zone zone1 = new Zone();
        zone1.setName("Zone 1");
        zone1 = zoneRepository.save(zone1);

        Zone zone2 = new Zone();
        zone2.setName("Zone 2");
        zone2 = zoneRepository.save(zone2);

        // 2. Schedules
        Schedule s1 = new Schedule();
        s1.setZone(zone1);
        s1.setPickupDay("MONDAY");
        s1.setStartTime(LocalTime.of(9, 0));
        s1.setEndTime(LocalTime.of(11, 0));
        s1 = scheduleRepository.save(s1);

        Schedule s2 = new Schedule();
        s2.setZone(zone1);
        s2.setPickupDay("WEDNESDAY");
        s2.setStartTime(LocalTime.of(9, 0));
        s2.setEndTime(LocalTime.of(11, 0));
        s2 = scheduleRepository.save(s2);

        Schedule s3 = new Schedule();
        s3.setZone(zone2);
        s3.setPickupDay("TUESDAY");
        s3.setStartTime(LocalTime.of(10, 0));
        s3.setEndTime(LocalTime.of(12, 0));
        s3 = scheduleRepository.save(s3);

        // 3. Households
        Household ravi = new Household();
        ravi.setName("Ravi Family");
        ravi.setAddress("12 Gandhi Street");
        ravi.setPhone("9876543210");
        ravi.setZone(zone1);
        ravi.setMinimumScore(50.0);
        ravi = householdRepository.save(ravi);

        Household kumar = new Household();
        kumar.setName("Kumar Family");
        kumar.setAddress("45 Anna Nagar");
        kumar.setPhone("9876543211");
        kumar.setZone(zone1);
        kumar.setMinimumScore(50.0);
        kumar = householdRepository.save(kumar);

        Household priya = new Household();
        priya.setName("Priya Family");
        priya.setAddress("78 Beach Road");
        priya.setPhone("9876543212");
        priya.setZone(zone2);
        priya.setMinimumScore(60.0);
        priya = householdRepository.save(priya);

        // 4. Pickup Logs
        LocalDate lastMonday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate prevMonday = lastMonday.minusWeeks(1);
        LocalDate lastWednesday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.WEDNESDAY));
        LocalDate prevWednesday = lastWednesday.minusWeeks(1);
        LocalDate lastTuesday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.TUESDAY));

        savePickupLog(ravi, s1, lastMonday, LocalTime.of(9, 30), 90);
        savePickupLog(ravi, s1, prevMonday, LocalTime.of(10, 15), 80);
        savePickupLog(kumar, s2, lastWednesday, LocalTime.of(9, 45), 45);
        savePickupLog(kumar, s2, prevWednesday, LocalTime.of(10, 30), 35);
        savePickupLog(priya, s3, lastTuesday, LocalTime.of(10, 45), 70);
    }

    private void savePickupLog(Household household, Schedule schedule, LocalDate pickupDate, LocalTime pickupTime, Integer score) {
        PickupLog log = new PickupLog();
        log.setHousehold(household);
        log.setSchedule(schedule);
        log.setPickupDate(pickupDate);
        log.setPickupTime(pickupTime);
        log.setSegregationScore(score);
        pickupLogRepository.save(log);
    }
}
