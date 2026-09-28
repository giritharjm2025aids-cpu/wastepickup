package _AD053.Wasteproject.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "zones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Zone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @JsonCreator
    public Zone(String value) {
        if (value != null && !value.trim().isEmpty()) {
            try {
                this.id = Long.parseLong(value.trim());
            } catch (NumberFormatException e) {
                this.name = value.trim();
            }
        }
    }

    public Zone(Long id) {
        this.id = id;
    }
}
