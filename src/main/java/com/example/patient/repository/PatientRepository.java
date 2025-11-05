package com.example.patient.repository;

import com.example.patient.domain.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    boolean existsByPid(String pid);

    @EntityGraph(attributePaths = {"addressRef"})
    @Query("select p from Patient p " +
            "where (" +
            ":q is null or lower(p.pid) like lower(concat('%', :q, '%')) " +
            "or lower(p.firstName) like lower(concat('%', :q, '%')) " +
            "or lower(p.lastName) like lower(concat('%', :q, '%'))" +
            ") ")
    Page<Patient> search(String q, Pageable pageable);
}
