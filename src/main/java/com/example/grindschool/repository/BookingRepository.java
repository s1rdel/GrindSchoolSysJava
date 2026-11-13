package com.example.grindschool.repository;

import com.example.grindschool.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    boolean existsBySession_IdAndStudent_Id(Long sessionId, Long studentId);

    long countBySession_Id(Long sessionId);
}
