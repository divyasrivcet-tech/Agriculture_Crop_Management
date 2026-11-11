package com.agriculture.cropmanagement;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/fields")
public class FieldController {
    private final JdbcTemplate jdbc;
    public FieldController(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    @GetMapping
public List<Map<String,Object>> getAll() {
    return jdbc.queryForList(
        "SELECT f.field_id, f.farmer_id, fm.farmer_name, f.field_name, f.area_acres " +
        "FROM fields f LEFT JOIN farmers fm ON fm.farmer_id=f.farmer_id " +
        "ORDER BY f.field_id"
    );
}
@GetMapping("/{id}")
public Map<String, Object> getById(@PathVariable int id) {
    List<Map<String, Object>> rows = jdbc.queryForList(
        "SELECT f.field_id, f.farmer_id, fm.farmer_name, f.field_name, f.area_acres " +
        "FROM fields f LEFT JOIN farmers fm ON fm.farmer_id = f.farmer_id " +
        "WHERE f.field_id = ?",
        id
    );

    if (rows.isEmpty()) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Field not found");
    }

    return rows.get(0);
}
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public Map<String,Object> add(@RequestBody FieldRequest r) {
        validate(r);
        return jdbc.queryForMap("INSERT INTO fields (farmer_id, field_name, area_acres) VALUES (?, ?, ?) RETURNING field_id, farmer_id, field_name, area_acres",
            r.farmerId(), r.fieldName().trim(), r.areaAcres());
    }
    @PutMapping("/{id}")
    public Map<String,Object> update(@PathVariable int id, @RequestBody FieldRequest r) {
        validate(r);
        List<Map<String,Object>> rows = jdbc.queryForList("UPDATE fields SET farmer_id=?, field_name=?, area_acres=? WHERE field_id=? RETURNING field_id, farmer_id, field_name, area_acres",
            r.farmerId(), r.fieldName().trim(), r.areaAcres(), id);
        if (rows.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Field not found");
        return rows.get(0);
    }
    @DeleteMapping("/{id}")
    public Map<String,String> delete(@PathVariable int id) {
        try {
            if (jdbc.update("DELETE FROM fields WHERE field_id=?", id) == 0) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Field not found");
            return Map.of("message", "Field deleted successfully");
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This field is linked to a yield record. Delete that yield record first.");
        }
    }
    private void validate(FieldRequest r) {
        if (r == null || r.farmerId() == null || r.fieldName() == null || r.fieldName().isBlank() || r.areaAcres() == null || r.areaAcres().compareTo(BigDecimal.ZERO) <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Enter a farmer, field name and area greater than zero");
    }
    public record FieldRequest(Integer farmerId, String fieldName, BigDecimal areaAcres) {}
}
