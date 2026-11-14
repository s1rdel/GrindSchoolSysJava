package com.example.grindschool.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingNotificationService {

    private final JdbcTemplate jdbcTemplate;

    public BookingNotificationService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<String> getRecentNotifications(Long studentId) {
        return jdbcTemplate.queryForList("""
                SELECT message
                FROM booking_notifications
                WHERE student_id = ?
                ORDER BY id DESC
                LIMIT 20
                """, String.class, studentId);
    }
}