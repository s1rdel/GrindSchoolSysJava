package com.example.grindschool.service;

import com.example.grindschool.entity.Session;
import com.example.grindschool.entity.SessionStatus;
import com.example.grindschool.repository.SessionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SessionService {

    private final SessionRepository sessionRepository;

    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
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
}
