package com.example.application.services;

import com.example.application.data.Teachers;
import com.example.application.data.TeacherSearchCriteria;
import com.example.application.data.CoursesRepository;
import com.example.application.data.TeachersRepository;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TeachersService {

    private final TeachersRepository repository;
    private final CoursesRepository coursesRepository;

    public TeachersService(TeachersRepository repository, CoursesRepository coursesRepository) {
        this.repository = repository;
        this.coursesRepository = coursesRepository;
    }

    public Optional<Teachers> get(Long id) {
        return repository.findById(id);
    }

    public Teachers save(Teachers entity) {
        return repository.save(entity);
    }

    @Transactional
    public void delete(Long id) {
        coursesRepository.clearTeacherCourses(id);
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

    /**
     * Search teachers using Criteria API with dynamic predicates
     */
    public Page<Teachers> search(TeacherSearchCriteria criteria, Pageable pageable) {
        return repository.searchByCriteria(criteria, pageable);
    }

}
