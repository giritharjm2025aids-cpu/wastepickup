package _AD053.Wasteproject.controller;

import _AD053.Wasteproject.entity.PickupLog;
import _AD053.Wasteproject.service.PickupLogService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pickups")
public class PickupLogController {

    @Autowired
    private PickupLogService pickupLogService;

    // GET /api/pickups
    @GetMapping
    public ResponseEntity<List<PickupLog>> getAllPickupLogs() {
        List<PickupLog> logs = pickupLogService.getAllPickupLogs();
        return ResponseEntity.ok(logs);
    }

    // GET /api/pickups/{id}
    @GetMapping("/{id}")
    public ResponseEntity<PickupLog> getPickupLogById(@PathVariable Long id) {
        PickupLog log = pickupLogService.getPickupLogById(id);
        return ResponseEntity.ok(log);
    }

    // POST /api/pickups
    @PostMapping
    public ResponseEntity<PickupLog> createPickupLog(@Valid @RequestBody PickupLog pickupLog) {
        PickupLog created = pickupLogService.createPickupLog(pickupLog);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // PUT /api/pickups/{id}
    @PutMapping("/{id}")
    public ResponseEntity<PickupLog> updatePickupLog(@PathVariable Long id, @Valid @RequestBody PickupLog pickupLog) {
        PickupLog updated = pickupLogService.updatePickupLog(id, pickupLog);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/pickups/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePickupLog(@PathVariable Long id) {
        pickupLogService.deletePickupLog(id);
        return ResponseEntity.noContent().build();
    }
}
