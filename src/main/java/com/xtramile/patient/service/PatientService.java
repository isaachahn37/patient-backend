package com.xtramile.patient.service;

import com.xtramile.patient.dto.PatientRequest;
import com.xtramile.patient.dto.PatientResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PatientService {

    Page<PatientResponse> list(String searchQuery, Pageable pageable);

    PatientResponse create(PatientRequest patientRequest);

    PatientResponse update(Long patientId, PatientRequest patientRequest);

    void delete(Long patientId);

    PatientResponse get(Long patientId);
}
