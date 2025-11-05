package com.example.patient.service.impl;

import com.example.patient.domain.Patient;
import com.example.patient.dto.PatientRequest;
import com.example.patient.dto.PatientResponse;
import com.example.patient.mapper.PatientMapper;
import com.example.patient.repository.PatientRepository;
import com.example.patient.service.PatientService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PatientServiceImpl implements PatientService {
    private final PatientRepository repo;

    public PatientServiceImpl(PatientRepository repo) {
        this.repo = repo;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PatientResponse> list(String q, Pageable pageable) {
        return repo.search(q, pageable).map(PatientMapper::toResponse);
    }

    @Override
    public PatientResponse create(PatientRequest request) {
        if (repo.existsByPid(request.pid())) throw new IllegalArgumentException("PID already exists");
        var saved = repo.save(PatientMapper.toEntity(request));
        return PatientMapper.toResponse(saved);
    }

    @Override
    public PatientResponse update(Long id, PatientRequest request) {
        var p = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Patient not found"));
        if (!p.getPid().equals(request.pid())) throw new IllegalArgumentException("PID cannot be changed");
        PatientMapper.update(p, request);
        return PatientMapper.toResponse(p);
    }

    @Override
    public void delete(Long id) {
        if (!repo.existsById(id)) throw new IllegalArgumentException("Patient not found");
        repo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public PatientResponse get(Long id) {
        return repo.findById(id).map(PatientMapper::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found"));
    }
}
