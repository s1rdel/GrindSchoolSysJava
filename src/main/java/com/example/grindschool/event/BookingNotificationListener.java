package com.example.grindschool.event;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class BookingNotificationListener {

    private final JdbcTemplate jdbcTemplate;

    public BookingNotificationListener(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveNotification(BookingChangedEvent event) {
        String message = switch (event.action()) {
            case BOOKED -> "Booking confirmed for session " + event.sessionId() + ".";
            case CANCELLED -> "Booking cancelled for session " + event.sessionId() + ".";
        };

        jdbcTemplate.update("INSERT INTO booking_notifications (student_id, message) VALUES (?, ?)",
                event.studentId(),
                message);
    }
}
