package com.example.grindschool.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tutors")
public class Tutor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    protected Tutor() {
    }

    public String getName() {
        return name;
    }
}
