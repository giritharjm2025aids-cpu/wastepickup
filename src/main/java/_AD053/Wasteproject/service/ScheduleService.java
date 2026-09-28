package _AD053.Wasteproject.service;

import _AD053.Wasteproject.entity.Schedule;
import _AD053.Wasteproject.entity.Zone;
import _AD053.Wasteproject.repository.ScheduleRepository;
import _AD053.Wasteproject.repository.ZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private ZoneRepository zoneRepository;

    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    public Schedule getScheduleById(Long id) {
        return scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found with id: " + id));
    }

    public Schedule createSchedule(Schedule schedule) {
        if (schedule.getZone() == null || schedule.getZone().getId() == null) {
            throw new RuntimeException("Zone is required for schedule.");
        }
        Zone zone = zoneRepository.findById(schedule.getZone().getId())
                .orElseThrow(() -> new RuntimeException("Zone not found with id: " + schedule.getZone().getId()));
        schedule.setZone(zone);

        if (schedule.getPickupDay() == null || schedule.getPickupDay().trim().isEmpty()) {
            throw new RuntimeException("Pickup day is required.");
        }
        schedule.setPickupDay(schedule.getPickupDay().trim().toUpperCase());

        if (schedule.getStartTime() == null || schedule.getEndTime() == null) {
            throw new RuntimeException("Start time and end time are required.");
        }
        if (!schedule.getStartTime().isBefore(schedule.getEndTime())) {
            throw new RuntimeException("Start time must be before end time.");
        }

        return scheduleRepository.save(schedule);
    }

    public Schedule updateSchedule(Long id, Schedule details) {
        Schedule schedule = getScheduleById(id);

        if (details.getZone() != null && details.getZone().getId() != null) {
            Zone zone = zoneRepository.findById(details.getZone().getId())
                    .orElseThrow(() -> new RuntimeException("Zone not found with id: " + details.getZone().getId()));
            schedule.setZone(zone);
        }

        if (details.getPickupDay() != null && !details.getPickupDay().trim().isEmpty()) {
            schedule.setPickupDay(details.getPickupDay().trim().toUpperCase());
        }

        if (details.getStartTime() != null && details.getEndTime() != null) {
            if (!details.getStartTime().isBefore(details.getEndTime())) {
                throw new RuntimeException("Start time must be before end time.");
            }
            schedule.setStartTime(details.getStartTime());
            schedule.setEndTime(details.getEndTime());
        }

        return scheduleRepository.save(schedule);
    }

    public void deleteSchedule(Long id) {
        Schedule schedule = getScheduleById(id);
        scheduleRepository.delete(schedule);
    }
}
