-- Seed 100 addresses
INSERT INTO addresses (address, suburb, state, postcode)
SELECT
    CONCAT(FLOOR(10 + RAND(n+2) * 200), ' Example St') AS address,
    CASE (n % 6)
        WHEN 0 THEN 'Sydney'   WHEN 1 THEN 'Melbourne' WHEN 2 THEN 'Brisbane'
        WHEN 3 THEN 'Perth'    WHEN 4 THEN 'Adelaide'  ELSE 'Canberra'
        END AS suburb,
    CASE (n % 8)
        WHEN 0 THEN 'NSW' WHEN 1 THEN 'VIC' WHEN 2 THEN 'QLD' WHEN 3 THEN 'WA'
        WHEN 4 THEN 'SA'  WHEN 5 THEN 'TAS' WHEN 6 THEN 'ACT' ELSE 'NT'
        END AS state,
    LPAD(2000 + (n % 800), 4, '0') AS postcode
FROM (
         SELECT o.d + t.d*10 + 1 AS n
         FROM (SELECT 0 d UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4
               UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) o
                  CROSS JOIN
              (SELECT 0 d UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4
               UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t
     ) AS seq;

-- Seed 100 patients with address_id = NULL first
INSERT INTO patients (pid, first_name, last_name, date_of_birth, gender, phone, address_id)
SELECT
    CONCAT('PID', LPAD(n, 4, '0')) AS pid,

    -- Deterministic first name: cycles through 10 names
    ELT(((n - 1) % 10) + 1,
      'Oliver','Noah','Leo','Jack','Henry','William','Charlie','Lucas','Thomas','James') AS first_name,

    -- Deterministic last name: changes every 10 rows
    ELT(FLOOR((n - 1) / 10) + 1,
        'Smith','Jones','Williams','Brown','Taylor','Wilson','Anderson','Thompson','White','Martin') AS last_name,

    DATE_ADD('1970-01-01', INTERVAL FLOOR(RAND(n) * 18000) DAY) AS date_of_birth,
    CASE WHEN n % 3 = 0 THEN 'FEMALE' WHEN n % 3 = 1 THEN 'MALE' ELSE 'OTHER' END AS gender,
    CONCAT('04', LPAD(FLOOR(RAND(n+1) * 100000000), 8, '0')) AS phone,
    NULL AS address_id
FROM (
         SELECT o.d + t.d*10 + 1 AS n
         FROM (SELECT 0 d UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4
               UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) o
                  CROSS JOIN
              (SELECT 0 d UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4
               UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t
     ) AS seq2;

-- Pair nth patient (PID0001..PID0100) to nth address by ID order
UPDATE patients p
    JOIN (
    SELECT id, (@rn := @rn + 1) AS rn
    FROM addresses, (SELECT @rn := 0) vars
    ORDER BY id
    ) a ON a.rn = CAST(SUBSTRING(p.pid, 4) AS UNSIGNED)
    SET p.address_id = a.id;

-- Enforce one-to-one AFTER data is consistent
ALTER TABLE patients
    MODIFY address_id BIGINT NOT NULL,
    ADD CONSTRAINT fk_patient_address FOREIGN KEY (address_id) REFERENCES addresses(id),
    ADD UNIQUE KEY uq_patient_address_id (address_id);
