package _AD053.Wasteproject.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "households")
public class Household {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Household name is required")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Address is required")
    @Column(nullable = false)
    private String address;

    @NotBlank(message = "Phone is required")
    @Column(nullable = false)
    private String phone;

    @NotNull(message = "Zone is required")
    @ManyToOne
    @JoinColumn(name = "zone_id", nullable = false)
    private Zone zone;

    @NotNull(message = "Minimum score is required")
    @Column(name = "minimum_score", nullable = false)
    private Double minimumScore = 50.0;

    // Transient fields: NOT saved in the database
    // Computed dynamically from PickupLog records for UI display
    @Transient
    private Double averageScore;

    @Transient
    private String status;

    // Default constructor
    public Household() {
    }

    // Constructor without id
    public Household(String name, String address, String phone, Zone zone, Double minimumScore) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.zone = zone;
        this.minimumScore = (minimumScore != null) ? minimumScore : 50.0;
    }

    // Constructor with id
    public Household(Long id, String name, String address, String phone, Zone zone, Double minimumScore) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.zone = zone;
        this.minimumScore = (minimumScore != null) ? minimumScore : 50.0;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Zone getZone() {
        return zone;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }

    public Double getMinimumScore() {
        return minimumScore;
    }

    public void setMinimumScore(Double minimumScore) {
        this.minimumScore = minimumScore;
    }

    public Double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(Double averageScore) {
        this.averageScore = averageScore;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Household{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", zone=" + (zone != null ? zone.getName() : null) +
                ", minimumScore=" + minimumScore +
                '}';
    }
}
