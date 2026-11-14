package com.example.grindschool.controller;

import com.example.grindschool.service.BookingNotificationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NotificationController {

    private final BookingNotificationService notificationService;
    private final Long demoStudentId;

    public NotificationController(
            BookingNotificationService notificationService,
            @Value("${app.demo-student-id}") Long demoStudentId
    ) {
        this.notificationService = notificationService;
        this.demoStudentId = demoStudentId;
    }

    @GetMapping("/notifications")
    public String showNotifications(Model model) {
        model.addAttribute(
                "notifications",
                notificationService.getRecentNotifications(demoStudentId)
        );
        return "notifications";
    }
}
