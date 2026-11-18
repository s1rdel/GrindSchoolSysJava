package com.example.grindschool.controller;

import com.example.grindschool.controller.SessionCreationController.SessionForm;
import com.example.grindschool.entity.Subject;
import com.example.grindschool.entity.Tutor;
import com.example.grindschool.repository.TutorRepository;
import com.example.grindschool.service.SessionService;
import com.example.grindschool.service.SubjectService;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class SessionCreationControllerTest {

    private static final long SUBJECT_ID = 1L;
    private static final long TUTOR_ID = 2L;

    private final SubjectService subjectService = mock(SubjectService.class);
    private final SessionService sessionService = mock(SessionService.class);
    private final TutorRepository tutorRepository = mock(TutorRepository.class);
    private final SessionCreationController controller =
            new SessionCreationController(subjectService, sessionService, tutorRepository);
    private final ExtendedModelMap model = new ExtendedModelMap();

    @Test
    void opensFormWithSubjectAndTutors() {
        Subject subject = mock(Subject.class);
        List<Tutor> tutors = List.of(mock(Tutor.class));
        when(subjectService.findById(SUBJECT_ID)).thenReturn(Optional.of(subject));
        when(tutorRepository.findAll(Sort.by("name"))).thenReturn(tutors);

        assertEquals("create-session", controller.showForm(SUBJECT_ID, model));
        assertEquals(new SessionForm(null, null, null, null), model.getAttribute("sessionForm"));
        assertEquals(subject, model.getAttribute("subject"));
        assertEquals(tutors, model.getAttribute("tutors"));
    }

    @Test
    void returnsNotFoundWhenSubjectIsMissing() {
        when(subjectService.findById(SUBJECT_ID)).thenReturn(Optional.empty());

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> controller.showForm(SUBJECT_ID, model)
        );

        assertEquals(HttpStatus.NOT_FOUND, error.getStatusCode());
        verifyNoInteractions(tutorRepository);
    }

    @Test
    void submitsValidFormAndRedirects() {
        SessionForm form = formFor(LocalDate.now().plusDays(1));
        var errors = new BeanPropertyBindingResult(form, "sessionForm");

        String view = controller.createSession(SUBJECT_ID, form, errors, model);

        assertEquals("redirect:/subjects/1/sessions", view);
        assertFalse(errors.hasErrors());
        verify(sessionService).createSession(SUBJECT_ID, TUTOR_ID, form.date(), form.time(), 3);
    }

    @Test
    void redisplaysFormWhenBindingAlreadyHasErrors() {
        givenSubject();
        SessionForm form = new SessionForm(TUTOR_ID, LocalDate.now().plusDays(1), LocalTime.NOON, 0);
        var errors = new BeanPropertyBindingResult(form, "sessionForm");
        errors.rejectValue("totalCapacity", "Positive");

        assertEquals("create-session", controller.createSession(SUBJECT_ID, form, errors, model));
        assertEquals("Positive", errors.getFieldError("totalCapacity").getCode());
        verifyNoInteractions(sessionService);
    }

    @Test
    void rejectsPastDateWithoutCreatingSession() {
        givenSubject();
        SessionForm form = formFor(LocalDate.now().minusDays(1));
        var errors = new BeanPropertyBindingResult(form, "sessionForm");

        assertEquals("create-session", controller.createSession(SUBJECT_ID, form, errors, model));
        assertEquals("session.past", errors.getFieldError("date").getCode());
        verifyNoInteractions(sessionService);
    }

    @Test
    void displaysServiceErrorOnForm() {
        givenSubject();
        SessionForm form = formFor(LocalDate.now().plusDays(1));
        var errors = new BeanPropertyBindingResult(form, "sessionForm");
        when(sessionService.createSession(SUBJECT_ID, TUTOR_ID, form.date(), form.time(), 3))
                .thenThrow(new IllegalArgumentException("Tutor not found."));

        assertEquals("create-session", controller.createSession(SUBJECT_ID, form, errors, model));
        assertEquals("session.creation", errors.getGlobalError().getCode());
        assertEquals("Tutor not found.", errors.getGlobalError().getDefaultMessage());
    }

    private void givenSubject() {
        when(subjectService.findById(SUBJECT_ID)).thenReturn(Optional.of(mock(Subject.class)));
    }

    private SessionForm formFor(LocalDate date) {
        return new SessionForm(TUTOR_ID, date, LocalTime.NOON, 3);
    }
}