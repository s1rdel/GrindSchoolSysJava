package com.example.grindschool.controller;

import com.example.grindschool.entity.Subject;
import com.example.grindschool.service.SessionService;
import com.example.grindschool.service.SubjectService;
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

    public SubjectController(SubjectService subjectService, SessionService sessionService) {
        this.subjectService = subjectService;
        this.sessionService = sessionService;
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
        return "sessions";
    }
}
