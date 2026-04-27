package com.example.application.services;

import com.example.application.data.Teachers;
import com.example.application.data.TeachersRepository;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class TeachersService {

    private final TeachersRepository repository;

    public TeachersService(TeachersRepository repository) {
        this.repository = repository;
    }

    public Optional<Teachers> get(Long id) {
        return repository.findById(id);
    }

    public Teachers save(Teachers entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<Teachers> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Teachers> list(Pageable pageable, Specification<Teachers> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
