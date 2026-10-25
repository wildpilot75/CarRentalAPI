package com.crd.car_rental.api;

import com.crd.car_rental.dto.CarCreateRequest;
import com.crd.car_rental.dto.CarResponse;
import com.crd.car_rental.dto.CarUpdateRequest;
import com.crd.car_rental.error.GlobalExceptionHandler;
import com.crd.car_rental.error.NotFoundException;
import com.crd.car_rental.model.CarType;
import com.crd.car_rental.service.CarService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CarControllerTest {

    private MockMvc mockMvc;
    private CarService carService;

    @BeforeEach
    void setUp() {
        carService = mock(CarService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new CarController(carService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // ---------------------------------------------------------
    // GET /api/cars
    // ---------------------------------------------------------
    @Test
    void shouldReturnCars() throws Exception {
        CarResponse car = new CarResponse(1L, "Toyota", "Camry", CarType.SEDAN);

        when(carService.getAll(Optional.empty())).thenReturn(List.of(car));

        mockMvc.perform(get("/api/cars"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].make").value("Toyota"));
    }

    @Test
    void shouldReturnCarsFilteredByType() throws Exception {
        CarResponse car = new CarResponse(1L, "Ford", "Explorer", CarType.SUV);

        when(carService.getAll(Optional.of(CarType.SUV))).thenReturn(List.of(car));

        mockMvc.perform(get("/api/cars?carType=SUV"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].carType").value("SUV"));
    }

    // ---------------------------------------------------------
    // GET /api/cars/{id}
    // ---------------------------------------------------------
    @Test
    void shouldReturnCarById() throws Exception {
        CarResponse car = new CarResponse(1L, "Honda", "Civic", CarType.SEDAN);

        when(carService.getById(1L)).thenReturn(car);

        mockMvc.perform(get("/api/cars/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.make").value("Honda"));
    }

    @Test
    void shouldReturnNotFoundWhenGettingNonExistingCar() throws Exception {
        doThrow(new NotFoundException("Car with id 999 does not exist"))
                .when(carService).getById(999L);

        mockMvc.perform(get("/api/cars/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Car with id 999 does not exist"))
                .andExpect(jsonPath("$.path").value("/api/cars/999"));
    }

    // ---------------------------------------------------------
    // POST /api/cars
    // ---------------------------------------------------------
    @Test
    void shouldCreateCar() throws Exception {
        CarCreateRequest request = new CarCreateRequest("Toyota", "Camry", CarType.SEDAN);
        CarResponse created = new CarResponse(10L, "Toyota", "Camry", CarType.SEDAN);

        when(carService.addCar(any())).thenReturn(created);

        mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "make": "Toyota",
                      "model": "Camry",
                      "carType": "SEDAN"
                    }
                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/cars/10"))
                .andExpect(jsonPath("$.id").value(10L));

        ArgumentCaptor<CarCreateRequest> captor = ArgumentCaptor.forClass(CarCreateRequest.class);
        verify(carService).addCar(captor.capture());
        assertThat(captor.getValue().make()).isEqualTo("Toyota");
    }

    // ---------------------------------------------------------
    // PUT /api/cars/{id}
    // ---------------------------------------------------------
    @Test
    void shouldUpdateCar() throws Exception {
        CarUpdateRequest request = new CarUpdateRequest("Ford", "Focus", CarType.SEDAN);
        CarResponse updated = new CarResponse(1L, "Ford", "Focus", CarType.SEDAN);

        when(carService.updateCar(eq(1L), any())).thenReturn(updated);

        mockMvc.perform(put("/api/cars/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "make": "Ford",
                      "model": "Focus",
                      "carType": "SEDAN"
                    }
                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.make").value("Ford"))
                .andExpect(jsonPath("$.model").value("Focus"));
    }

    void shouldReturnNotFoundWhenUpdatingNonExistingCar() throws Exception {
        doThrow(new NotFoundException("Car with id 999 does not exist"))
                .when(carService).updateCar(eq(999L), any());

        mockMvc.perform(put("/api/cars/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                  "make": "Ford",
                  "model": "Focus",
                  "carType": "SEDAN"
                }
            """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Car with id 999 does not exist"))
                .andExpect(jsonPath("$.path").value("/api/cars/999"));
    }

    // ---------------------------------------------------------
    // DELETE /api/cars/{id}
    // ---------------------------------------------------------
    @Test
    void shouldDeleteCar() throws Exception {
        doNothing().when(carService).deleteCar(1L);

        mockMvc.perform(delete("/api/cars/1"))
                .andExpect(status().isNoContent());

        verify(carService).deleteCar(1L);
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistingCar() throws Exception {
        doThrow(new NotFoundException("Car with id 999 does not exist"))
                .when(carService).deleteCar(999L);

        mockMvc.perform(delete("/api/cars/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Car with id 999 does not exist"))
                .andExpect(jsonPath("$.path").value("/api/cars/999"));
    }

    // ---------------------------------------------------------
    // VALIDATION TESTS — POST
    // ---------------------------------------------------------

    @Test
    void shouldRejectCreateCarWhenMakeIsBlank() throws Exception {
        mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "make": "",
                      "model": "Camry",
                      "carType": "SEDAN"
                    }
                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectCreateCarWhenModelIsBlank() throws Exception {
        mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "make": "Toyota",
                      "model": "",
                      "carType": "SEDAN"
                    }
                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectCreateCarWhenCarTypeIsMissing() throws Exception {
        mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "make": "Toyota",
                      "model": "Camry"
                    }
                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectCreateCarWhenCarTypeIsInvalidEnum() throws Exception {
        mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "make": "Toyota",
                      "model": "Camry",
                      "carType": "INVALID_TYPE"
                    }
                """))
                .andExpect(status().isBadRequest());
    }

    // ---------------------------------------------------------
    // VALIDATION TESTS — PUT
    // ---------------------------------------------------------

    @Test
    void shouldRejectUpdateCarWhenMakeIsBlank() throws Exception {
        mockMvc.perform(put("/api/cars/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "make": "",
                      "model": "Focus",
                      "carType": "SEDAN"
                    }
                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectUpdateCarWhenModelIsBlank() throws Exception {
        mockMvc.perform(put("/api/cars/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "make": "Ford",
                      "model": "",
                      "carType": "SEDAN"
                    }
                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectUpdateCarWhenCarTypeIsMissing() throws Exception {
        mockMvc.perform(put("/api/cars/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "make": "Ford",
                      "model": "Focus"
                    }
                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectUpdateCarWhenCarTypeIsInvalidEnum() throws Exception {
        mockMvc.perform(put("/api/cars/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "make": "Ford",
                      "model": "Focus",
                      "carType": "INVALID_TYPE"
                    }
                """))
                .andExpect(status().isBadRequest());
    }
}
