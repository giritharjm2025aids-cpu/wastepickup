package _AD053.Wasteproject.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

@Entity
@Table(name = "schedules")
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Zone is required")
    @ManyToOne
    @JoinColumn(name = "zone_id", nullable = false)
    private Zone zone;

    @NotBlank(message = "Pickup day is required")
    @Column(name = "pickup_day", nullable = false)
    private String pickupDay;

    @NotNull(message = "Start time is required")
    @JsonFormat(pattern = "HH:mm")
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    @JsonFormat(pattern = "HH:mm")
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    // Default constructor
    public Schedule() {
    }

    // Constructor without id
    public Schedule(Zone zone, String pickupDay, LocalTime startTime, LocalTime endTime) {
        this.zone = zone;
        this.pickupDay = pickupDay;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Constructor with all fields
    public Schedule(Long id, Zone zone, String pickupDay, LocalTime startTime, LocalTime endTime) {
        this.id = id;
        this.zone = zone;
        this.pickupDay = pickupDay;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Zone getZone() {
        return zone;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }

    public String getPickupDay() {
        return pickupDay;
    }

    public void setPickupDay(String pickupDay) {
        this.pickupDay = pickupDay;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "id=" + id +
                ", zone=" + (zone != null ? zone.getName() : null) +
                ", pickupDay='" + pickupDay + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
