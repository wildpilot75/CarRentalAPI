package com.crd.car_rental.validation;

import com.crd.car_rental.dto.ReservationCreateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintValidatorContext;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class ReservationDatesValidatorTest {

    private ReservationDatesValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new ReservationDatesValidator();
        context = null; // not needed for these tests
    }

    @Test
    void shouldBeValidWhenDatesAreNull() {
        ReservationCreateRequest request = new ReservationCreateRequest(
                1L,
                null,
                null
        );

        boolean result = validator.isValid(request, context);

        assertThat(result).isTrue();
    }

    @Test
    void shouldBeValidWhenEndDateIsAfterStartDate() {
        Date start = new Date(System.currentTimeMillis());
        Date end = new Date(System.currentTimeMillis() + 3600_000); // +1 hour

        ReservationCreateRequest request = new ReservationCreateRequest(
                1L,
                start,
                end
        );

        boolean result = validator.isValid(request, context);

        assertThat(result).isTrue();
    }

    @Test
    void shouldBeInvalidWhenEndDateEqualsStartDate() {
        Date now = new Date();

        ReservationCreateRequest request = new ReservationCreateRequest(
                1L,
                now,
                now
        );

        boolean result = validator.isValid(request, context);

        assertThat(result).isFalse();
    }

    @Test
    void shouldBeInvalidWhenEndDateIsBeforeStartDate() {
        Date start = new Date(System.currentTimeMillis());
        Date end = new Date(System.currentTimeMillis() - 3600_000); // -1 hour

        ReservationCreateRequest request = new ReservationCreateRequest(
                1L,
                start,
                end
        );

        boolean result = validator.isValid(request, context);

        assertThat(result).isFalse();
    }
}
