package com.xtramile.patient.dto;

public record AddressResponse(
        Long id,
        String address,
        String suburb,
        String state,
        String postcode
) {
}
