package com.example.grindschool.controller;

import com.example.grindschool.facade.BookingFacade;
import com.example.grindschool.facade.BookingFacade.BookingResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class BookingController {

    private final BookingFacade bookingFacade;
    private final Long demoStudentId;

    public BookingController(
            BookingFacade bookingFacade,
            @Value("${app.demo-student-id}") Long demoStudentId
    ) {
        this.bookingFacade = bookingFacade;
        this.demoStudentId = demoStudentId;
    }

    @PostMapping("/subjects/{subjectId}/sessions/{sessionId}/book")
    public String bookSession(
            @PathVariable("subjectId") Long subjectId,
            @PathVariable("sessionId") Long sessionId,
            RedirectAttributes redirectAttributes
    ) {
        BookingResult result = bookingFacade.bookSession(subjectId, sessionId, demoStudentId);

        String message = switch (result) {
            case BOOKED -> "Booking confirmed.";
            case SESSION_NOT_FOUND -> "Session not found.";
            case SESSION_CLOSED -> "This session is no longer open for booking.";
            case STUDENT_NOT_FOUND -> "Demo student not found. Check the configured ID.";
            case ALREADY_BOOKED -> "You already booked this session.";
            case SESSION_FULL -> "This session is fully booked.";
        };

        redirectAttributes.addFlashAttribute("bookingMessage", message);
        return "redirect:/subjects/" + subjectId + "/sessions";
    }

    @PostMapping("/subjects/{subjectId}/sessions/{sessionId}/cancel")
    public String cancelBooking(
            @PathVariable("subjectId") Long subjectId,
            @PathVariable("sessionId") Long sessionId,
            RedirectAttributes redirectAttributes
    ) {
        BookingFacade.CancellationResult result =
                bookingFacade.cancelBooking(subjectId, sessionId, demoStudentId);

        String message = switch (result) {
            case CANCELLED -> "Booking cancelled.";
            case SESSION_NOT_FOUND -> "Session not found.";
            case BOOKING_NOT_FOUND -> "No booking was found for this session.";
        };

        redirectAttributes.addFlashAttribute("bookingMessage", message);
        return "redirect:/subjects/" + subjectId + "/sessions";
    }
}
