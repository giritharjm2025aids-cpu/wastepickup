package com.example._AD053.Wasteproject.repository;

import com.example._AD053.Wasteproject.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByZoneIdAndDayOfWeek(
            Long zoneId,
            DayOfWeek dayOfWeek
    );
}