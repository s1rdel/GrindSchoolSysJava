package com.example.grindschool.entity;

import java.time.LocalDate;
import java.time.LocalTime;

public class SessionBuilder {

    private Subject subject;
    private Tutor tutor;
    private LocalDate date;
    private LocalTime time;
    private int totalCapacity;
    private SessionStatus status = SessionStatus.OPEN;

    public SessionBuilder subject(Subject subject) {
        this.subject = subject;
        return this;
    }

    public SessionBuilder tutor(Tutor tutor) {
        this.tutor = tutor;
        return this;
    }

    public SessionBuilder date(LocalDate date) {
        this.date = date;
        return this;
    }

    public SessionBuilder time(LocalTime time) {
        this.time = time;
        return this;
    }

    public SessionBuilder capacity(int totalCapacity) {
        this.totalCapacity = totalCapacity;
        return this;
    }

    public SessionBuilder status(SessionStatus status) {
        this.status = status;
        return this;
    }

    public Session build() {
        if (subject == null || tutor == null || date == null || time == null || status == null) {
            throw new IllegalStateException("Subject, tutor, date, time and status are required.");
        }

        if (totalCapacity < 1) {
            throw new IllegalStateException("Session capacity must be positive.");
        }

        return new Session(subject, tutor, date, time, totalCapacity, status);
    }
}
