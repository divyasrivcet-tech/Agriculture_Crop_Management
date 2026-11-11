-- Quick checks to run in pgAdmin Query Tool
SELECT current_database();
SELECT table_name FROM information_schema.tables WHERE table_schema='public' ORDER BY table_name;
