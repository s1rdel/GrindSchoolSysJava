package com.example.grindschool.service;

import com.example.grindschool.entity.Subject;
import com.example.grindschool.repository.SubjectRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SubjectServiceTest {

    private final SubjectRepository repository = mock(SubjectRepository.class);
    private final SubjectService service = new SubjectService(repository);

    @Test
    void requestsSubjectsSortedByName() {
        List<Subject> subjects = List.of(mock(Subject.class));
        when(repository.findAll(Sort.by("name"))).thenReturn(subjects);

        assertEquals(subjects, service.getAllSubjects());
    }

    @Test
    void returnsSubjectForRequestedId() {
        Subject subject = mock(Subject.class);
        when(repository.findById(7L)).thenReturn(Optional.of(subject));

        assertEquals(Optional.of(subject), service.findById(7L));
    }

    @Test
    void returnsEmptyForUnknownSubject() {
        when(repository.findById(8L)).thenReturn(Optional.empty());

        assertEquals(Optional.empty(), service.findById(8L));
    }
}