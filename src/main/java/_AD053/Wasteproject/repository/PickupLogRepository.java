package com.example._AD053.Wasteproject.repository;

import com.example._AD053.Wasteproject.entity.PickupLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PickupLogRepository extends JpaRepository<PickupLog, Long> {

    List<PickupLog> findByHouseholdId(Long householdId);

    @Query("""
           SELECT AVG(p.segregationScore)
           FROM PickupLog p
           WHERE p.household.id = :householdId
           """)
    Double averageScore(@Param("householdId") Long householdId);

    @Query("""
           SELECT AVG(p.segregationScore)
           FROM PickupLog p
           WHERE p.household.zone.id = :zoneId
           """)
    Double zoneAverageScore(@Param("zoneId") Long zoneId);
}