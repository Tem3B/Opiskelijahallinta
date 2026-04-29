package com.example.application.data;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * Specification for dynamic search of Teachers based on TeacherSearchCriteria.
 */
public class TeacherSearchSpecification implements Specification<Teachers> {
    private final TeacherSearchCriteria criteria;

    public TeacherSearchSpecification(TeacherSearchCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<Teachers> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();

        if (criteria == null) {
            return cb.conjunction();
        }

        // Partial text search on firstName or lastName (OR condition)
        List<Predicate> nameOrPredicates = new ArrayList<>();

        if (criteria.getFirstName() != null && !criteria.getFirstName().isEmpty()) {
            nameOrPredicates.add(cb.like(
                cb.lower(root.get("firstName")),
                "%" + criteria.getFirstName().toLowerCase() + "%"
            ));
        }

        if (criteria.getLastName() != null && !criteria.getLastName().isEmpty()) {
            nameOrPredicates.add(cb.like(
                cb.lower(root.get("lastName")),
                "%" + criteria.getLastName().toLowerCase() + "%"
            ));
        }

        // Add OR condition if both firstName and lastName are provided
        if (!nameOrPredicates.isEmpty()) {
            predicates.add(cb.or(nameOrPredicates.toArray(new Predicate[0])));
        }

        // Partial text search on email
        if (criteria.getEmail() != null && !criteria.getEmail().isEmpty()) {
            predicates.add(cb.like(
                cb.lower(root.get("email")),
                "%" + criteria.getEmail().toLowerCase() + "%"
            ));
        }


        // JOIN with Courses and search by course name
        if (criteria.getCourseName() != null && !criteria.getCourseName().isEmpty()) {
            Join<Teachers, Courses> courseJoin = root.join("courses");
            predicates.add(cb.like(
                cb.lower(courseJoin.get("name")),
                "%" + criteria.getCourseName().toLowerCase() + "%"
            ));
            // Distinct to avoid cartesian product
            query.distinct(true);
        }

        // Combine all predicates with AND
        return cb.and(predicates.toArray(new Predicate[0]));
    }
}

