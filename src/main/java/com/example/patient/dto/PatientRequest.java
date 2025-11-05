package com.example.patient.dto;

import com.example.patient.domain.Gender;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record PatientRequest(
        @NotBlank String pid,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotNull LocalDate dateOfBirth,
        @NotNull Gender gender,
        @NotBlank String phone,
        @NotNull AddressRequest address
) {
}
