package _AD053.Wasteproject.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "pickup_logs")
public class PickupLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Household is required")
    @ManyToOne
    @JoinColumn(name = "household_id", nullable = false)
    private Household household;

    @NotNull(message = "Schedule is required")
    @ManyToOne
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @NotNull(message = "Pickup date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "pickup_date", nullable = false)
    private LocalDate pickupDate;

    @NotNull(message = "Pickup time is required")
    @JsonFormat(pattern = "HH:mm")
    @Column(name = "pickup_time", nullable = false)
    private LocalTime pickupTime;

    @NotNull(message = "Segregation score is required")
    @Min(value = 0, message = "Segregation score must be between 0 and 100.")
    @Max(value = 100, message = "Segregation score must be between 0 and 100.")
    @Column(name = "segregation_score", nullable = false)
    private Integer segregationScore;

    // Default constructor
    public PickupLog() {
    }

    // Constructor without id
    public PickupLog(Household household, Schedule schedule, LocalDate pickupDate, LocalTime pickupTime, Integer segregationScore) {
        this.household = household;
        this.schedule = schedule;
        this.pickupDate = pickupDate;
        this.pickupTime = pickupTime;
        this.segregationScore = segregationScore;
    }

    // Constructor with all fields
    public PickupLog(Long id, Household household, Schedule schedule, LocalDate pickupDate, LocalTime pickupTime, Integer segregationScore) {
        this.id = id;
        this.household = household;
        this.schedule = schedule;
        this.pickupDate = pickupDate;
        this.pickupTime = pickupTime;
        this.segregationScore = segregationScore;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Household getHousehold() {
        return household;
    }

    public void setHousehold(Household household) {
        this.household = household;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public LocalDate getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(LocalDate pickupDate) {
        this.pickupDate = pickupDate;
    }

    public LocalTime getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(LocalTime pickupTime) {
        this.pickupTime = pickupTime;
    }

    public Integer getSegregationScore() {
        return segregationScore;
    }

    public void setSegregationScore(Integer segregationScore) {
        this.segregationScore = segregationScore;
    }

    @Override
    public String toString() {
        return "PickupLog{" +
                "id=" + id +
                ", household=" + (household != null ? household.getName() : null) +
                ", schedule=" + (schedule != null ? schedule.getId() : null) +
                ", pickupDate=" + pickupDate +
                ", pickupTime=" + pickupTime +
                ", segregationScore=" + segregationScore +
                '}';
    }
}
