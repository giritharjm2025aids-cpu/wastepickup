package _AD053.Wasteproject.controller;

import _AD053.Wasteproject.entity.Schedule;
import _AD053.Wasteproject.service.ScheduleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    // GET /api/schedules
    @GetMapping
    public ResponseEntity<List<Schedule>> getAllSchedules() {
        List<Schedule> schedules = scheduleService.getAllSchedules();
        return ResponseEntity.ok(schedules);
    }

    // GET /api/schedules/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Schedule> getScheduleById(@PathVariable Long id) {
        Schedule schedule = scheduleService.getScheduleById(id);
        return ResponseEntity.ok(schedule);
    }

    // POST /api/schedules
    @PostMapping
    public ResponseEntity<Schedule> createSchedule(@Valid @RequestBody Schedule schedule) {
        Schedule created = scheduleService.createSchedule(schedule);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // PUT /api/schedules/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Schedule> updateSchedule(@PathVariable Long id, @Valid @RequestBody Schedule schedule) {
        Schedule updated = scheduleService.updateSchedule(id, schedule);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/schedules/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }
}
