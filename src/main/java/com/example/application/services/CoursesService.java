package com.example.application.services;

import com.example.application.data.Courses;
import com.example.application.data.CoursesRepository;
import com.example.application.data.StudentsRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CoursesService {

    private final CoursesRepository repository;
    private final StudentsRepository studentsRepository;

    public CoursesService(CoursesRepository repository, StudentsRepository studentsRepository) {
        this.repository = repository;
        this.studentsRepository = studentsRepository;
    }

    public Optional<Courses> get(Long id) {
        return repository.findById(id);
    }

    public Courses save(Courses entity) {
        return repository.save(entity);
    }

    @Transactional
    public void delete(Long id) {
        repository.findById(id).ifPresent(course -> {
            studentsRepository.findAll().forEach(student -> student.getCourses().removeIf(existingCourse -> existingCourse.getId().equals(id)));
            repository.delete(course);
        });
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

    public List<Courses> findByTeacherId(Long teacherId) {
        return repository.findAll().stream()
                .filter(course -> course.getTeacher() != null && course.getTeacher().getId().equals(teacherId))
                .toList();
    }

}
