package com.example.grindschool.repository;

import com.example.grindschool.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    boolean existsBySession_IdAndStudent_Id(Long sessionId, Long studentId);

    long countBySession_Id(Long sessionId);

    Optional<Booking> findBySession_IdAndStudent_Id(Long sessionId, Long studentId);

    @Query("""
        select b.session.id
        from Booking b
        where b.student.id = :studentId
          and b.session.subject.id = :subjectId
        """)
    Set<Long> findBookedSessionIds(
            @Param("studentId") Long studentId,
            @Param("subjectId") Long subjectId
    );
}
