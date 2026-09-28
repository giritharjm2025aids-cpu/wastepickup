package _AD053.Wasteproject.controller;

import _AD053.Wasteproject.entity.Household;
import _AD053.Wasteproject.service.HouseholdService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/households")
public class HouseholdController {

    @Autowired
    private HouseholdService householdService;

    // GET /api/households
    @GetMapping
    public ResponseEntity<List<Household>> getAllHouseholds() {
        List<Household> households = householdService.getAllHouseholds();
        return ResponseEntity.ok(households);
    }

    // GET /api/households/flagged
    @GetMapping("/flagged")
    public ResponseEntity<List<Household>> getFlaggedHouseholds() {
        List<Household> flagged = householdService.getFlaggedHouseholds();
        return ResponseEntity.ok(flagged);
    }

    // GET /api/households/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Household> getHouseholdById(@PathVariable Long id) {
        Household household = householdService.getHouseholdById(id);
        return ResponseEntity.ok(household);
    }

    // POST /api/households
    @PostMapping
    public ResponseEntity<Household> createHousehold(@Valid @RequestBody Household household) {
        Household created = householdService.createHousehold(household);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // PUT /api/households/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Household> updateHousehold(@PathVariable Long id, @Valid @RequestBody Household household) {
        Household updated = householdService.updateHousehold(id, household);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/households/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHousehold(@PathVariable Long id) {
        householdService.deleteHousehold(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/households/{id}/average-score
    @GetMapping("/{id}/average-score")
    public ResponseEntity<Map<String, Object>> getHouseholdAverageScore(@PathVariable Long id) {
        Double avg = householdService.getHouseholdAverageScore(id);
        Map<String, Object> response = new HashMap<>();
        response.put("householdId", id);
        response.put("averageScore", avg);
        return ResponseEntity.ok(response);
    }
}
