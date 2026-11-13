package com.example.grindschool.service;

import com.example.grindschool.entity.Subject;
import com.example.grindschool.repository.SubjectRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubjectService {

    private final SubjectRepository repository;

    public SubjectService(SubjectRepository repository) {
        this.repository = repository;
    }

    public List<Subject> getAllSubjects() {
        return repository.findAll(Sort.by("name"));
    }

    public Optional<Subject> findById(Long id) {
        return repository.findById(id);
    }
}
