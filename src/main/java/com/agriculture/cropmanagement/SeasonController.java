package com.agriculture.cropmanagement;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/seasons")
public class SeasonController {
    private final JdbcTemplate jdbc;
    public SeasonController(JdbcTemplate jdbc) { this.jdbc = jdbc; }
    @GetMapping public List<Map<String,Object>> getAll() {
        return jdbc.queryForList("SELECT season_id, season_name, start_date, end_date FROM seasons ORDER BY season_id");
    }
    @GetMapping("/{id}")
public Map<String, Object> getById(@PathVariable int id) {
    List<Map<String, Object>> rows = jdbc.queryForList(
        "SELECT season_id, season_name, start_date, end_date " +
        "FROM seasons WHERE season_id = ?",
        id
    );

    if (rows.isEmpty()) {
        throw new ResponseStatusException(
            HttpStatus.NOT_FOUND, "Season not found"
        );
    }

    return rows.get(0);
}
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public Map<String,Object> add(@RequestBody SeasonRequest r) {
        validate(r);
        return jdbc.queryForMap("INSERT INTO seasons (season_name, start_date, end_date) VALUES (?, ?, ?) RETURNING season_id, season_name, start_date, end_date", r.seasonName().trim(), r.startDate(), r.endDate());
    }
    @PutMapping("/{id}")
    public Map<String,Object> update(@PathVariable int id, @RequestBody SeasonRequest r) {
        validate(r);
        List<Map<String,Object>> rows = jdbc.queryForList("UPDATE seasons SET season_name=?, start_date=?, end_date=? WHERE season_id=? RETURNING season_id, season_name, start_date, end_date", r.seasonName().trim(), r.startDate(), r.endDate(), id);
        if (rows.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Season not found");
        return rows.get(0);
    }
    @DeleteMapping("/{id}")
    public Map<String,String> delete(@PathVariable int id) {
        try {
            if (jdbc.update("DELETE FROM seasons WHERE season_id=?", id) == 0) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Season not found");
            return Map.of("message", "Season deleted successfully");
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This season is used in a yield record. Delete that yield record first.");
        }
    }
    private void validate(SeasonRequest r) {
        if (r == null || r.seasonName() == null || r.seasonName().isBlank() || r.startDate() == null || r.endDate() == null || r.endDate().isBefore(r.startDate()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Enter a season name and valid dates (end date must not be before start date)");
    }
    public record SeasonRequest(String seasonName, LocalDate startDate, LocalDate endDate) {}
}
