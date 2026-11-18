# Distributed Task Manager

Full-stack job processing demo: **Java 17 / Spring Boot 3** backend and **React / TypeScript / Vite** monitoring UI. Submit jobs, execute asynchronously using a bounded worker pool, retry failed jobs, and inspect state through HTTP.

## Run

Backend (Java 17+, Maven): `cd backend && mvn spring-boot:run` (localhost:8080).
Frontend (Node 20+): `cd frontend && npm install && npm run dev` (localhost:5173).

Jobs are stored **in memory** and lost at restart; this is a single-server worker pool, not a production distributed message broker. State transitions and retries are implemented as a clear foundation for later Redis/RabbitMQ workers. Use only for permitted demo tasks.

## API

- `GET /api/jobs` — list jobs
- `POST /api/jobs` — create job: `{"name":"demo","durationMs":800,"shouldFail":false}`
- `POST /api/jobs/{id}/retry` — retry failed job

Includes JUnit controller/service tests and a GitHub Actions Maven build. MIT licensed.
