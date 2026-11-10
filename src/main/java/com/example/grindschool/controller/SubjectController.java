package com.example.grindschool.controller;

import com.example.grindschool.service.SubjectService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SubjectController {

    private final SubjectService subjectService;

    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @GetMapping("/subjects")
    public String showSubjects(Model model) {
        model.addAttribute("subjects", subjectService.getAllSubjects());
        return "subjects";
    }
}
