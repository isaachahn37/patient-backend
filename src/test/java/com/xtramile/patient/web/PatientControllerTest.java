package com.xtramile.patient.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xtramile.patient.domain.Gender;
import com.xtramile.patient.dto.AddressRequest;
import com.xtramile.patient.dto.AddressResponse;
import com.xtramile.patient.dto.PatientRequest;
import com.xtramile.patient.dto.PatientResponse;
import com.xtramile.patient.service.PatientService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PatientController.class)
class PatientControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @MockBean
    private PatientService service;

    private PatientResponse resp(long id, String pid) {
        return new PatientResponse(
                id,
                pid,
                "Isaac",
                "Hahn",
                LocalDate.of(1990, 1, 2),
                Gender.MALE,
                "0400123456",
                new AddressResponse(10L, "12 Example St", "Sydney", "NSW", "2000")
        );
    }

    private PatientRequest req(String pid) {
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

    @Test
    @DisplayName("GET /api/v1/patients returns a paginated list")
    void list_ok() throws Exception {
        var page = new PageImpl<>(
                List.of(resp(1L, "PID0001"), resp(2L, "PID0002")),
                PageRequest.of(0, 10),
                2
        );
        when(service.list(eq("isa"), any())).thenReturn(page);

        mvc.perform(get("/api/v1/patients")
                        .param("search", "isa")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "lastName,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].pid", is("PID0001")))
                .andExpect(jsonPath("$.totalElements", is(2)));

        verify(service).list(eq("isa"), any());
    }

    @Test
    @DisplayName("GET /api/v1/patients/{id} returns a patient")
    void get_ok() throws Exception {
        when(service.get(5L)).thenReturn(resp(5L, "PID0005"));

        mvc.perform(get("/api/v1/patients/{id}", 5))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pid", is("PID0005")));

        verify(service).get(5L);
    }

    @Test
    @DisplayName("POST /api/v1/patients creates a patient")
    void create_ok() throws Exception {
        var request = req("PID1234");
        when(service.create(any())).thenReturn(resp(99L, "PID1234"));

        mvc.perform(post("/api/v1/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(99)))
                .andExpect(jsonPath("$.pid", is("PID1234")));

        verify(service).create(any(PatientRequest.class));
    }

    @Test
    @DisplayName("PUT /api/v1/patients/{id} updates a patient")
    void update_ok() throws Exception {
        var request = req("PID7777");
        when(service.update(eq(77L), any())).thenReturn(resp(77L, "PID7777"));

        mvc.perform(put("/api/v1/patients/{id}", 77)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(77)))
                .andExpect(jsonPath("$.pid", is("PID7777")));

        verify(service).update(eq(77L), any(PatientRequest.class));
    }

    @Test
    @DisplayName("DELETE /api/v1/patients/{id} deletes a patient")
    void delete_ok() throws Exception {
        Mockito.doNothing().when(service).delete(8L);

        mvc.perform(delete("/api/v1/patients/{id}", 8))
                .andExpect(status().isNoContent());

        verify(service).delete(8L);
    }
}
