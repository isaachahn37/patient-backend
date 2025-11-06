package com.xtramile.patient.service.impl;

import com.xtramile.patient.domain.Patient;
import com.xtramile.patient.dto.AddressRequest;
import com.xtramile.patient.dto.AddressResponse;
import com.xtramile.patient.dto.PatientRequest;
import com.xtramile.patient.dto.PatientResponse;
import com.xtramile.patient.mapper.PatientMapper;
import com.xtramile.patient.repository.PatientRepository;
import com.xtramile.patient.domain.Gender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PatientServiceImpl")
class PatientServiceImplTest {

    @Mock
    private PatientRepository repo;

    private PatientServiceImpl service;

    @BeforeEach
    void initializeServiceUnderTest() {
        service = new PatientServiceImpl(repo);
    }

    // ---------- helpers ----------
    private PatientRequest buildPatientRequest(String pid) {
        return new PatientRequest(
                pid,
                "Isaac",
                "Hahn",
                LocalDate.of(1990, 1, 2),
                Gender.MALE,
                "0400123456",
                new AddressRequest("12 Example St", "Sydney", "NSW", "2000")
        );
    }

    private PatientResponse buildPatientResponse(Long id, String pid) {
        return new PatientResponse(
                id, pid, "Isaac", "Hahn",
                LocalDate.of(1990, 1, 2),
                Gender.MALE,
                "0400123456",
                new AddressResponse(10L, "12 Example St", "Sydney", "NSW", "2000")
        );
    }

    // ---------- tests ----------

    @Test
    @DisplayName("list(q, pageable) maps entities to responses")
    void list_mapsEntitiesToResponses() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("lastName").ascending());

        Patient e1 = mock(Patient.class);
        Patient e2 = mock(Patient.class);
        Page<Patient> page = new PageImpl<>(List.of(e1, e2), pageable, 2);

        when(repo.search(eq("isa"), eq(pageable))).thenReturn(page);

        try (MockedStatic<PatientMapper> mapper = mockStatic(PatientMapper.class)) {
            mapper.when(() -> PatientMapper.toResponse(e1)).thenReturn(buildPatientResponse(1L, "PID0001"));
            mapper.when(() -> PatientMapper.toResponse(e2)).thenReturn(buildPatientResponse(2L, "PID0002"));

            Page<PatientResponse> out = service.list("isa", pageable);

            assertThat(out.getTotalElements()).isEqualTo(2);
            assertThat(out.getContent()).extracting(PatientResponse::pid)
                    .containsExactly("PID0001", "PID0002");
        }

        verify(repo).search("isa", pageable);
    }

    @Test
    @DisplayName("create(request) succeeds when PID is unique")
    void create_succeeds_whenPidUnique() {
        PatientRequest request = buildPatientRequest("PID1234");
        Patient entityIn = mock(Patient.class);
        Patient entitySaved = mock(Patient.class);

        when(repo.existsByPid("PID1234")).thenReturn(false);
        when(repo.save(entityIn)).thenReturn(entitySaved);

        try (MockedStatic<PatientMapper> mapper = mockStatic(PatientMapper.class)) {
            mapper.when(() -> PatientMapper.toEntity(request)).thenReturn(entityIn);
            mapper.when(() -> PatientMapper.toResponse(entitySaved)).thenReturn(buildPatientResponse(99L, "PID1234"));

            PatientResponse out = service.create(request);

            assertThat(out.id()).isEqualTo(99L);
            assertThat(out.pid()).isEqualTo("PID1234");
        }

        verify(repo).existsByPid("PID1234");
        verify(repo).save(entityIn);
    }

    @Test
    @DisplayName("create(request) throws when PID already exists")
    void create_throws_whenPidExists() {
        PatientRequest request = buildPatientRequest("PID1234");
        when(repo.existsByPid("PID1234")).thenReturn(true);

        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> service.create(request));
        assertThat(ex).hasMessageContaining("PID already exists");

        verify(repo).existsByPid("PID1234");
        verify(repo, never()).save(any());
    }

    @Test
    @DisplayName("update(id, request) succeeds when PID unchanged")
    void update_succeeds_whenPidUnchanged() {
        Long id = 77L;
        PatientRequest request = buildPatientRequest("PID7777");
        Patient existing = mock(Patient.class);
        when(existing.getPid()).thenReturn("PID7777");
        when(repo.findById(id)).thenReturn(Optional.of(existing));

        try (MockedStatic<PatientMapper> mapper = mockStatic(PatientMapper.class)) {
            mapper.when(() -> PatientMapper.update(existing, request)).thenAnswer(inv -> null);
            mapper.when(() -> PatientMapper.toResponse(existing)).thenReturn(buildPatientResponse(id, "PID7777"));

            PatientResponse out = service.update(id, request);

            assertThat(out.id()).isEqualTo(id);
            assertThat(out.pid()).isEqualTo("PID7777");
        }

        verify(repo).findById(id);
    }

    @Test
    @DisplayName("update(id, request) throws when PID changed")
    void update_throws_whenPidChanged() {
        Long id = 77L;
        PatientRequest request = buildPatientRequest("PID-NEW");
        Patient existing = mock(Patient.class);
        when(existing.getPid()).thenReturn("PID-OLD");
        when(repo.findById(id)).thenReturn(Optional.of(existing));

        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> service.update(id, request));
        assertThat(ex).hasMessageContaining("PID cannot be changed");

        verify(repo).findById(id);
    }

    @Test
    @DisplayName("update(id, request) throws when patient not found")
    void update_throws_whenNotFound() {
        Long id = 404L;
        when(repo.findById(id)).thenReturn(Optional.empty());

        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> service.update(id, buildPatientRequest("PIDX")));
        assertThat(ex).hasMessageContaining("Patient not found");

        verify(repo).findById(id);
    }

    @Test
    @DisplayName("get(id) returns response when found")
    void get_returnsResponse_whenFound() {
        Long id = 10L;
        Patient entity = mock(Patient.class);
        when(repo.findById(id)).thenReturn(Optional.of(entity));

        try (MockedStatic<PatientMapper> mapper = mockStatic(PatientMapper.class)) {
            mapper.when(() -> PatientMapper.toResponse(entity)).thenReturn(buildPatientResponse(id, "PID0010"));
            PatientResponse out = service.get(id);
            assertThat(out.pid()).isEqualTo("PID0010");
        }

        verify(repo).findById(id);
    }

    @Test
    @DisplayName("get(id) throws when patient not found")
    void get_throws_whenNotFound() {
        when(repo.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> service.get(1L));
        assertThat(ex).hasMessageContaining("Patient not found");

        verify(repo).findById(1L);
    }

    @Test
    @DisplayName("delete(id) deletes patient when patient exists")
    void delete_deletesPatient_whenExists() {
        when(repo.existsById(5L)).thenReturn(true);

        service.delete(5L);

        verify(repo).existsById(5L);
        verify(repo).deleteById(5L);
    }

    @Test
    @DisplayName("delete(id) throws Exception when patient missing")
    void delete_throwsException_whenMissing() {
        when(repo.existsById(6L)).thenReturn(false);

        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> service.delete(6L));
        assertThat(ex).hasMessageContaining("Patient not found");

        verify(repo).existsById(6L);
        verify(repo, never()).deleteById(anyLong());
    }
}