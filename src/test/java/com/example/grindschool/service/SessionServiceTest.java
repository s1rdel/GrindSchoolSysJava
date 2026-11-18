package com.example.grindschool.service;

import com.example.grindschool.entity.Session;
import com.example.grindschool.entity.SessionStatus;
import com.example.grindschool.entity.Subject;
import com.example.grindschool.entity.Tutor;
import com.example.grindschool.repository.SessionRepository;
import com.example.grindschool.repository.SubjectRepository;
import com.example.grindschool.repository.TutorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    private static final long SUBJECT_ID = 1L;
    private static final long TUTOR_ID = 2L;
    private static final LocalDate DATE = LocalDate.of(2030, 6, 15);
    private static final LocalTime TIME = LocalTime.of(14, 30);

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private SubjectRepository subjectRepository;

    @Mock
    private TutorRepository tutorRepository;

    @Mock
    private Subject subject;

    @Mock
    private Tutor tutor;

    private SessionService service;

    @BeforeEach
    void setUp() {
        service = new SessionService(sessionRepository, subjectRepository, tutorRepository);
    }

    @Test
    void createsAndSavesSessionWithRequestedDetails() {
        givenSubjectAndTutor();
        when(sessionRepository.save(any(Session.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Session session = service.createSession(SUBJECT_ID, TUTOR_ID, DATE, TIME, 3);

        verify(sessionRepository).save(session);
        assertAll(
                () -> assertSame(subject, session.getSubject()),
                () -> assertSame(tutor, session.getTutor()),
                () -> assertEquals(DATE, session.getDate()),
                () -> assertEquals(TIME, session.getTime()),
                () -> assertEquals(3, session.getTotalCapacity()),
                () -> assertEquals(SessionStatus.OPEN, session.getStatus())
        );
    }

    @Test
    void rejectsUnknownSubjectWithoutSaving() {
        when(subjectRepository.findById(SUBJECT_ID)).thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> service.createSession(SUBJECT_ID, TUTOR_ID, DATE, TIME, 3)
        );

        verifyNoInteractions(tutorRepository, sessionRepository);
    }

    @Test
    void rejectsUnknownTutorWithoutSaving() {
        when(subjectRepository.findById(SUBJECT_ID)).thenReturn(Optional.of(subject));
        when(tutorRepository.findById(TUTOR_ID)).thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> service.createSession(SUBJECT_ID, TUTOR_ID, DATE, TIME, 3)
        );

        verifyNoInteractions(sessionRepository);
    }

    @Test
    void rejectsZeroCapacityWithoutSaving() {
        givenSubjectAndTutor();

        assertThrows(
                IllegalStateException.class,
                () -> service.createSession(SUBJECT_ID, TUTOR_ID, DATE, TIME, 0)
        );

        verifyNoInteractions(sessionRepository);
    }

    private void givenSubjectAndTutor() {
        when(subjectRepository.findById(SUBJECT_ID)).thenReturn(Optional.of(subject));
        when(tutorRepository.findById(TUTOR_ID)).thenReturn(Optional.of(tutor));
    }

    @Test
    void requestsOpenSessionsForSelectedSubjectFromCurrentTime() {
        List<Session> sessions = List.of(mock(Session.class));
        when(sessionRepository.findUpcomingBySubject(
                eq(SUBJECT_ID), eq(SessionStatus.OPEN), any(LocalDate.class), any(LocalTime.class)
        )).thenReturn(sessions);
        ArgumentCaptor<LocalDate> day = ArgumentCaptor.forClass(LocalDate.class);
        ArgumentCaptor<LocalTime> time = ArgumentCaptor.forClass(LocalTime.class);

        LocalDateTime before = LocalDateTime.now().withNano(0);
        List<Session> result = service.getUpcomingSessions(SUBJECT_ID);
        LocalDateTime after = LocalDateTime.now().withNano(0);

        verify(sessionRepository).findUpcomingBySubject(
                eq(SUBJECT_ID), eq(SessionStatus.OPEN), day.capture(), time.capture()
        );
        LocalDateTime cutoff = LocalDateTime.of(day.getValue(), time.getValue());
        assertEquals(sessions, result);
        assertEquals(0, cutoff.getNano());
        assertTrue(!cutoff.isBefore(before) && !cutoff.isAfter(after));
    }
}