package com.example.grindschool.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "subjects")
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    protected Subject() {
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}