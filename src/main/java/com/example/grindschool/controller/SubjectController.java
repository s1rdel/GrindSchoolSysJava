package com.example.grindschool.controller;

import com.example.grindschool.entity.Subject;
import com.example.grindschool.facade.BookingFacade;
import com.example.grindschool.service.SessionService;
import com.example.grindschool.service.SubjectService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class SubjectController {

    private final SubjectService subjectService;
    private final SessionService sessionService;
    private final BookingFacade bookingFacade;
    private final Long demoStudentId;

    public SubjectController(SubjectService subjectService, SessionService sessionService, BookingFacade bookingFacade, @Value("${app.demo-student-id}") Long demoStudentId) {
        this.subjectService = subjectService;
        this.sessionService = sessionService;
        this.bookingFacade = bookingFacade;
        this.demoStudentId = demoStudentId;
    }

    @GetMapping("/subjects")
    public String showSubjects(Model model) {
        model.addAttribute("subjects", subjectService.getAllSubjects());
        return "subjects";
    }

    @GetMapping("/subjects/{id}/sessions")
    public String showSessions(@PathVariable("id") Long id, Model model) {
        Subject subject = subjectService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Subject not found"
                ));

        model.addAttribute("subject", subject);
        model.addAttribute("sessions", sessionService.getUpcomingSessions(id));
        model.addAttribute("bookedSessionIds", bookingFacade.getBookedSessionIds(demoStudentId, id));
        return "sessions";
    }
}
