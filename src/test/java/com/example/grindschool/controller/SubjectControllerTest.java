package com.example.grindschool.controller;

import com.example.grindschool.entity.Session;
import com.example.grindschool.entity.Subject;
import com.example.grindschool.facade.BookingFacade;
import com.example.grindschool.service.SessionService;
import com.example.grindschool.service.SubjectService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class SubjectControllerTest {

    private static final long SUBJECT_ID = 1L;
    private static final long STUDENT_ID = 3L;

    private final SubjectService subjectService = mock(SubjectService.class);
    private final SessionService sessionService = mock(SessionService.class);
    private final BookingFacade bookingFacade = mock(BookingFacade.class);
    private final SubjectController controller =
            new SubjectController(subjectService, sessionService, bookingFacade, STUDENT_ID);
    private final ExtendedModelMap model = new ExtendedModelMap();

    @Test
    void addsSubjectsToModel() {
        List<Subject> subjects = List.of(mock(Subject.class));
        when(subjectService.getAllSubjects()).thenReturn(subjects);

        assertEquals("subjects", controller.showSubjects(model));
        assertEquals(subjects, model.getAttribute("subjects"));
    }

    @Test
    void addsUpcomingSessionsAndBookedIdsForSelectedSubject() {
        Subject subject = mock(Subject.class);
        List<Session> sessions = List.of(mock(Session.class));
        Set<Long> bookedSessionIds = Set.of(2L);
        when(subjectService.findById(SUBJECT_ID)).thenReturn(Optional.of(subject));
        when(sessionService.getUpcomingSessions(SUBJECT_ID)).thenReturn(sessions);
        when(bookingFacade.getBookedSessionIds(STUDENT_ID, SUBJECT_ID)).thenReturn(bookedSessionIds);

        assertEquals("sessions", controller.showSessions(SUBJECT_ID, model));
        assertEquals(subject, model.getAttribute("subject"));
        assertEquals(sessions, model.getAttribute("sessions"));
        assertEquals(bookedSessionIds, model.getAttribute("bookedSessionIds"));
    }

    @Test
    void returnsNotFoundWhenSubjectIsMissing() {
        when(subjectService.findById(SUBJECT_ID)).thenReturn(Optional.empty());

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> controller.showSessions(SUBJECT_ID, model)
        );

        assertEquals(HttpStatus.NOT_FOUND, error.getStatusCode());
        verifyNoInteractions(sessionService, bookingFacade);
    }
}