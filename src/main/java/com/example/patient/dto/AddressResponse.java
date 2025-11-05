package com.example.patient.dto;

public record AddressResponse(
        Long id,
        String address,
        String suburb,
        String state,
        String postcode
) {
}
