package com.example.grindschool.facade;

import com.example.grindschool.entity.Booking;
import com.example.grindschool.entity.Session;
import com.example.grindschool.entity.SessionBuilder;
import com.example.grindschool.entity.SessionStatus;
import com.example.grindschool.entity.Student;
import com.example.grindschool.entity.Subject;
import com.example.grindschool.entity.Tutor;
import com.example.grindschool.event.BookingChangedEvent;
import com.example.grindschool.facade.BookingFacade.BookingResult;
import com.example.grindschool.facade.BookingFacade.CancellationResult;
import com.example.grindschool.repository.BookingRepository;
import com.example.grindschool.repository.SessionRepository;
import com.example.grindschool.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingFacadeTest {

    private static final long SUBJECT_ID = 1L;
    private static final long OTHER_SUBJECT_ID = 2L;
    private static final long STUDENT_ID = 3L;
    private static final long SESSION_ID = 4L;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private Subject subject;

    @Mock
    private Tutor tutor;

    @Mock
    private Student student;

    private BookingFacade facade;

    @BeforeEach
    void setUp() {
        facade = new BookingFacade(
                sessionRepository, studentRepository, bookingRepository, eventPublisher
        );
    }

    @Test
    void booksLastPlaceAndPublishesEventAfterSaving() {
        givenOpenSession(2);
        when(studentRepository.findById(STUDENT_ID)).thenReturn(Optional.of(student));
        when(bookingRepository.existsBySession_IdAndStudent_Id(SESSION_ID, STUDENT_ID)).thenReturn(false);
        when(bookingRepository.countBySession_Id(SESSION_ID)).thenReturn(1L);

        assertEquals(BookingResult.BOOKED, facade.bookSession(SUBJECT_ID, SESSION_ID, STUDENT_ID));

        InOrder order = inOrder(bookingRepository, eventPublisher);
        order.verify(bookingRepository).save(any(Booking.class));
        order.verify(eventPublisher).publishEvent(
                new BookingChangedEvent(STUDENT_ID, SESSION_ID, BookingChangedEvent.Action.BOOKED)
        );
    }

    @Test
    void rejectsMissingSession() {
        when(sessionRepository.findByIdForUpdate(SESSION_ID)).thenReturn(Optional.empty());

        assertEquals(BookingResult.SESSION_NOT_FOUND, facade.bookSession(SUBJECT_ID, SESSION_ID, STUDENT_ID));
        verifyNoInteractions(studentRepository, bookingRepository, eventPublisher);
    }

    @Test
    void rejectsSessionFromDifferentSubject() {
        givenOpenSession(1);

        assertEquals(BookingResult.SESSION_NOT_FOUND, facade.bookSession(OTHER_SUBJECT_ID, SESSION_ID, STUDENT_ID));
        verifyNoInteractions(studentRepository, bookingRepository, eventPublisher);
    }

    @Test
    void rejectsCancelledSession() {
        givenSession(SessionStatus.CANCELLED, LocalDate.now().plusDays(1), 1);

        assertEquals(BookingResult.SESSION_CLOSED, facade.bookSession(SUBJECT_ID, SESSION_ID, STUDENT_ID));
        verifyNoInteractions(studentRepository, bookingRepository, eventPublisher);
    }

    @Test
    void rejectsPastSession() {
        givenSession(SessionStatus.OPEN, LocalDate.now().minusDays(1), 1);

        assertEquals(BookingResult.SESSION_CLOSED, facade.bookSession(SUBJECT_ID, SESSION_ID, STUDENT_ID));
        verifyNoInteractions(studentRepository, bookingRepository, eventPublisher);
    }

    @Test
    void rejectsUnknownStudent() {
        givenOpenSession(1);
        when(studentRepository.findById(STUDENT_ID)).thenReturn(Optional.empty());

        assertEquals(BookingResult.STUDENT_NOT_FOUND, facade.bookSession(SUBJECT_ID, SESSION_ID, STUDENT_ID));
        verifyNoInteractions(bookingRepository, eventPublisher);
    }

    @Test
    void rejectsDuplicateBooking() {
        givenOpenSession(2);
        when(studentRepository.findById(STUDENT_ID)).thenReturn(Optional.of(student));
        when(bookingRepository.existsBySession_IdAndStudent_Id(SESSION_ID, STUDENT_ID)).thenReturn(true);

        assertEquals(BookingResult.ALREADY_BOOKED, facade.bookSession(SUBJECT_ID, SESSION_ID, STUDENT_ID));
        verify(bookingRepository, never()).save(any(Booking.class));
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void rejectsBookingWhenNoPlacesRemain() {
        givenOpenSession(1);
        when(studentRepository.findById(STUDENT_ID)).thenReturn(Optional.of(student));
        when(bookingRepository.existsBySession_IdAndStudent_Id(SESSION_ID, STUDENT_ID)).thenReturn(false);
        when(bookingRepository.countBySession_Id(SESSION_ID)).thenReturn(1L);

        assertEquals(BookingResult.SESSION_FULL, facade.bookSession(SUBJECT_ID, SESSION_ID, STUDENT_ID));
        verify(bookingRepository, never()).save(any(Booking.class));
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void cancelsExistingBookingAndPublishesEventAfterDeleting() {
        Booking booking = new Booking(givenOpenSession(1), student);
        when(bookingRepository.findBySession_IdAndStudent_Id(SESSION_ID, STUDENT_ID))
                .thenReturn(Optional.of(booking));

        assertEquals(CancellationResult.CANCELLED, facade.cancelBooking(SUBJECT_ID, SESSION_ID, STUDENT_ID));

        InOrder order = inOrder(bookingRepository, eventPublisher);
        order.verify(bookingRepository).delete(booking);
        order.verify(eventPublisher).publishEvent(
                new BookingChangedEvent(STUDENT_ID, SESSION_ID, BookingChangedEvent.Action.CANCELLED)
        );
    }

    @Test
    void refusesCancellationWhenSessionDoesNotExist() {
        when(sessionRepository.findByIdForUpdate(SESSION_ID)).thenReturn(Optional.empty());

        assertEquals(CancellationResult.SESSION_NOT_FOUND, facade.cancelBooking(SUBJECT_ID, SESSION_ID, STUDENT_ID));
        verifyNoInteractions(bookingRepository, eventPublisher);
    }

    @Test
    void refusesCancellationWhenSessionBelongsToDifferentSubject() {
        givenOpenSession(1);

        assertEquals(
                CancellationResult.SESSION_NOT_FOUND,
                facade.cancelBooking(OTHER_SUBJECT_ID, SESSION_ID, STUDENT_ID)
        );
        verifyNoInteractions(bookingRepository, eventPublisher);
    }

    @Test
    void refusesCancellationWhenStudentHasNoBooking() {
        givenOpenSession(1);
        when(bookingRepository.findBySession_IdAndStudent_Id(SESSION_ID, STUDENT_ID))
                .thenReturn(Optional.empty());

        assertEquals(CancellationResult.BOOKING_NOT_FOUND, facade.cancelBooking(SUBJECT_ID, SESSION_ID, STUDENT_ID));
        verify(bookingRepository, never()).delete(any(Booking.class));
        verifyNoInteractions(eventPublisher);
    }

    private Session givenOpenSession(int capacity) {
        return givenSession(SessionStatus.OPEN, LocalDate.now().plusDays(1), capacity);
    }

    private Session givenSession(SessionStatus status, LocalDate date, int capacity) {
        Session session = new SessionBuilder()
                .subject(subject)
                .tutor(tutor)
                .date(date)
                .time(LocalTime.NOON)
                .capacity(capacity)
                .status(status)
                .build();

        when(subject.getId()).thenReturn(SUBJECT_ID);
        when(sessionRepository.findByIdForUpdate(SESSION_ID)).thenReturn(Optional.of(session));

        return session;
    }

    @Test
    void returnsBookedSessionIdsForStudentAndSubject() {
        Set<Long> bookedIds = Set.of(SESSION_ID);
        when(bookingRepository.findBookedSessionIds(STUDENT_ID, SUBJECT_ID)).thenReturn(bookedIds);

        assertEquals(bookedIds, facade.getBookedSessionIds(STUDENT_ID, SUBJECT_ID));
    }
}
