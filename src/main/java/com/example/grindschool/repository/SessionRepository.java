package com.example.grindschool.repository;

import com.example.grindschool.entity.Session;
import com.example.grindschool.entity.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {

    @Query("""
            select s from Session s
            join fetch s.tutor
            where s.subject.id = :subjectId
              and s.status = :status
              and (s.date > :today or (s.date = :today and s.time > :now))
            order by s.date, s.time
            """)
    List<Session> findUpcomingBySubject(
            @Param("subjectId") Long subjectId,
            @Param("status") SessionStatus status,
            @Param("today") LocalDate today,
            @Param("now") LocalTime now
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Session s where s.id = :id")
    Optional<Session> findByIdForUpdate(@Param("id") Long id);
}
