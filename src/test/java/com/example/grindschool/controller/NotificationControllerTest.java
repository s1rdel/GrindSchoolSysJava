package com.example.grindschool.controller;

import com.example.grindschool.service.BookingNotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class NotificationControllerTest {

    @Test
    void addsCurrentStudentsNotificationsToModel() {
        long studentId = 3L;
        BookingNotificationService service = mock(BookingNotificationService.class);
        List<String> notifications = List.of("Booking confirmed for session 2.");
        when(service.getRecentNotifications(studentId)).thenReturn(notifications);

        NotificationController controller = new NotificationController(service, studentId);
        ExtendedModelMap model = new ExtendedModelMap();

        assertEquals("notifications", controller.showNotifications(model));
        assertEquals(notifications, model.getAttribute("notifications"));
    }
}