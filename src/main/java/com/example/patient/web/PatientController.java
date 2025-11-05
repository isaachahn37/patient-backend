package com.example.patient.web;

import com.example.patient.dto.PatientRequest;
import com.example.patient.dto.PatientResponse;
import com.example.patient.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/patients")
@CrossOrigin
public class PatientController {
    private final PatientService service;

    public PatientController(PatientService service) {
        this.service = service;
    }

    @GetMapping
    public Page<PatientResponse> list(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "lastName,asc") String sort
    ) {
        String[] s = sort.split(",");
        Sort sortObj = (s.length == 2 && s[1].equalsIgnoreCase("desc")) ? Sort.by(s[0]).descending() : Sort.by(s[0]).ascending();
        Pageable pageable = PageRequest.of(page, size, sortObj);
        return service.list(q, pageable);
    }

    @GetMapping("/{id}")
    public PatientResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    public ResponseEntity<PatientResponse> create(@RequestBody @Valid PatientRequest request) {
        return ResponseEntity.ok(service.create(request));
    }

    @PutMapping("/{id}")
    public PatientResponse update(@PathVariable Long id, @RequestBody @Valid PatientRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
