package com.example.grindschool.service;

import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BookingNotificationServiceTest {

    private final JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
    private final BookingNotificationService service = new BookingNotificationService(jdbcTemplate);

    @Test
    void returnsNotificationsForRequestedStudent() {
        long studentId = 7L;
        List<String> notifications = List.of(
                "Booking cancelled for session 42.",
                "Booking confirmed for session 42."
        );
        when(jdbcTemplate.queryForList(anyString(), eq(String.class), eq(studentId)))
                .thenReturn(notifications);

        assertEquals(notifications, service.getRecentNotifications(studentId));

        verify(jdbcTemplate).queryForList(anyString(), eq(String.class), eq(studentId));
    }
}