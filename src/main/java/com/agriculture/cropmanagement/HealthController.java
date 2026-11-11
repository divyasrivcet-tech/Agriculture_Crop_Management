package com.agriculture.cropmanagement;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class HealthController {
 private final JdbcTemplate jdbc;
 public HealthController(JdbcTemplate jdbc) { this.jdbc = jdbc; }
 @GetMapping("/api/health")
 public Map<String,String> health() {
  String db = jdbc.queryForObject("SELECT current_database()", String.class);
  return Map.of("status","Connected","database",db,"message","Java is connected to PostgreSQL");
 }
}