package com.example.grindschool.facade;

import com.example.grindschool.entity.Booking;
import com.example.grindschool.entity.Session;
import com.example.grindschool.entity.SessionStatus;
import com.example.grindschool.entity.Student;
import com.example.grindschool.repository.BookingRepository;
import com.example.grindschool.repository.SessionRepository;
import com.example.grindschool.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class BookingFacade {

    private final SessionRepository sessionRepository;
    private final StudentRepository studentRepository;
    private final BookingRepository bookingRepository;

    public BookingFacade(
            SessionRepository sessionRepository,
            StudentRepository studentRepository,
            BookingRepository bookingRepository
    ) {
        this.sessionRepository = sessionRepository;
        this.studentRepository = studentRepository;
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    public BookingResult bookSession(Long subjectId, Long sessionId, Long studentId) {
        Session session = sessionRepository.findByIdForUpdate(sessionId).orElse(null);

        if (session == null || !subjectId.equals(session.getSubject().getId())) {
            return BookingResult.SESSION_NOT_FOUND;
        }

        LocalDateTime start = LocalDateTime.of(session.getDate(), session.getTime());
        if (session.getStatus() != SessionStatus.OPEN || !start.isAfter(LocalDateTime.now())) {
            return BookingResult.SESSION_CLOSED;
        }

        Student student = studentRepository.findById(studentId).orElse(null);
        if (student == null) {
            return BookingResult.STUDENT_NOT_FOUND;
        }

        if (bookingRepository.existsBySession_IdAndStudent_Id(sessionId, studentId)) {
            return BookingResult.ALREADY_BOOKED;
        }

        if (bookingRepository.countBySession_Id(sessionId) >= session.getTotalCapacity()) {
            return BookingResult.SESSION_FULL;
        }

        bookingRepository.save(new Booking(session, student));
        return BookingResult.BOOKED;
    }

    public enum BookingResult {
        BOOKED,
        SESSION_NOT_FOUND,
        SESSION_CLOSED,
        STUDENT_NOT_FOUND,
        ALREADY_BOOKED,
        SESSION_FULL
    }
}
