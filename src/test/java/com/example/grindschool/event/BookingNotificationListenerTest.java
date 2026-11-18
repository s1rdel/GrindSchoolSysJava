package com.example.grindschool.event;

import com.example.grindschool.event.BookingChangedEvent.Action;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class BookingNotificationListenerTest {

    private final JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
    private final BookingNotificationListener listener = new BookingNotificationListener(jdbcTemplate);

    @ParameterizedTest
    @CsvSource({
            "BOOKED, Booking confirmed for session 42.",
            "CANCELLED, Booking cancelled for session 42."
    })
    void writesNotificationForCorrectStudentAndSession(Action action, String expectedMessage) {
        listener.saveNotification(new BookingChangedEvent(7L, 42L, action));

        verify(jdbcTemplate).update(
                "INSERT INTO booking_notifications (student_id, message) VALUES (?, ?)",
                7L,
                expectedMessage
        );
    }
}
