package com.example.grindschool.event;

import com.example.grindschool.event.BookingChangedEvent.Action;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(OutputCaptureExtension.class)
class BookingAuditListenerTest {

    private final BookingAuditListener listener = new BookingAuditListener();

    @ParameterizedTest
    @EnumSource(Action.class)
    void logsActionWithStudentAndSessionIds(Action action, CapturedOutput output) {
        listener.logBookingChange(new BookingChangedEvent(7L, 42L, action));

        assertThat(output.getOut()).contains("Booking action " + action + " for student 7 and session 42");
    }
}
