package com.xtramile.patient.dto;

import com.xtramile.patient.domain.Gender;

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
