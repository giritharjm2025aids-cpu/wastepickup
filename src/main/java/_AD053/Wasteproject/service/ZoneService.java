package _AD053.Wasteproject.service;

import _AD053.Wasteproject.entity.Zone;
import _AD053.Wasteproject.repository.PickupLogRepository;
import _AD053.Wasteproject.repository.ZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ZoneService {

    @Autowired
    private ZoneRepository zoneRepository;

    @Autowired
    private PickupLogRepository pickupLogRepository;

    public List<Zone> getAllZones() {
        return zoneRepository.findAll();
    }

    public Zone getZoneById(Long id) {
        return zoneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zone not found with id: " + id));
    }

    public Zone createZone(Zone zone) {
        if (zone.getName() == null || zone.getName().trim().isEmpty()) {
            throw new RuntimeException("Zone name cannot be empty.");
        }
        return zoneRepository.save(zone);
    }

    public Zone updateZone(Long id, Zone zoneDetails) {
        Zone zone = getZoneById(id);
        if (zoneDetails.getName() == null || zoneDetails.getName().trim().isEmpty()) {
            throw new RuntimeException("Zone name cannot be empty.");
        }
        zone.setName(zoneDetails.getName().trim());
        return zoneRepository.save(zone);
    }

    public void deleteZone(Long id) {
        Zone zone = getZoneById(id);
        zoneRepository.delete(zone);
    }

    public Double getZoneAverageScore(Long zoneId) {
        // verify that zone exists
        getZoneById(zoneId);
        Double avg = pickupLogRepository.findAverageScoreByZoneId(zoneId);
        return avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0;
    }
}
