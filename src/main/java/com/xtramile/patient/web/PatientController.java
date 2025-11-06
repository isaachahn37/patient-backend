package com.xtramile.patient.web;

import com.xtramile.patient.dto.PatientRequest;
import com.xtramile.patient.dto.PatientResponse;
import com.xtramile.patient.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/patients")
@CrossOrigin
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    public Page<PatientResponse> list(
            @RequestParam(name = "search", required = false) String searchQuery,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "lastName,asc") String sort
    ) {
        Pageable pageable = PageRequest.of(page, size, parseSort(sort));
        return patientService.list(searchQuery, pageable);
    }

    @GetMapping("/{patientId}")
    public PatientResponse get(@PathVariable Long patientId) {
        return patientService.get(patientId);
    }

    @PostMapping
    public ResponseEntity<PatientResponse> create(@RequestBody @Valid PatientRequest patientRequest) {
        return ResponseEntity.ok(patientService.create(patientRequest));
    }

    @PutMapping("/{patientId}")
    public PatientResponse update(@PathVariable Long patientId,
                                  @RequestBody @Valid PatientRequest patientRequest) {
        return patientService.update(patientId, patientRequest);
    }

    @DeleteMapping("/{patientId}")
    public ResponseEntity<Void> delete(@PathVariable Long patientId) {
        patientService.delete(patientId);
        return ResponseEntity.noContent().build();
    }

    private Sort parseSort(String sort) {
        String[] parts = sort.split(",", 2);
        String field = parts[0];
        boolean desc = parts.length == 2 && "desc".equalsIgnoreCase(parts[1]);
        return desc ? Sort.by(field).descending() : Sort.by(field).ascending();
    }
}
