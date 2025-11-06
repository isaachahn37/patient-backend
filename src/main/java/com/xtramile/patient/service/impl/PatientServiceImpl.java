package com.xtramile.patient.service.impl;

import com.xtramile.patient.dto.PatientRequest;
import com.xtramile.patient.dto.PatientResponse;
import com.xtramile.patient.mapper.PatientMapper;
import com.xtramile.patient.repository.PatientRepository;
import com.xtramile.patient.service.PatientService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;

    public PatientServiceImpl(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PatientResponse> list(String searchQuery, Pageable pageable) {
        return patientRepository.search(searchQuery, pageable)
                .map(PatientMapper::toResponse);
    }

    @Override
    public PatientResponse create(PatientRequest patientRequest) {
        if (patientRepository.existsByPid(patientRequest.pid())) {
            throw new IllegalArgumentException("PID already exists");
        }
        var savedPatient = patientRepository.save(PatientMapper.toEntity(patientRequest));
        return PatientMapper.toResponse(savedPatient);
    }

    @Override
    public PatientResponse update(Long patientId, PatientRequest patientRequest) {
        var patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found"));

        if (!patient.getPid().equals(patientRequest.pid())) {
            throw new IllegalArgumentException("PID cannot be changed");
        }

        PatientMapper.update(patient, patientRequest);
        return PatientMapper.toResponse(patient);
    }

    @Override
    public void delete(Long patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new IllegalArgumentException("Patient not found");
        }
        patientRepository.deleteById(patientId);
    }

    @Override
    @Transactional(readOnly = true)
    public PatientResponse get(Long patientId) {
        return patientRepository.findById(patientId)
                .map(PatientMapper::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found"));
    }
}