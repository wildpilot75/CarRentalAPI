package com.crd.car_rental.api;

import com.crd.car_rental.dto.CarUnitResponse;
import com.crd.car_rental.error.GlobalExceptionHandler;
import com.crd.car_rental.error.NotFoundException;
import com.crd.car_rental.model.CarType;
import com.crd.car_rental.service.CarUnitService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Optional;

class CarUnitControllerTest {

    private MockMvc mockMvc;
    private CarUnitService carUnitService;

    @BeforeEach
    void setUp() {
        carUnitService = mock(CarUnitService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new CarUnitController(carUnitService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // ---------------------------------------------------------
    // GET /api/carunits
    // ---------------------------------------------------------
    @Test
    void shouldReturnCarUnits() throws Exception {
        CarUnitResponse unit = new CarUnitResponse(10L, 1L, "VIN123");

        when(carUnitService.getAll(Optional.empty(), Optional.empty()))
                .thenReturn(List.of(unit));

        mockMvc.perform(get("/api/carunits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10L))
                .andExpect(jsonPath("$[0].vin").value("VIN123"));
    }

    @Test
    void shouldReturnCarUnitsFilteredByCarId() throws Exception {
        CarUnitResponse unit = new CarUnitResponse(10L, 1L, "VIN123");

        when(carUnitService.getAll(Optional.of(1L), Optional.empty()))
                .thenReturn(List.of(unit));

        mockMvc.perform(get("/api/carunits?carId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].carId").value(1L));
    }

    @Test
    void shouldReturnCarUnitsFilteredByCarType() throws Exception {
        CarUnitResponse unit = new CarUnitResponse(10L, 1L, "VIN123");

        when(carUnitService.getAll(Optional.empty(), Optional.of(CarType.SUV)))
                .thenReturn(List.of(unit));

        mockMvc.perform(get("/api/carunits?carType=SUV"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].carId").value(1L));
    }

    // ---------------------------------------------------------
    // GET /api/carunits/{id}
    // ---------------------------------------------------------
    @Test
    void shouldReturnCarUnitById() throws Exception {
        CarUnitResponse unit = new CarUnitResponse(10L, 1L, "VIN123");

        when(carUnitService.getById(10L)).thenReturn(unit);

        mockMvc.perform(get("/api/carunits/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.vin").value("VIN123"));
    }

    @Test
    void shouldReturnNotFoundWhenGettingNonExistingCarUnit() throws Exception {
        doThrow(new NotFoundException("Car unit with id 999 does not exist"))
                .when(carUnitService).getById(999L);

        mockMvc.perform(get("/api/carunits/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Car unit with id 999 does not exist"))
                .andExpect(jsonPath("$.path").value("/api/carunits/999"));
    }

    // ---------------------------------------------------------
    // VALIDATION TESTS — POST
    // ---------------------------------------------------------
    @Test
    void shouldRejectCreateCarUnitWhenCarIdIsNull() throws Exception {
        mockMvc.perform(post("/api/carunits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "carId": null,
                      "vin": "VIN123"
                    }
                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectCreateCarUnitWhenVinIsBlank() throws Exception {
        mockMvc.perform(post("/api/carunits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "carId": 1,
                      "vin": ""
                    }
                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectCreateCarUnitWhenFieldsMissing() throws Exception {
        mockMvc.perform(post("/api/carunits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "vin": "VIN123"
                    }
                """))
                .andExpect(status().isBadRequest());
    }

    // ---------------------------------------------------------
    // POST /api/carunits
    // ---------------------------------------------------------
    @Test
    void shouldCreateCarUnit() throws Exception {
        CarUnitResponse created = new CarUnitResponse(10L, 1L, "VIN123");

        when(carUnitService.addCarUnit(any())).thenReturn(created);

        mockMvc.perform(post("/api/carunits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "carId": 1,
                      "vin": "VIN123"
                    }
                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/carunits/10"))
                .andExpect(jsonPath("$.id").value(10L));
    }

    // ---------------------------------------------------------
    // VALIDATION TESTS — PUT
    // ---------------------------------------------------------
    @Test
    void shouldRejectUpdateCarUnitWhenCarIdIsNull() throws Exception {
        mockMvc.perform(put("/api/carunits/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "carId": null,
                      "vin": "VIN123"
                    }
                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectUpdateCarUnitWhenVinIsBlank() throws Exception {
        mockMvc.perform(put("/api/carunits/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "carId": 1,
                      "vin": ""
                    }
                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectUpdateCarUnitWhenFieldsMissing() throws Exception {
        mockMvc.perform(put("/api/carunits/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "vin": "VIN123"
                    }
                """))
                .andExpect(status().isBadRequest());
    }

    // ---------------------------------------------------------
    // PUT /api/carunits/{id}
    // ---------------------------------------------------------
    @Test
    void shouldUpdateCarUnit() throws Exception {
        CarUnitResponse updated = new CarUnitResponse(10L, 1L, "NEWVIN");

        when(carUnitService.updateCarUnit(eq(10L), any())).thenReturn(updated);

        mockMvc.perform(put("/api/carunits/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "carId": 1,
                      "vin": "NEWVIN"
                    }
                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vin").value("NEWVIN"));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistingCarUnit() throws Exception {
        doThrow(new NotFoundException("Car unit with id 999 does not exist"))
                .when(carUnitService).updateCarUnit(eq(999L), any());

        mockMvc.perform(put("/api/carunits/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                  "carId": 1,
                  "vin": "NEWVIN"
                }
            """))
                .andExpect(status().isNotFound());
    }

    // ---------------------------------------------------------
    // DELETE /api/carunits/{id}
    // ---------------------------------------------------------
    @Test
    void shouldDeleteCarUnit() throws Exception {
        doNothing().when(carUnitService).deleteCarUnit(10L);

        mockMvc.perform(delete("/api/carunits/10"))
                .andExpect(status().isNoContent());

        verify(carUnitService).deleteCarUnit(10L);
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistingCarUnit() throws Exception {
        doThrow(new NotFoundException("Car unit with id 999 does not exist"))
                .when(carUnitService).deleteCarUnit(999L);

        mockMvc.perform(delete("/api/carunits/999"))
                .andExpect(status().isNotFound());
    }
}

