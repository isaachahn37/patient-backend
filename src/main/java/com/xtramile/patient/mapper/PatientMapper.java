package com.xtramile.patient.mapper;

import com.xtramile.patient.domain.Address;
import com.xtramile.patient.domain.Patient;
import com.example.patient.dto.*;
import com.xtramile.patient.dto.AddressRequest;
import com.xtramile.patient.dto.AddressResponse;
import com.xtramile.patient.dto.PatientRequest;
import com.xtramile.patient.dto.PatientResponse;

public final class PatientMapper {
    private PatientMapper() {
    }

    public static Address toAddress(AddressRequest req) {
        return Address.builder()
                .address(req.address())
                .suburb(req.suburb())
                .state(req.state())
                .postcode(req.postcode())
                .build();
    }

    public static AddressResponse toAddressResponse(Address a) {
        return new AddressResponse(a.getId(), a.getAddress(), a.getSuburb(), a.getState(), a.getPostcode());
    }

    public static Patient toEntity(PatientRequest req) {
        return Patient.builder()
                .pid(req.pid())
                .firstName(req.firstName())
                .lastName(req.lastName())
                .dateOfBirth(req.dateOfBirth())
                .gender(req.gender())
                .phone(req.phone())
                .addressRef(toAddress(req.address()))
                .build();
    }

    public static void update(Patient p, PatientRequest req) {
        p.setFirstName(req.firstName());
        p.setLastName(req.lastName());
        p.setDateOfBirth(req.dateOfBirth());
        p.setGender(req.gender());
        p.setPhone(req.phone());
        var a = p.getAddressRef();
        a.setAddress(req.address().address());
        a.setSuburb(req.address().suburb());
        a.setState(req.address().state());
        a.setPostcode(req.address().postcode());
    }

    public static PatientResponse toResponse(Patient p) {
        return new PatientResponse(
                p.getId(), p.getPid(), p.getFirstName(), p.getLastName(),
                p.getDateOfBirth(), p.getGender(), p.getPhone(), toAddressResponse(p.getAddressRef())
        );
    }
}
