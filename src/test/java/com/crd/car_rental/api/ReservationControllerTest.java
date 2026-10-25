package com.crd.car_rental.api;

import com.crd.car_rental.dto.*;
import com.crd.car_rental.error.GlobalExceptionHandler;
import com.crd.car_rental.error.NotFoundException;
import com.crd.car_rental.error.BadRequestException;
import com.crd.car_rental.error.ReservationOverlapException;
import com.crd.car_rental.model.CarType;
import com.crd.car_rental.service.ReservationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ReservationControllerTest {

    private MockMvc mockMvc;
    private ReservationService reservationService;

    // Generate dynamic future dates
    private final Date startDate = Date.from(Instant.now().plus(1, ChronoUnit.DAYS));
    private final Date endDate   = Date.from(Instant.now().plus(2, ChronoUnit.DAYS));

    private final String startJson = startDate.toInstant().toString();
    private final String endJson   = endDate.toInstant().toString();

    @BeforeEach
    void setUp() {
        reservationService = mock(ReservationService.class);

        mockMvc = MockMvcBuilders
                .standaloneSetup(new ReservationController(reservationService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // ---------------------------------------------------------
    // GET /api/reservations
    // ---------------------------------------------------------
    @Test
    void shouldReturnAllReservations() throws Exception {
        ReservationDetailsResponse res =
                new ReservationDetailsResponse(1L, 10L, "VIN123", 100L, "Toyota", "Camry",
                        CarType.SEDAN, startDate, endDate);

        when(reservationService.getAll()).thenReturn(List.of(res));

        mockMvc.perform(get("/api/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    // ---------------------------------------------------------
    // GET /api/reservations/{id}
    // ---------------------------------------------------------
    @Test
    void shouldReturnReservationById() throws Exception {
        ReservationDetailsResponse res =
                new ReservationDetailsResponse(1L, 10L, "VIN123", 100L, "Toyota", "Camry",
                        CarType.SEDAN, startDate, endDate);

        when(reservationService.getById(1L)).thenReturn(res);

        mockMvc.perform(get("/api/reservations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void shouldReturnNotFoundWhenGettingNonExistingReservation() throws Exception {
        doThrow(new NotFoundException("Reservation with id 999 does not exist"))
                .when(reservationService).getById(999L);

        mockMvc.perform(get("/api/reservations/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Reservation with id 999 does not exist"))
                .andExpect(jsonPath("$.path").value("/api/reservations/999"));
    }

    // ---------------------------------------------------------
    // POST /api/reservations
    // ---------------------------------------------------------
    @Test
    void shouldCreateReservation() throws Exception {
        ReservationResponse res = new ReservationResponse(1L, 10L, startDate, endDate);

        when(reservationService.addReservation(any())).thenReturn(res);

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "carUnitId": 10,
                      "startDate": "%s",
                      "endDate": "%s"
                    }
                """.formatted(startJson, endJson)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void shouldFailCreateReservationWhenCarUnitNotFound() throws Exception {
        doThrow(new NotFoundException("Car unit not found"))
                .when(reservationService).addReservation(any());

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "carUnitId": 10,
                      "startDate": "%s",
                      "endDate": "%s"
                    }
                """.formatted(startJson, endJson)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Car unit not found"));
    }

    @Test
    void shouldFailCreateReservationWhenOverlapping() throws Exception {
        doThrow(new ReservationOverlapException("Overlap"))
                .when(reservationService).addReservation(any());

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "carUnitId": 10,
                      "startDate": "%s",
                      "endDate": "%s"
                    }
                """.formatted(startJson, endJson)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    // ---------------------------------------------------------
    // POST /api/reservations/by-car
    // ---------------------------------------------------------
    @Test
    void shouldCreateReservationByCar() throws Exception {
        ReservationResponse res = new ReservationResponse(1L, 10L, startDate, endDate);

        when(reservationService.createReservationByCar(any())).thenReturn(res);

        mockMvc.perform(post("/api/reservations/by-car")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "carId": 5,
                      "startDate": "%s",
                      "endDate": "%s"
                    }
                """.formatted(startJson, endJson)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void shouldFailCreateReservationByCarWhenBadRequest() throws Exception {
        mockMvc.perform(post("/api/reservations/by-car")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "startDate": "%s",
                      "endDate": "%s"
                    }
                """.formatted(startJson, endJson)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation Failed"))
                .andExpect(jsonPath("$.details").isArray());
    }

    // ---------------------------------------------------------
    // PUT /api/reservations/{id}
    // ---------------------------------------------------------
    @Test
    void shouldUpdateReservation() throws Exception {
        ReservationResponse res = new ReservationResponse(1L, 10L, startDate, endDate);

        when(reservationService.updateReservation(eq(1L), any())).thenReturn(res);

        mockMvc.perform(put("/api/reservations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "carUnitId": 10,
                      "startDate": "%s",
                      "endDate": "%s"
                    }
                """.formatted(startJson, endJson)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void shouldFailUpdateReservationWhenNotFound() throws Exception {
        doThrow(new NotFoundException("Reservation with id 999 does not exist"))
                .when(reservationService).updateReservation(eq(999L), any());

        mockMvc.perform(put("/api/reservations/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "carUnitId": 10,
                      "startDate": "%s",
                      "endDate": "%s"
                    }
                """.formatted(startJson, endJson)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Reservation with id 999 does not exist"));
    }

    @Test
    void shouldFailUpdateReservationWhenOverlapping() throws Exception {
        doThrow(new ReservationOverlapException("Overlap"))
                .when(reservationService).updateReservation(eq(1L), any());

        mockMvc.perform(put("/api/reservations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "carUnitId": 10,
                      "startDate": "%s",
                      "endDate": "%s"
                    }
                """.formatted(startJson, endJson)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    // ---------------------------------------------------------
    // DELETE /api/reservations/{id}
    // ---------------------------------------------------------
    @Test
    void shouldDeleteReservation() throws Exception {
        doNothing().when(reservationService).deleteReservation(1L);

        mockMvc.perform(delete("/api/reservations/1"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldFailDeleteReservationWhenNotFound() throws Exception {
        doThrow(new NotFoundException("Reservation with id 999 does not exist"))
                .when(reservationService).deleteReservation(999L);

        mockMvc.perform(delete("/api/reservations/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Reservation with id 999 does not exist"));
    }

    // ---------------------------------------------------------
    // GET BY CAR UNIT / CAR ENTITY / CAR TYPE
    // ---------------------------------------------------------
    @Test
    void shouldReturnReservationsByCarUnit() throws Exception {
        ReservationDetailsResponse res =
                new ReservationDetailsResponse(1L, 10L, "VIN123", 100L, "Toyota", "Camry",
                        CarType.SEDAN, startDate, endDate);

        when(reservationService.getByCarUnit(10L)).thenReturn(List.of(res));

        mockMvc.perform(get("/api/reservations/car-unit/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].carUnitId").value(10L));
    }

    @Test
    void shouldReturnReservationsByCarEntity() throws Exception {
        ReservationDetailsResponse res =
                new ReservationDetailsResponse(1L, 10L, "VIN123", 100L, "Toyota", "Camry",
                        CarType.SEDAN, startDate, endDate);

        when(reservationService.getByCarEntity(100L)).thenReturn(List.of(res));

        mockMvc.perform(get("/api/reservations/car-entity/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].carId").value(100L));
    }

    @Test
    void shouldReturnReservationsByCarType() throws Exception {
        ReservationDetailsResponse res =
                new ReservationDetailsResponse(1L, 10L, "VIN123", 100L, "Toyota", "Camry",
                        CarType.SEDAN, startDate, endDate);

        when(reservationService.getByCarType(CarType.SEDAN)).thenReturn(List.of(res));

        mockMvc.perform(get("/api/reservations/car-type/SEDAN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].carType").value("SEDAN"));
    }

    // ---------------------------------------------------------
    // AVAILABILITY ENDPOINTS
    // ---------------------------------------------------------
    @Test
    void shouldCheckUnitAvailability() throws Exception {
        AvailabilityResponse res = new AvailabilityResponse(1, List.of(10L));

        when(reservationService.checkUnitAvailability(eq(10L), any(), any()))
                .thenReturn(res);

        mockMvc.perform(get("/api/reservations/car-unit/10/availability")
                        .param("startDate", startJson)
                        .param("endDate", endJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableUnits").value(1));
    }

    @Test
    void shouldCheckEntityAvailability() throws Exception {
        AvailabilityResponse res = new AvailabilityResponse(2, List.of(10L, 11L));

        when(reservationService.checkEntityAvailability(eq(100L), any(), any()))
                .thenReturn(res);

        mockMvc.perform(get("/api/reservations/car-entity/100/availability")
                        .param("startDate", startJson)
                        .param("endDate", endJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableUnits").value(2));
    }

    @Test
    void shouldCheckEntityAvailabilityByCarType() throws Exception {
        AvailabilityResponse res = new AvailabilityResponse(1, List.of(10L));

        when(reservationService.checkEntityAvailability(eq(CarType.SEDAN), any(), any()))
                .thenReturn(res);

        mockMvc.perform(get("/api/reservations/car-type/SEDAN/availability")
                        .param("startDate", startJson)
                        .param("endDate", endJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableUnits").value(1));
    }
}
