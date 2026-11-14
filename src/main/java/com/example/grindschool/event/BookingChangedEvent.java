package com.example.grindschool.event;

public record BookingChangedEvent(Long studentId, Long sessionId, Action action) {

    public enum Action {
        BOOKED,
        CANCELLED
    }
}