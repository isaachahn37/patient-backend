package com.xtramile.patient.mapper;

import com.xtramile.patient.domain.Address;
import com.xtramile.patient.domain.Gender;
import com.xtramile.patient.domain.Patient;
import com.xtramile.patient.dto.AddressRequest;
import com.xtramile.patient.dto.AddressResponse;
import com.xtramile.patient.dto.PatientRequest;
import com.xtramile.patient.dto.PatientResponse;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class PatientMapperTest {

    private AddressRequest addrReq() {
        return new AddressRequest("12 Example St", "Sydney", "NSW", "2000");
    }

    private PatientRequest patReq(String pid) {
        return new PatientRequest(
                pid,
                "Isaac",
                "Hahn",
                LocalDate.of(1990, 1, 2),
                Gender.MALE,
                "0400123456",
                addrReq()
        );
    }

    private Patient entity(Long id, String pid) {
        return Patient.builder()
                .id(id)
                .pid(pid)
                .firstName("Isaac")
                .lastName("Hahn")
                .dateOfBirth(LocalDate.of(1990, 1, 2))
                .gender(Gender.MALE)
                .phone("0400123456")
                .addressRef(Address.builder()
                        .id(10L)
                        .address("12 Example St")
                        .suburb("Sydney")
                        .state("NSW")
                        .postcode("2000")
                        .build())
                .build();
    }

    @Test
    void toAddress_maps_all_fields() {
        AddressRequest req = addrReq();

        Address a = PatientMapper.toAddress(req);

        assertThat(a.getAddress()).isEqualTo("12 Example St");
        assertThat(a.getSuburb()).isEqualTo("Sydney");
        assertThat(a.getState()).isEqualTo("NSW");
        assertThat(a.getPostcode()).isEqualTo("2000");
    }

    @Test
    void toAddressResponse_maps_all_fields() {
        Address a = Address.builder()
                .id(7L)
                .address("1 George St")
                .suburb("Sydney")
                .state("NSW")
                .postcode("2000")
                .build();

        AddressResponse out = PatientMapper.toAddressResponse(a);

        assertThat(out.id()).isEqualTo(7L);
        assertThat(out.address()).isEqualTo("1 George St");
        assertThat(out.suburb()).isEqualTo("Sydney");
        assertThat(out.state()).isEqualTo("NSW");
        assertThat(out.postcode()).isEqualTo("2000");
    }

    @Test
    void toEntity_maps_nested_address_and_fields() {
        PatientRequest req = patReq("PID1234");

        Patient p = PatientMapper.toEntity(req);

        assertThat(p.getPid()).isEqualTo("PID1234");
        assertThat(p.getFirstName()).isEqualTo("Isaac");
        assertThat(p.getLastName()).isEqualTo("Hahn");
        assertThat(p.getDateOfBirth()).isEqualTo(LocalDate.of(1990, 1, 2));
        assertThat(p.getGender()).isEqualTo(Gender.MALE);
        assertThat(p.getPhone()).isEqualTo("0400123456");

        assertThat(p.getAddressRef()).isNotNull();
        assertThat(p.getAddressRef().getAddress()).isEqualTo("12 Example St");
        assertThat(p.getAddressRef().getSuburb()).isEqualTo("Sydney");
        assertThat(p.getAddressRef().getState()).isEqualTo("NSW");
        assertThat(p.getAddressRef().getPostcode()).isEqualTo("2000");
    }

    @Test
    void update_mutates_existing_entity_without_changing_pid_or_id() {
        Patient existing = entity(99L, "PID0099");
        PatientRequest req = new PatientRequest(
                "PID0099", // same PID (service layer already enforces this)
                "Isaac-Updated",
                "Hahn-Updated",
                LocalDate.of(1991, 2, 3),
                Gender.OTHER,
                "0400111222",
                new AddressRequest("34 Updated Rd", "Melbourne", "VIC", "3000")
        );

        PatientMapper.update(existing, req);

        // unchanged identity
        assertThat(existing.getId()).isEqualTo(99L);
        assertThat(existing.getPid()).isEqualTo("PID0099");

        // updated scalars
        assertThat(existing.getFirstName()).isEqualTo("Isaac-Updated");
        assertThat(existing.getLastName()).isEqualTo("Hahn-Updated");
        assertThat(existing.getDateOfBirth()).isEqualTo(LocalDate.of(1991, 2, 3));
        assertThat(existing.getGender()).isEqualTo(Gender.OTHER);
        assertThat(existing.getPhone()).isEqualTo("0400111222");

        // updated nested address (same object instance)
        Address addr = existing.getAddressRef();
        assertThat(addr).isNotNull();
        assertThat(addr.getAddress()).isEqualTo("34 Updated Rd");
        assertThat(addr.getSuburb()).isEqualTo("Melbourne");
        assertThat(addr.getState()).isEqualTo("VIC");
        assertThat(addr.getPostcode()).isEqualTo("3000");
    }

    @Test
    void toResponse_maps_entity_to_dto() {
        Patient p = entity(11L, "PID0011");

        PatientResponse out = PatientMapper.toResponse(p);

        assertThat(out.id()).isEqualTo(11L);
        assertThat(out.pid()).isEqualTo("PID0011");
        assertThat(out.firstName()).isEqualTo("Isaac");
        assertThat(out.lastName()).isEqualTo("Hahn");
        assertThat(out.dateOfBirth()).isEqualTo(LocalDate.of(1990, 1, 2));
        assertThat(out.gender()).isEqualTo(Gender.MALE);
        assertThat(out.phone()).isEqualTo("0400123456");

        assertThat(out.address()).isNotNull();
        assertThat(out.address().id()).isEqualTo(10L);
        assertThat(out.address().address()).isEqualTo("12 Example St");
        assertThat(out.address().suburb()).isEqualTo("Sydney");
        assertThat(out.address().state()).isEqualTo("NSW");
        assertThat(out.address().postcode()).isEqualTo("2000");
    }
}
