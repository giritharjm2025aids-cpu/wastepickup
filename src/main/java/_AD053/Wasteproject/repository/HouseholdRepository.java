package com.example._AD053.Wasteproject.repository;

import com.example._AD053.Wasteproject.entity.Household;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HouseholdRepository extends JpaRepository<Household, Long> {

    List<Household> findByZoneId(Long zoneId);
}