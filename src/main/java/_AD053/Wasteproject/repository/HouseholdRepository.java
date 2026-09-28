package _AD053.Wasteproject.repository;

import _AD053.Wasteproject.entity.Household;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HouseholdRepository extends JpaRepository<Household, Long> {
    List<Household> findByZoneId(Long zoneId);
}
