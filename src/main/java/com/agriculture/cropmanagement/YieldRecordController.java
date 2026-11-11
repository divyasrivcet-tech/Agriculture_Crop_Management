
package com.agriculture.cropmanagement;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/yield-records")
public class YieldRecordController {

    private final JdbcTemplate jdbc;

    public YieldRecordController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // View all yield records
    @GetMapping
    public List<Map<String, Object>> getAll() {
        String sql = """
            SELECT y.record_id AS yield_id,
                   y.field_id,
                   f.field_name,
                   y.crop_id,
                   c.crop_name,
                   y.season_id,
                   s.season_name,
                   y.yield_kg
            FROM yield_records y
            JOIN fields f ON f.field_id = y.field_id
            JOIN crops c ON c.crop_id = y.crop_id
            JOIN seasons s ON s.season_id = y.season_id
            ORDER BY y.record_id
            """;

        return jdbc.queryForList(sql);
    }

    // Add a yield record
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> add(@RequestBody YieldRequest r) {
        validate(r);

        String sql = """
            INSERT INTO yield_records
            (field_id, crop_id, season_id, yield_kg)
            VALUES (?, ?, ?, ?)
            RETURNING record_id AS yield_id,
                      field_id, crop_id, season_id, yield_kg
            """;

        return jdbc.queryForMap(
            sql, r.fieldId(), r.cropId(), r.seasonId(), r.yieldKg()
        );
    }

    // Update a yield record
    @PutMapping("/{id}")
    public Map<String, Object> update(
            @PathVariable int id,
            @RequestBody YieldRequest r) {

        validate(r);

        String sql = """
            UPDATE yield_records
            SET field_id = ?,
                crop_id = ?,
                season_id = ?,
                yield_kg = ?
            WHERE record_id = ?
            RETURNING record_id AS yield_id,
                      field_id, crop_id, season_id, yield_kg
            """;

        List<Map<String, Object>> rows = jdbc.queryForList(
            sql, r.fieldId(), r.cropId(), r.seasonId(), r.yieldKg(), id
        );

        if (rows.isEmpty()) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Yield record not found"
            );
        }

        return rows.get(0);
    }

    // Delete a yield record
    @DeleteMapping("/{id}")
    public Map<String, String> delete(@PathVariable int id) {

        if (jdbc.update(
                "DELETE FROM yield_records WHERE record_id = ?", id
            ) == 0) {

            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Yield record not found"
            );
        }

        return Map.of("message", "Yield record deleted successfully");
    }

    // Validate input
    private void validate(YieldRequest r) {
        if (r == null ||
            r.fieldId() == null ||
            r.cropId() == null ||
            r.seasonId() == null ||
            r.yieldKg() == null ||
            r.yieldKg().compareTo(BigDecimal.ZERO) < 0) {

            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Select field, crop, season and enter a valid yield value"
            );
        }
    }

    public record YieldRequest(
        Integer fieldId,
        Integer cropId,
        Integer seasonId,
        BigDecimal yieldKg
    ) {}
}
