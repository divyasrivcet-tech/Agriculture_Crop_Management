
package com.agriculture.cropmanagement;

import java.util.List;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/crops")
public class CropController {

    private final JdbcTemplate jdbc;

    public CropController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // View all crops
    @GetMapping
    public List<Map<String, Object>> getAllCrops() {
        String sql = """
            SELECT crop_id, crop_name, crop_type,
                   season, duration, description
            FROM crops
            ORDER BY crop_id
            """;

        return jdbc.queryForList(sql);
    }

    // View crop by ID
    @GetMapping("/{id}")
    public Map<String, Object> getCrop(@PathVariable int id) {
        List<Map<String, Object>> rows = jdbc.queryForList(
            "SELECT * FROM crops WHERE crop_id = ?", id
        );

        if (rows.isEmpty()) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Crop not found"
            );
        }

        return rows.get(0);
    }

    // Add a crop
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> addCrop(@RequestBody CropRequest crop) {
        validate(crop);

        String sql = """
            INSERT INTO crops
            (crop_name, crop_type, season, duration, description)
            VALUES (?, ?, ?, ?, ?)
            RETURNING crop_id, crop_name, crop_type,
                      season, duration, description
            """;

        return jdbc.queryForMap(
            sql,
            crop.cropName().trim(),
            crop.cropType().trim(),
            crop.season(),
            crop.duration(),
            crop.description()
        );
    }

    // Update a crop
    @PutMapping("/{id}")
    public Map<String, Object> updateCrop(
            @PathVariable int id,
            @RequestBody CropRequest crop) {

        validate(crop);

        String sql = """
            UPDATE crops
            SET crop_name = ?, crop_type = ?,
                season = ?, duration = ?, description = ?
            WHERE crop_id = ?
            RETURNING crop_id, crop_name, crop_type,
                      season, duration, description
            """;

        List<Map<String, Object>> rows = jdbc.queryForList(
            sql,
            crop.cropName().trim(),
            crop.cropType().trim(),
            crop.season(),
            crop.duration(),
            crop.description(),
            id
        );

        if (rows.isEmpty()) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Crop not found"
            );
        }

        return rows.get(0);
    }

    // Delete a crop
    @DeleteMapping("/{id}")
    public Map<String, String> deleteCrop(@PathVariable int id) {
        try {
            int deleted = jdbc.update(
                "DELETE FROM crops WHERE crop_id = ?", id
            );

            if (deleted == 0) {
                throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Crop not found"
                );
            }

            return Map.of("message", "Crop deleted successfully");

        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Crop is used in a yield record. Delete that record first."
            );
        }
    }

    // Validate crop details
    private void validate(CropRequest crop) {
        if (crop == null ||
            crop.cropName() == null || crop.cropName().isBlank() ||
            crop.cropType() == null || crop.cropType().isBlank()) {

            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Crop name and crop type are required"
            );
        }

        if (crop.duration() != null && crop.duration() <= 0) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Duration must be greater than zero"
            );
        }
    }

    public record CropRequest(
        String cropName,
        String cropType,
        String season,
        Integer duration,
        String description
    ) {}
}
