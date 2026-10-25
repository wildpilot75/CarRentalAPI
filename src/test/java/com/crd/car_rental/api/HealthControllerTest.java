package com.crd.car_rental.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class HealthControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new HealthController()).build();
    }

    @Test
    void shouldReturnHealthStatus() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.app").value("Car Rental"))
                .andExpect(jsonPath("$.time").exists());
    }

    @Test
    void shouldReturnValidTimestampFormat() throws Exception {
        var result = mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        // Extract the "time" field manually
        String time = json.replaceAll(".*\"time\":\"([^\"]+)\".*", "$1");

        // AssertJ validation: timestamp should match your formatter pattern
        assertThat(time)
                .matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2} [A-Za-z_]+");
    }
}

