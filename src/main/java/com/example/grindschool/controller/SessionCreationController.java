package com.example.grindschool.controller;

import com.example.grindschool.repository.TutorRepository;
import com.example.grindschool.service.SessionService;
import com.example.grindschool.service.SubjectService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Controller
@RequestMapping("/subjects/{subjectId}/sessions/new")
public class SessionCreationController {

    private final SubjectService subjectService;
    private final SessionService sessionService;
    private final TutorRepository tutorRepository;

    public SessionCreationController(
            SubjectService subjectService,
            SessionService sessionService,
            TutorRepository tutorRepository
    ) {
        this.subjectService = subjectService;
        this.sessionService = sessionService;
        this.tutorRepository = tutorRepository;
    }

    @GetMapping
    public String showForm(@PathVariable("subjectId") Long subjectId, Model model) {
        model.addAttribute("sessionForm", new SessionForm(null, null, null, null));
        return renderForm(subjectId, model);
    }

    @PostMapping
    public String createSession(
            @PathVariable("subjectId") Long subjectId,
            @Valid @ModelAttribute("sessionForm") SessionForm form,
            BindingResult errors,
            Model model
    ) {
        if (errors.hasErrors()) {
            return renderForm(subjectId, model);
        }

        if (!LocalDateTime.of(form.date(), form.time()).isAfter(LocalDateTime.now())) {
            errors.rejectValue("date", "session.past", "Choose a future date and time.");
            return renderForm(subjectId, model);
        }

        try {
            sessionService.createSession(
                    subjectId, form.tutorId(), form.date(), form.time(), form.totalCapacity()
            );
        } catch (IllegalArgumentException ex) {
            errors.reject("session.creation", ex.getMessage());
            return renderForm(subjectId, model);
        }

        return "redirect:/subjects/" + subjectId + "/sessions";
    }

    private String renderForm(Long subjectId, Model model) {
        model.addAttribute("subject", subjectService.findById(subjectId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Subject not found"
                )));
        model.addAttribute("tutors", tutorRepository.findAll(Sort.by("name")));
        return "create-session";
    }

    public record SessionForm(
            @NotNull @Positive Long tutorId,
            @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time,
            @NotNull @Positive Integer totalCapacity
    ) {
    }
}
