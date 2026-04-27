package com.example.application.services;

import com.example.application.data.Students;
import com.example.application.data.StudentsRepository;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class StudentsService {

    private final StudentsRepository repository;

    public StudentsService(StudentsRepository repository) {
        this.repository = repository;
    }

    public Optional<Students> get(Long id) {
        return repository.findById(id);
    }

    public Students save(Students entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<Students> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Students> list(Pageable pageable, Specification<Students> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
