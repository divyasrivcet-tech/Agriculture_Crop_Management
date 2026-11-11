-- AGRICULTURE CROP MANAGEMENT SYSTEM - PostgreSQL schema
-- For a NEW/EMPTY agriculture_db database only. Existing tables are not changed.

CREATE TABLE IF NOT EXISTS farmers (
    farmer_id SERIAL PRIMARY KEY,
    farmer_name VARCHAR(100) NOT NULL,
    phone VARCHAR(15),
    village VARCHAR(100),
    district VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS fields (
    field_id SERIAL PRIMARY KEY,
    farmer_id INTEGER REFERENCES farmers(farmer_id),
    field_name VARCHAR(100) NOT NULL,
    area_acres NUMERIC(10,2) CHECK (area_acres IS NULL OR area_acres > 0)
);

CREATE TABLE IF NOT EXISTS crops (
    crop_id SERIAL PRIMARY KEY,
    crop_name VARCHAR(100) NOT NULL,
    crop_type VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS seasons (
    season_id SERIAL PRIMARY KEY,
    season_name VARCHAR(100) NOT NULL,
    start_date DATE,
    end_date DATE,
    CHECK (start_date IS NULL OR end_date IS NULL OR end_date >= start_date)
);

CREATE TABLE IF NOT EXISTS yield_records (
    yield_id SERIAL PRIMARY KEY,
    field_id INTEGER REFERENCES fields(field_id),
    crop_id INTEGER REFERENCES crops(crop_id),
    season_id INTEGER REFERENCES seasons(season_id),
    yield_kg NUMERIC(12,2) CHECK (yield_kg IS NULL OR yield_kg >= 0)
);
