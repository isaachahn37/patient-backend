# Patient Backend

- MySQL (OCI): `168.110.216.58:13306`, DB `patientdb`, user `root`.
- Flyway:
  - `V1__schema.sql` → `addresses` and `patients` with FK (`address_id`, one-to-one).
  - `V2__seed_100_patients.sql` → seeds 100 mock patients + addresses (fresh installs).

## Build & Run (Docker)
```bash
docker compose down
docker compose build --no-cache
docker compose up -d
docker compose logs -f backend
# API: http://localhost:8080/api/v1/patients
```

## Sample endpoints
- `GET /api/v1/patients?q={pidOrName}&page=0&size=10&sort=lastName,asc`
- `GET /api/v1/patients/{id}`
- `POST /api/v1/patients`
- `PUT /api/v1/patients/{id}`
- `DELETE /api/v1/patients/{id}`
