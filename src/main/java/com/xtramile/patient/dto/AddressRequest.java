package com.xtramile.patient.dto;

import jakarta.validation.constraints.*;

public record AddressRequest(
        @NotBlank String address,
        @NotBlank String suburb,
        @NotBlank String state,
        @NotBlank String postcode
) {
}
