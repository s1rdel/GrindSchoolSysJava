package com.example.grindschool.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "sessions")
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tutor_id", nullable = false)
    private Tutor tutor;

    @Column(name = "session_date", nullable = false)
    private LocalDate date;

    @Column(name = "session_time", nullable = false)
    private LocalTime time;

    @Column(name = "total_capacity", nullable = false)
    private int totalCapacity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SessionStatus status;

    protected Session() {
    }

    Session(
            Subject subject,
            Tutor tutor,
            LocalDate date,
            LocalTime time,
            int totalCapacity,
            SessionStatus status
    ) {
        this.subject = subject;
        this.tutor = tutor;
        this.date = date;
        this.time = time;
        this.totalCapacity = totalCapacity;
        this.status = status;
    }

    public Tutor getTutor() {
        return tutor;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTime() {
        return time;
    }

    public int getTotalCapacity() {
        return totalCapacity;
    }

    public Long getId() {
        return id;
    }

    public Subject getSubject() {
        return subject;
    }

    public SessionStatus getStatus() {
        return status;
    }
}