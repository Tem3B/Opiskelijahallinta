package com.example.application.data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TeachersRepository extends JpaRepository<Teachers, Long>, JpaSpecificationExecutor<Teachers> {

    /**
     * Search teachers
     */
    default Page<Teachers> searchByCriteria(TeacherSearchCriteria criteria, Pageable pageable) {
        return findAll(new TeacherSearchSpecification(criteria), pageable);
    }
}

