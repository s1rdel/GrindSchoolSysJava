package com.example.grindschool.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class BookingAuditListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookingAuditListener.class);

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void logBookingChange(BookingChangedEvent event) {
        LOGGER.info("Booking action {} for student {} and session {}",
                event.action(),
                event.studentId(),
                event.sessionId()
        );
    }
}
