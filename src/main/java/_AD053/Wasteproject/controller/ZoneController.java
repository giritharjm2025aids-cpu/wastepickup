package _AD053.Wasteproject.controller;

import _AD053.Wasteproject.entity.Zone;
import _AD053.Wasteproject.service.ZoneService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/zones")
public class ZoneController {

    @Autowired
    private ZoneService zoneService;

    // GET /api/zones
    @GetMapping
    public ResponseEntity<List<Zone>> getAllZones() {
        List<Zone> zones = zoneService.getAllZones();
        return ResponseEntity.ok(zones);
    }

    // GET /api/zones/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Zone> getZoneById(@PathVariable Long id) {
        Zone zone = zoneService.getZoneById(id);
        return ResponseEntity.ok(zone);
    }

    // POST /api/zones
    @PostMapping
    public ResponseEntity<Zone> createZone(@Valid @RequestBody Zone zone) {
        Zone created = zoneService.createZone(zone);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // PUT /api/zones/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Zone> updateZone(@PathVariable Long id, @Valid @RequestBody Zone zone) {
        Zone updated = zoneService.updateZone(id, zone);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/zones/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteZone(@PathVariable Long id) {
        zoneService.deleteZone(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/zones/{id}/average-score
    @GetMapping("/{id}/average-score")
    public ResponseEntity<Map<String, Object>> getZoneAverageScore(@PathVariable Long id) {
        Double avg = zoneService.getZoneAverageScore(id);
        Map<String, Object> response = new HashMap<>();
        response.put("zoneId", id);
        response.put("averageScore", avg);
        return ResponseEntity.ok(response);
    }
}
