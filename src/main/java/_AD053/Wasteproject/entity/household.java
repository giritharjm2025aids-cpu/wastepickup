package _AD053.Wasteproject.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "households")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Household {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String address;
    private String phone;

    @ManyToOne
    private Zone zone;

    private Double minimumScore = 50.0;

    @Transient
    private Double averageScore;

    @Transient
    private String status;

    @JsonCreator
    public Household(String value) {
        if (value != null && !value.trim().isEmpty()) {
            try {
                this.id = Long.parseLong(value.trim());
            } catch (NumberFormatException e) {
                this.name = value.trim();
            }
        }
    }

    public Household(Long id) {
        this.id = id;
    }
}
