package com.example.grindschool.service;

import com.example.grindschool.entity.*;
import com.example.grindschool.repository.SessionRepository;
import com.example.grindschool.repository.SubjectRepository;
import com.example.grindschool.repository.TutorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class SessionService {

    private final SessionRepository sessionRepository;
    private final SubjectRepository subjectRepository;
    private final TutorRepository tutorRepository;

    public SessionService(SessionRepository sessionRepository, SubjectRepository subjectRepository, TutorRepository tutorRepository) {
        this.sessionRepository = sessionRepository;
        this.subjectRepository = subjectRepository;
        this.tutorRepository = tutorRepository;
    }

    public List<Session> getUpcomingSessions(Long subjectId) {
        LocalDateTime now = LocalDateTime.now();

        return sessionRepository.findUpcomingBySubject(
                subjectId,
                SessionStatus.OPEN,
                now.toLocalDate(),
                now.toLocalTime().withNano(0)
        );
    }

    @Transactional
    public Session createSession(Long subjectId, Long tutorId, LocalDate date, LocalTime time, int totalCapacity) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new IllegalArgumentException("Subject not found."));

        Tutor tutor = tutorRepository.findById(tutorId)
                .orElseThrow(() -> new IllegalArgumentException("Tutor not found."));

        Session session = new SessionBuilder()
                .subject(subject)
                .tutor(tutor)
                .date(date)
                .time(time)
                .capacity(totalCapacity)
                .build();

        return sessionRepository.save(session);
    }
}
