package com.example.patient.dto;

import com.example.patient.domain.Gender;

import java.time.LocalDate;

public record PatientResponse(
        Long id,
        String pid,
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        Gender gender,
        String phone,
        AddressResponse address
) {
}
