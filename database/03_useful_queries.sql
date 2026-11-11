-- Run these individually in pgAdmin Query Tool after connecting to agriculture_db.

-- 1. Check database and list project tables
SELECT current_database();
SELECT table_name FROM information_schema.tables WHERE table_schema='public' ORDER BY table_name;

-- 2. View all rows in each table
SELECT * FROM farmers ORDER BY farmer_id;
SELECT * FROM fields ORDER BY field_id;
SELECT * FROM crops ORDER BY crop_id;
SELECT * FROM seasons ORDER BY season_id;
SELECT * FROM yield_records ORDER BY yield_id;

-- 3. Farmer with their fields
SELECT fm.farmer_name, fm.village, f.field_name, f.area_acres
FROM farmers fm LEFT JOIN fields f ON f.farmer_id=fm.farmer_id
ORDER BY fm.farmer_name, f.field_name;

-- 4. Full yield report (all five tables joined)
SELECT y.record_id AS yield_id,
       fm.farmer_name,
       f.field_name,
       c.crop_name,
       s.season_name,
       y.yield_kg
FROM yield_records y
JOIN fields f ON f.field_id = y.field_id
JOIN farmers fm ON fm.farmer_id = f.farmer_id
JOIN crops c ON c.crop_id = f.crop_id
JOIN seasons s ON s.season_id = y.season_id
ORDER BY y.record_id;

-- 5. Average, highest and lowest yield
SELECT AVG(yield_kg) AS average_yield_kg,
       MAX(yield_kg) AS highest_yield_kg,
       MIN(yield_kg) AS lowest_yield_kg
FROM yield_records;

-- 6. Total yield by crop
SELECT c.crop_name, SUM(y.yield_kg) AS total_yield_kg
FROM crops c LEFT JOIN yield_records y ON y.crop_id=c.crop_id
GROUP BY c.crop_name ORDER BY total_yield_kg DESC NULLS LAST;

-- 7. Total cultivated area by farmer
SELECT fm.farmer_name, SUM(f.area_acres) AS total_area_acres
FROM farmers fm LEFT JOIN fields f ON f.farmer_id=fm.farmer_id
GROUP BY fm.farmer_name ORDER BY fm.farmer_name;

-- 8. Example INSERT statements (replace IDs with IDs that exist in your tables)
-- INSERT INTO farmers (farmer_name, phone, village, district) VALUES ('New Farmer','9000000000','Erode','Erode');
-- INSERT INTO fields (farmer_id, field_name, area_acres) VALUES (1,'North Field',1.50);
-- INSERT INTO crops (crop_name, crop_type) VALUES ('Millet','Food Crop');
-- INSERT INTO seasons (season_name, start_date, end_date) VALUES ('Summer','2027-03-01','2027-05-31');
-- INSERT INTO yield_records (field_id, crop_id, season_id, yield_kg) VALUES (1,1,1,1200);
