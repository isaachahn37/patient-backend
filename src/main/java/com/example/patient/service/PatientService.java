package com.example.patient.service;

import com.example.patient.dto.PatientRequest;
import com.example.patient.dto.PatientResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PatientService {
    Page<PatientResponse> list(String q, Pageable pageable);

    PatientResponse create(PatientRequest request);

    PatientResponse update(Long id, PatientRequest request);

    void delete(Long id);

    PatientResponse get(Long id);
}
