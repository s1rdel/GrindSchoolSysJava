package com.example.grindschool.controller;

import com.example.grindschool.facade.BookingFacade;
import com.example.grindschool.facade.BookingFacade.BookingResult;
import com.example.grindschool.facade.BookingFacade.CancellationResult;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BookingControllerTest {

    private static final long SUBJECT_ID = 1L;
    private static final long SESSION_ID = 2L;
    private static final long STUDENT_ID = 3L;
    private static final String SESSIONS_REDIRECT = "redirect:/subjects/1/sessions";

    private final BookingFacade facade = mock(BookingFacade.class);
    private final BookingController controller = new BookingController(facade, STUDENT_ID);
    private final RedirectAttributesModelMap redirect = new RedirectAttributesModelMap();

    @ParameterizedTest
    @CsvSource({
            "BOOKED, Booking confirmed.",
            "SESSION_NOT_FOUND, Session not found.",
            "SESSION_CLOSED, This session is no longer open for booking.",
            "STUDENT_NOT_FOUND, Demo student not found. Check the configured ID.",
            "ALREADY_BOOKED, You already booked this session.",
            "SESSION_FULL, This session is fully booked."
    })
    void redirectsAfterBookingWithExpectedMessage(BookingResult result, String message) {
        when(facade.bookSession(SUBJECT_ID, SESSION_ID, STUDENT_ID)).thenReturn(result);

        String view = controller.bookSession(SUBJECT_ID, SESSION_ID, redirect);

        assertEquals(SESSIONS_REDIRECT, view);
        assertEquals(message, redirect.getFlashAttributes().get("bookingMessage"));
    }

    @ParameterizedTest
    @CsvSource({
            "CANCELLED, Booking cancelled.",
            "SESSION_NOT_FOUND, Session not found.",
            "BOOKING_NOT_FOUND, No booking was found for this session."
    })
    void redirectsAfterCancellationWithExpectedMessage(CancellationResult result, String message) {
        when(facade.cancelBooking(SUBJECT_ID, SESSION_ID, STUDENT_ID)).thenReturn(result);

        String view = controller.cancelBooking(SUBJECT_ID, SESSION_ID, redirect);

        assertEquals(SESSIONS_REDIRECT, view);
        assertEquals(message, redirect.getFlashAttributes().get("bookingMessage"));
    }
}
