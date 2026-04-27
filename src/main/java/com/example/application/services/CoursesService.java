package com.example.application.services;

import com.example.application.data.Courses;
import com.example.application.data.CoursesRepository;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class CoursesService {

    private final CoursesRepository repository;

    public CoursesService(CoursesRepository repository) {
        this.repository = repository;
    }

    public Optional<Courses> get(Long id) {
        return repository.findById(id);
    }

    public Courses save(Courses entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<Courses> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Courses> list(Pageable pageable, Specification<Courses> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
