# Agriculture Crop Management and Yield Analysis System

This is the **single consolidated project folder**. All five main modules are included in this one project:

1. Farmer Management
2. Field Management
3. Crop Management
4. Season Management
5. Yield Record Management

Each module has Add, View, Update and Delete operations in the web interface. Yield records connect a field, crop and season. The system also includes PostgreSQL SQL scripts and a starter Python yield-analysis script.

## Technologies
- Java 21, Spring Boot 3.5.6, Maven
- PostgreSQL
- HTML, CSS and JavaScript
- Python, Pandas and Matplotlib

## Important: before running
1. Open `src/main/resources/application.properties`.
2. Replace `CHANGE_ME` with your own PostgreSQL password **on your laptop only**. Never send the password in chat or upload it to GitHub.
3. Confirm PostgreSQL is running and the database is named `agriculture_db`.
4. The app expects these existing tables: `farmers`, `fields`, `crops`, `seasons`, `yield_records`.
5. If the five tables already exist, **do not run the table-creation or sample-data scripts again unnecessarily**.

## Run the application
Open this project folder in VS Code. In the terminal, run:

```powershell
mvn spring-boot:run
```

Open `http://localhost:8080` in your browser. Keep the terminal open while using the website.

## PostgreSQL files
- `database/01_create_tables.sql`: create the five tables in a new/empty database.
- `database/02_sample_data_for_empty_tables.sql`: optional sample rows; use only after checking that the corresponding records are not already present.
- `database/03_useful_queries.sql`: table checks, SELECTs, INSERT examples, joins and yield summaries.

Run SQL in pgAdmin Query Tool after selecting `agriculture_db`.

## Backend API endpoints
- `/api/health`
- `/api/farmers`
- `/api/fields`
- `/api/crops`
- `/api/seasons`
- `/api/yield-records`

Each resource supports GET, POST, PUT by ID and DELETE by ID.

## Python yield analysis
Install packages from the `python` folder:

```powershell
python -m pip install -r requirements.txt
```

Export the full yield report query (query 4 in `database/03_useful_queries.sql`) as `python/yield_report.csv`, then run:

```powershell
python python/yield_analysis.py
```

## Security and storage notes
- The PostgreSQL password is a local setting in `application.properties`; keep this file private and do not commit a real password.
- `.gitignore` excludes build output and local secrets.
- This ZIP contains one project folder. You do not need separate module downloads.

## Current scope
The five core data modules and their CRUD screens are implemented in this starter project. Additional optional features such as weather integration, irrigation recommendations, fertilizer planning, user login and advanced prediction models are not included yet.
