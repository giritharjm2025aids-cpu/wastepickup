package _AD053.Wasteproject.repository;

import _AD053.Wasteproject.entity.PickupLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PickupLogRepository extends JpaRepository<PickupLog, Long> {

    List<PickupLog> findByHouseholdId(Long householdId);

    List<PickupLog> findByScheduleId(Long scheduleId);

    @Query("SELECT AVG(p.segregationScore) FROM PickupLog p WHERE p.household.id = :householdId")
    Double findAverageScoreByHouseholdId(@Param("householdId") Long householdId);

    @Query("SELECT AVG(p.segregationScore) FROM PickupLog p WHERE p.household.zone.id = :zoneId")
    Double findAverageScoreByZoneId(@Param("zoneId") Long zoneId);
}
