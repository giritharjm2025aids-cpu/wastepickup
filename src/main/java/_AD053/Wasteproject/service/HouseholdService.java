package _AD053.Wasteproject.service;

import _AD053.Wasteproject.entity.Household;
import _AD053.Wasteproject.entity.Zone;
import _AD053.Wasteproject.repository.HouseholdRepository;
import _AD053.Wasteproject.repository.PickupLogRepository;
import _AD053.Wasteproject.repository.ZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HouseholdService {

    @Autowired
    private HouseholdRepository householdRepository;

    @Autowired
    private ZoneRepository zoneRepository;

    @Autowired
    private PickupLogRepository pickupLogRepository;

    public List<Household> getAllHouseholds() {
        List<Household> households = householdRepository.findAll();
        for (Household h : households) {
            Double avg = pickupLogRepository.findAverageScoreByHouseholdId(h.getId());
            if (avg != null) {
                double rounded = Math.round(avg * 10.0) / 10.0;
                h.setAverageScore(rounded);
                h.setStatus(rounded < h.getMinimumScore() ? "FLAGGED" : "NORMAL");
            } else {
                h.setAverageScore(null);
                h.setStatus("NORMAL");
            }
        }
        return households;
    }

    public Household getHouseholdById(Long id) {
        Household household = householdRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Household not found with id: " + id));

        Double avg = pickupLogRepository.findAverageScoreByHouseholdId(household.getId());
        if (avg != null) {
            double rounded = Math.round(avg * 10.0) / 10.0;
            household.setAverageScore(rounded);
            household.setStatus(rounded < household.getMinimumScore() ? "FLAGGED" : "NORMAL");
        } else {
            household.setAverageScore(null);
            household.setStatus("NORMAL");
        }
        return household;
    }

    public Household createHousehold(Household household) {
        if (household.getName() == null || household.getName().trim().isEmpty()) {
            throw new RuntimeException("Household name cannot be empty.");
        }
        if (household.getAddress() == null || household.getAddress().trim().isEmpty()) {
            throw new RuntimeException("Address cannot be empty.");
        }
        if (household.getPhone() == null || household.getPhone().trim().isEmpty()) {
            throw new RuntimeException("Phone cannot be empty.");
        }

        Zone zone = zoneRepository.findById(household.getZone().getId())
                .orElseThrow(() -> new RuntimeException("Zone not found with id: " + household.getZone().getId()));
        household.setZone(zone);

        if (household.getMinimumScore() == null) {
            household.setMinimumScore(50.0);
        }

        return householdRepository.save(household);
    }

    public Household updateHousehold(Long id, Household details) {
        Household household = householdRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Household not found with id: " + id));

        if (details.getName() != null && !details.getName().trim().isEmpty()) {
            household.setName(details.getName().trim());
        }
        if (details.getAddress() != null && !details.getAddress().trim().isEmpty()) {
            household.setAddress(details.getAddress().trim());
        }
        if (details.getPhone() != null && !details.getPhone().trim().isEmpty()) {
            household.setPhone(details.getPhone().trim());
        }
        if (details.getMinimumScore() != null) {
            household.setMinimumScore(details.getMinimumScore());
        }
        if (details.getZone() != null && details.getZone().getId() != null) {
            Zone zone = zoneRepository.findById(details.getZone().getId())
                    .orElseThrow(() -> new RuntimeException("Zone not found with id: " + details.getZone().getId()));
            household.setZone(zone);
        }

        return householdRepository.save(household);
    }

    public void deleteHousehold(Long id) {
        Household household = householdRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Household not found with id: " + id));
        householdRepository.delete(household);
    }

    public Double getHouseholdAverageScore(Long householdId) {
        // verify household exists
        householdRepository.findById(householdId)
                .orElseThrow(() -> new RuntimeException("Household not found with id: " + householdId));
        Double avg = pickupLogRepository.findAverageScoreByHouseholdId(householdId);
        return avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0;
    }

    public List<Household> getFlaggedHouseholds() {
        List<Household> all = getAllHouseholds();
        List<Household> flagged = new ArrayList<>();
        for (Household h : all) {
            if ("FLAGGED".equals(h.getStatus())) {
                flagged.add(h);
            }
        }
        return flagged;
    }
}
