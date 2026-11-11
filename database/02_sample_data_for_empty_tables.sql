-- OPTIONAL sample data for a NEW / EMPTY set of tables only.
-- Do not run this repeatedly. Your farmers Arun and Priya may already exist.
-- Review the records first; use the web app to add records for normal project work.

INSERT INTO farmers (farmer_name, phone, village, district)
SELECT 'Arun', '9876543210', 'Perundurai', 'Erode'
WHERE NOT EXISTS (SELECT 1 FROM farmers WHERE farmer_name='Arun' AND village='Perundurai');

INSERT INTO farmers (farmer_name, phone, village, district)
SELECT 'Priya', '9876501234', 'Bhavani', 'Erode'
WHERE NOT EXISTS (SELECT 1 FROM farmers WHERE farmer_name='Priya' AND village='Bhavani');

INSERT INTO fields (farmer_id, field_name, area_acres)
SELECT farmer_id, 'Field A', 2.50 FROM farmers
WHERE farmer_name='Arun' AND village='Perundurai'
AND NOT EXISTS (SELECT 1 FROM fields WHERE field_name='Field A');

INSERT INTO fields (farmer_id, field_name, area_acres)
SELECT farmer_id, 'Field B', 3.00 FROM farmers
WHERE farmer_name='Priya' AND village='Bhavani'
AND NOT EXISTS (SELECT 1 FROM fields WHERE field_name='Field B');

INSERT INTO crops (crop_name, crop_type)
SELECT 'Rice', 'Food Crop' WHERE NOT EXISTS (SELECT 1 FROM crops WHERE crop_name='Rice');
INSERT INTO crops (crop_name, crop_type)
SELECT 'Sugarcane', 'Cash Crop' WHERE NOT EXISTS (SELECT 1 FROM crops WHERE crop_name='Sugarcane');
INSERT INTO crops (crop_name, crop_type)
SELECT 'Cotton', 'Cash Crop' WHERE NOT EXISTS (SELECT 1 FROM crops WHERE crop_name='Cotton');

INSERT INTO seasons (season_name, start_date, end_date)
SELECT 'Kharif', DATE '2026-06-01', DATE '2026-10-31'
WHERE NOT EXISTS (SELECT 1 FROM seasons WHERE season_name='Kharif');
INSERT INTO seasons (season_name, start_date, end_date)
SELECT 'Rabi', DATE '2026-11-01', DATE '2027-03-31'
WHERE NOT EXISTS (SELECT 1 FROM seasons WHERE season_name='Rabi');

INSERT INTO yield_records (field_id, crop_id, season_id, yield_kg)
SELECT f.field_id, c.crop_id, s.season_id, 2500
FROM fields f CROSS JOIN crops c CROSS JOIN seasons s
WHERE f.field_name='Field A' AND c.crop_name='Rice' AND s.season_name='Kharif'
AND NOT EXISTS (SELECT 1 FROM yield_records y WHERE y.field_id=f.field_id AND y.crop_id=c.crop_id AND y.season_id=s.season_id);

INSERT INTO yield_records (field_id, crop_id, season_id, yield_kg)
SELECT f.field_id, c.crop_id, s.season_id, 4000
FROM fields f CROSS JOIN crops c CROSS JOIN seasons s
WHERE f.field_name='Field B' AND c.crop_name='Sugarcane' AND s.season_name='Rabi'
AND NOT EXISTS (SELECT 1 FROM yield_records y WHERE y.field_id=f.field_id AND y.crop_id=c.crop_id AND y.season_id=s.season_id);
